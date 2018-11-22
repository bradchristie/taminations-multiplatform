package com.bradchristie.taminations
/*

  Taminations Square Dance Animations for Web Browsers
  Copyright (C) 2018 Brad Christie

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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.platform.*
import org.w3c.dom.COMPLETE
import org.w3c.dom.DocumentReadyState
import org.w3c.dom.HTMLBodyElement
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {

  TamUtils.waitForInit {
    if (document.readyState == DocumentReadyState.COMPLETE)
      Application
    else
      document.onreadystatechange = {
        if (document.readyState == DocumentReadyState.COMPLETE)
          Application
      }
  }
}


actual object Application : Page() {

  actual val titleBar = TitleBar()
  override val view = StackLayout(document.querySelector("body") as HTMLBodyElement)
  actual val isLandscape get() = window.innerHeight < window.innerWidth
  actual val isPortrait get() = window.innerHeight >= window.innerWidth
  actual val screenHeight = window.innerHeight
  actual val isTouch = true
  private var contentPage: Page? = null
  private var testMessager:((Request)->Unit)? = null
  private val landscapePage = object : NavigationPage() {
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
  private val portraitPage = object : NavigationPage() {
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

  private val tips = listOf(
      "You can move an animation manually by dragging the slider with your finger.",
      "Tap a dancer to display its path.",

      "Looking for a call but don\'t know the level? " +
      "Go to the Index and enter a search.",

      "Tap the level at the upper right to jump back to the list of calls.",

      "The square dancers are boys, and the round dancers girls.  But most square "+
      "dance calls are not gender-specific.  So study all dancers in a call "+
      "to be proficient in all-position dancing.")

  init {
    //checkForMobile()
    buildDisplay()
    System.log("buildDisplay complete")
    var winh = window.innerHeight
    var winw = window.innerWidth
    view.div.onresize = {
      //  Don't flash the screen on small window adjustments
      if ((winh-window.innerHeight).abs > winh/10 ||
          (winw-window.innerWidth).abs > winw/10) {
        buildDisplay()
        winh = window.innerHeight
        winw = window.innerWidth
      }
    }
    if (Setting("Tips").b != false && Request(window.location.hash)["embed"].isBlank() && !TamUtils.testing) {
      Alert("Tip of the Day").apply {
        val day = System.currentTime() / 86400000  // 8640000 milliseconds in a day
        val tip = tips[day.i % tips.size]
        textView(tip)
        val showbox = appendView(Checkbox("Show Tip of the Day at startup"))
        showbox.isChecked = true
        okAction {
          if (!showbox.isChecked)
            Setting("Tips").b = false
        }
      }
    }
    System.log("Application init complete")
  }

  private fun checkForMobile() {
    val request = Request(window.location.hash)
    if (request["link"].isNotBlank() && request["embed"].isBlank()) {
      val applink = "/${request["link"]}.html" +
          if (request["name"].isNotBlank()) "?${request["name"].encodeURI()}" else ""
      if ("android" in window.navigator.userAgent.toLowerCase()) {
        window.alert("taminations://www.tamtwirlers.org/tamination$applink")
        //window.alert("intent://view" + applink +
        //    "#Intent;package=com.bradchristie.taminationsapp;scheme=Taminations;end")
        window.location.href = "taminations://www.tamtwirlers.org/tamination$applink"
        //window.location.href = "intent://view" + applink +
        //    "#Intent;package=com.bradchristie.taminationsapp;scheme=Taminations;end"
        //window.history.back()
      }
      if ("iPhone|iPad|iPod".ri in window.navigator.userAgent) {
        window.location.href = "Taminations://www.tamtwirlers.org/tamination$applink"
        //  iOS does not replace the browser URL, so..
        //  but con't do it immediately or it cancels the previous line
        //  So instead go back when the window re-gains focus
        window.onfocus = { window.history.back() }
      }
    }
  }

  private fun buildDisplay() {
    val request = Request(window.location.hash)
    val embed = request["embed"].isNotBlank()
    view.apply {
      clear()
      //  Set the view to occupy the entire window
      style.width = "100vw"
      style.height = "100vh"
      style.overflowX = "hidden"
      style.overflowY = "hidden"
      style.margin = "0"
      paddings = 0
      //  Vertical layout for the title bar and the body
      verticalLayout {
        style.height = "100%"
        //  Add the title bar
        if (!embed)
          appendView(titleBar) {
            weight = 1
          }
        //  View to hold content
        contentPage = when {
          embed && (request.action == Request.Action.SEQUENCER) -> SequencerPage()
          embed -> AnimationPage()
          isLandscape -> landscapePage
          else -> portraitPage
        }

        appendView(contentPage!!.view) {
          weight = 9
        }

      }
    }


    //  When the user goes "back" it changes the hash
    //  So we process it by adding a listener for hash changes
    window.onhashchange = {
      doRequest(Request(window.location.hash))
      0
    }

    //  Now go to the main page, or whatever was requested by the hash
    //  Special hack - if embed, replace ANIMLIST with ANIMATION,
    //  to handle links copied from landscape

    if (request["embed"] == "true" && request.action != Request.Action.SEQUENCER)
      doRequest(Request(Request.Action.ANIMATION,request))
    else
      doRequest(request)

  }

  override fun doRequest(request: Request): Boolean = contentPage?.doRequest(request) == true

  //  When we want to go to another page, we convert the request
  //  to a hash location and push it to the browser.
  //  That triggers onhashchange, which converts the hash back
  //  to an request and loads the page.
  //  All this is so "back" will go back to the previous page.
  actual fun sendRequest(request: Request) {
    window.location.hash = request.s
  }
  actual fun sendRequest(action: Request.Action, vararg params:Pair<String,String>) {
    sendRequest(Request(action, *params))
  }
  //  Update the current location like sendRequest but don't
  //  actually send a request
  actual fun updateLocation(request: Request) {
    val newRequest = Request(window.location.hash) + request
    window.history.replaceState(object { },"","#$newRequest")
  }
  actual fun updateLocation(action: Request.Action, vararg params:Pair<String,String>) {
    updateLocation(Request(action,*params))
  }


  //  Message is like a Request except it does not get saved
  //  in the location history and is not expected to trigger
  //  a page change
  fun setTestMesseger(m:(Request)->Unit) {
    testMessager = m
  }
  override fun sendMessage(message: Request) {
    testMessager?.invoke(message)
    contentPage?.sendMessage(message)
  }

  actual fun goBack() : Boolean {
    window.history.back()
    return true
  }

}