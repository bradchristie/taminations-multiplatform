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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.platform.LinearLayout
import com.bradchristie.taminations.platform.Page

class FirstLandscapePage : Page() {

  override val view = LinearLayout(LinearLayout.Direction.HORIZONTAL)
  private val leftPage: Page = LevelPage()
  private val rightPage : NavigationPage = object : NavigationPage() {
    override val pages = listOf(AboutPage(), CalllistPage(), SettingsPage())
  }

  init {
    view.apply {

      //  Add the level view, on the left side, controlled by a LevelPage
      appendView(leftPage.view) {
        weight = 2
      }

      //  Right side holds other pages
      appendView(rightPage.view) {
        weight = 6
      }

    }
  }

  override fun doRequest(request: Request): Boolean =
      (leftPage.doRequest(request) || rightPage.doRequest(request)).also {
        if (it)
          Application.titleBar.level = ""
      }

  override fun canDoAction(action: Request.Action): Boolean =
      (leftPage.canDoAction(action)) ||
          (rightPage.canDoAction(action)) ||
          (Application.isLandscape && action == Request.Action.STARTUP)


  override fun sendMessage(message: Request) {
    leftPage.sendMessage(message)
    rightPage.sendMessage(message)
  }

}