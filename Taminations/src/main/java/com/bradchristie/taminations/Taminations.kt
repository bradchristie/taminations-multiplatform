package com.bradchristie.taminations
/*

  Taminations Square Dance Animations
  Copyright (C) 2019 Brad Christie

  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Configuration.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker.PERMISSION_GRANTED
import android.util.DisplayMetrics
import com.bradchristie.taminations.Application.doRequest
import com.bradchristie.taminations.Taminations.Companion.context
import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.platform.LinearLayout
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.System.later
import com.bradchristie.taminations.platform.View
import com.bradchristie.taminations.platform.removeFromParent
import kotlin.properties.Delegates.notNull

class Taminations : Activity(), ActivityCompat.OnRequestPermissionsResultCallback
{

  companion object {
    private var myContext by notNull<Activity>()
    //  Following are retrieved when needed via get()
    //  because context is not available until after onCreate
    val context: Activity get() = myContext
    var onPermissionsSuccess:()->Unit = { }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    myContext = this
    setContentView(Application.buildDisplay().div)
    //  This handles Activity re-creation on device rotation
    if (!Application.goHere())
      //  Not a rotation, do normal startup
      doRequest(Request(Request.Action.STARTUP))
  }

  //  We only have one Activity, although to the user it looks like
  //  there are multiple screens.  So intercept the Back button
  //  to go to the previous screen
  override fun onBackPressed() {
    if (!Application.goBack())
      super.onBackPressed()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    if (grantResults.count() == 2 &&
        grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
      //  Got requested permissions
      onPermissionsSuccess()
    }
    // otherwise do nothing
  }

  override fun onResume() {
    super.onResume()
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    if (intent.action.contains("VIEW")) {
      @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
      doRequest(Request(intent.data.fragment))
    }
  }

}

actual object Application : Page() {

  private val metrics: DisplayMetrics get() = context.resources.displayMetrics
  private val config: Configuration get() = context.resources.configuration
  //  And these could change if the user rotates the device
  val density:Float get() = metrics.density
  //  not used val fontdensity:Float get() = metrics.scaledDensity
  private val tablet:Boolean get() = config.screenLayout and SCREENLAYOUT_SIZE_MASK >= SCREENLAYOUT_SIZE_LARGE
  actual val screenHeight:Int get() =
    if (tablet)
      (metrics.heightPixels / density).i
    else
      ((metrics.heightPixels max metrics.widthPixels) / density).i
  actual val isLandscape:Boolean get() = config.orientation == ORIENTATION_LANDSCAPE
  actual val isPortrait:Boolean get() = config.orientation == ORIENTATION_PORTRAIT
  actual val isTouch = true
  @SuppressLint("StaticFieldLeak")
  override val view = LinearLayout(LinearLayout.Direction.VERTICAL)
  @SuppressLint("StaticFieldLeak")
  actual val titleBar = TitleBar()
  private lateinit var contentPage: Page
  private val landscapePage get() = object : NavigationPage() {
    override val pages =
        listOf(FirstLandscapePage(),
            SecondLandscapePage(),
            SequencerPage(),
            StartPracticePage(),
            TutorialPage(),
            PracticePage(),
            DefinitionPage() //  only from practice page
        )
  }
  private val portraitPage get() = object : NavigationPage() {
    override val pages = listOf(
        LevelPage(),
        AboutPage(),
        CalllistPage(),
        AnimListPage(),
        AnimationPage(),
        DefinitionPage(),
        SettingsPage(),
        StartPracticePage(),
        TutorialPage(),
        PracticePage(),
        SequencerPage())
  }

  fun buildDisplay() : View = LinearLayout(LinearLayout.Direction.VERTICAL).apply {
    fillParent()
    //  Add the title bar
    //titleBar = TitleBar()
    titleBar.removeFromParent()
    appendView(titleBar) {
      weight = 1
    }
    //  View to hold content
    contentPage = if (isLandscape) landscapePage else portraitPage
    if (contentPage.view.parentView != null)
      contentPage.view.parentView!!.removeView(contentPage.view)
    appendView(contentPage.view) {
      weight = 9
    }
  }

  override fun doRequest(request: Request): Boolean {
    //  Hard-wire some pages for landscape
    if (request.action == Request.Action.PRACTICE ||
        request.action == Request.Action.TUTORIAL ||
        request.action == Request.Action.STARTPRACTICE)
      Taminations.context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    //  Definition page from Practice is landscape
      else if (request.action == Request.Action.DEFINITION &&
        hashes.count() > 1 &&
        Request(hashes[hashes.lastIndex-1]).action == Request.Action.PRACTICE)
      Taminations.context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    //  Other pages are always portrait on small devices
    else if (!tablet) {
      Taminations.context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    return contentPage.doRequest(request)
  }

  //  When we want to go to another page, we convert the request
  //  to a hash location and push it to a stack.
  //  All this is so "back" will go back to the previous page.
  //  Init the hash with STARTUP so we can go back to it
  private val hashes = mutableListOf(Request(Request.Action.STARTUP).s)
  actual fun sendRequest(request: Request) {
    //  If requesting a startup (logo was presses)
    //  then user wants to restart, clear out all old stuff
    if (request.action == Request.Action.STARTUP)
      hashes.clear()
    hashes.add(request.s)
    later {
      doRequest(request)
    }
  }
  actual fun sendRequest(action: Request.Action, vararg params:Pair<String,String>) {
    sendRequest(Request(action, *params))
  }

  //  Update the current location like sendRequest but don't
  //  actually send a request
  actual fun updateLocation(request: Request) {
    hashes.removeAt(hashes.lastIndex)
    hashes.add(request.s)
  }
  actual fun updateLocation(action: Request.Action, vararg params:Pair<String,String>) {
    updateLocation(Request(action,*params))
  }
  val location:String get() = hashes.last()

  //  Message is like a Request except it does not get saved
  //  in the location history and is not expected to
  //  directly trigger a page change
  override fun sendMessage(message: Request) {
    later {
      contentPage.sendMessage(message)
    }
  }

  //  This restores the current location
  //  Needed to handle device rotation
  fun goHere():Boolean =
      if (hashes.count() > 0) {
        doRequest(Request(hashes.last()))
        true  //  we handled the request
      } else
        false  // we did not handle the request


  actual fun goBack():Boolean =
    //  The top hash is the current page
    //  So to go back, we have to pop that and then look at the next one
    if (hashes.count() > 1) {
      hashes.removeAt(hashes.lastIndex)
      doRequest(Request(hashes.last()))
      true  //  we handled the request
    } else
      false  // we did not handle the request

}