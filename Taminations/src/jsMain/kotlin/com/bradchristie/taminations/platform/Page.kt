package com.bradchristie.taminations.platform
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.Request
import com.bradchristie.taminations.common.d
import com.bradchristie.taminations.common.s
import kotlin.js.Promise

//  This builds an animation that is a Promise
//  using a function and a class
fun Animation(duration:Long,
              changer:(time:Long,fraction:Double)->Unit) : Promise<String> {
  val helper = AnimationHelper(duration, changer)
  return Promise(helper.executor)
}

class AnimationHelper(private val duration:Long,
                      private val changer:(time: Long, fraction: Double)->Unit) {

  private var startTime = 0L
  private lateinit var resolver:(String)->Unit
  private lateinit var rejecter:(Throwable)->Unit  // never used
  val executor:(resolve:(String)->Unit,reject:(Throwable)->Unit)->Unit =
    {  resolve,reject ->
      resolver = resolve
      rejecter = reject
    }

  init {
    System.later { step(System.currentTime()) }
  }

  fun step(timestamp:Long) {
    if (startTime == 0L) {
      startTime = timestamp
    }
    changer(timestamp-startTime,(timestamp-startTime).d/duration.d)
    if (timestamp - startTime < duration)
      System.later { step(System.currentTime()) }
    else {
      resolver("We are done!")
    }
  }


}

actual abstract class Page {

  actual companion object {

    //  Animate a transition between two pages
    actual fun animate(currentPage: Page?, nextPage: Page, code: () -> Unit) {
      if (Setting("Transitions").s == "None") {
        nextPage.view.style.opacity = "1" // just to make sure
        nextPage.view.style.transform = ""
        System.later {
          code()
          Application.sendMessage(Request.Action.TRANSITION_COMPLETE)
        }
      }
      else {
        val zoom = Setting("Transitions").s == "Fade and Zoom"
        Animation(300) { _, fraction ->
          currentPage?.view?.style?.opacity = (1.0 - fraction).s
          if (zoom)
            currentPage?.view?.style?.transform = "scale(${1 - fraction},${1 - fraction})"
        }.then {
          currentPage?.view?.style?.opacity = "1"
          currentPage?.view?.style?.transform = ""
          nextPage.view.style.opacity = "0"
          code()
          Animation(300) { _, fraction ->
            nextPage.view.style.opacity = (fraction).s
            if (zoom)
              nextPage.view.style.transform = "scale($fraction,$fraction)"
          }.then {
            nextPage.view.style.opacity = "1" // just to make sure
            nextPage.view.style.transform = ""
            Application.sendMessage(Request.Action.TRANSITION_COMPLETE)
          }
        }
      }
    }

  }

  //  Every page has a base view that it needs to build on init
  actual abstract val view: View

  //  If it cannot handle the given request, it returns false
  //  Default is to handle the single action registered with onAction
  //  A page can override this method for more complex cases
  actual open fun doRequest(request: Request): Boolean =
      if (request.action == requestAction) {
        requestCode(request)
        true
      } else
        false

  actual fun doRequest(action: Request.Action, vararg pairs:Pair<String,String>) {
    doRequest(Request(action, *pairs))
  }
  actual fun doRequest(action: Request.Action, from: Request) {
    doRequest(Request(action, from))
  }

  //  Almost always a simple page handles just one request action
  //  So these are convenience methods for that case
  private var requestAction: Request.Action = Request.Action.NONE
  private var requestCode: (Request) -> Unit = { }
  protected actual fun onAction(action: Request.Action, code: (Request) -> Unit) {
    requestAction = action
    requestCode = code
  }

  actual open fun canDoAction(action: Request.Action) = action == requestAction

  //  Other requests are sent as "messages"
  //  A page can register to process a message with the onMessage function
  private var messageAction = hashMapOf<Request.Action, (Request) -> Unit>()

  protected actual fun onMessage(message: Request.Action, code: (Request) -> Unit) {
    messageAction[message] = code
  }

  //  Anybody can send a message to a page or the application
  actual open fun sendMessage(message: Request) {
    messageAction[message.action]?.invoke(message)
  }
  actual fun sendMessage(message: Request.Action, vararg params:Pair<String,String>) {
    sendMessage(Request(message, *params))
  }

}
