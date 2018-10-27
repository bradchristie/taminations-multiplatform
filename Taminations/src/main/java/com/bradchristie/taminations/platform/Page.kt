package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
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

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.Request
import com.bradchristie.taminations.common.f

actual abstract class Page {

  actual companion object {

    fun oneAnimation(page:Page, alphaEndValue:Int, scaleEndValue:Int, after:()->Unit = { }) {
      if (Setting("Transitions").s == "None") {
        //  Just make sure any previous animations are reset
        page.view.div.scaleX = 1f
        page.view.div.scaleY = 1f
        page.view.div.alpha = 1f
        System.later {
          after()
          Application.sendMessage(Request.Action.TRANSITION_COMPLETE)
        }
      }
      else {
        //  Use android View.animate() to do the animation
        page.view.div.animate().run {
          if (alphaEndValue == 1)
            page.view.div.alpha = 0f
          alpha(alphaEndValue.f)
          if (Setting("Transitions").s != "Fade") {
            //  Fade and Zoom
            scaleX(scaleEndValue.f).scaleY(scaleEndValue.f)
            page.view.div.scaleX = (1-scaleEndValue).f
            page.view.div.scaleY = (1-scaleEndValue).f
          } else {
            //  Fade only
            //  Make sure any previous zoom is reset
            page.view.div.scaleX = 1f
            page.view.div.scaleY = 1f
          }
          duration = 300
          setListener(object: AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
              animation?.removeAllListeners()
              after()
              Application.sendMessage(Request.Action.TRANSITION_COMPLETE)
            }
          })
        }
      }
    }

    //  Animate a transition between two pages
    actual fun animate(currentPage: Page?, nextPage: Page, code: () -> Unit) {
      if (currentPage != null) {
        oneAnimation(currentPage,0,0) {
          code()
          oneAnimation(nextPage,1,1)
        }
      } else {
        code()
        oneAnimation(nextPage, 1, 1)
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