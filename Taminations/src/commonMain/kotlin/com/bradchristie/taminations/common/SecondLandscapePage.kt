package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.platform.LinearLayout
import com.bradchristie.taminations.platform.Page

class SecondLandscapePage : Page() {

  override val view = LinearLayout(LinearLayout.Direction.HORIZONTAL)

  private val leftPage = AnimListPage()
  private val centerPage = AnimationPage()
  private val definitionPage = DefinitionPage()
  private val rightPage : NavigationPage = object : NavigationPage() {
    override val pages = listOf(definitionPage, SettingsPage())
  }

  init {
    view.apply {
      fillParent()

      //  Add the list of animations on the left side
      appendView(leftPage.view) {
        weight = 1
      }

      //  Animation is in the center
      appendView(centerPage.view) {
        weight = 1
      }

      //  Right side holds definition and settings
      appendView(rightPage.view) {
        weight = 1
      }

    }

    onMessage(Request.Action.ANIMATION_READY) { request ->
      //  The list of animations is complete
      //  Choose either an animation in the link
      //  or if none the first animation
      if (!leftPage.animListModel.selectAnimationByName(request["name"]))
        leftPage.animListModel.selectFirstAnimation()
      //  Send request to navigation page so it brings up the definition
      rightPage.doRequest(Request.Action.DEFINITION,request)
    }
    onMessage(Request.Action.ANIMATION) { request ->
      //  Animate a change to a different Tamination
      Page.animate(centerPage,centerPage) {
        centerPage.doRequest(Request.Action.ANIMATION, request)
      }
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      when (request["button"]) {
        "Settings" -> rightPage.doRequest(Request.Action.SETTINGS)
        "Definition" -> rightPage.doRequest(Request.Action.DEFINITION,
            "link" to leftPage.animListModel.link)
      }
    }

  }

  override fun doRequest(request: Request): Boolean = when (request.action) {
    //  Handle requests for specific pages
    Request.Action.ANIMLIST, Request.Action.ANIMATION -> {
      leftPage.doRequest(Request(Request.Action.ANIMLIST, request))
    }
    else -> false
  }

  override fun canDoAction(action: Request.Action): Boolean =
      leftPage.canDoAction(action)

  override fun sendMessage(message: Request) {
    super.sendMessage(message)
    leftPage.sendMessage(message)
    centerPage.sendMessage(message)
    definitionPage.sendMessage(message)
  }


}