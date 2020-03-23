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

import com.bradchristie.taminations.common.Request
import com.bradchristie.taminations.common.TitleBar
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.View

actual object Application : Page() {
  override val view: View
    get() = TODO("not implemented")
  actual val titleBar: TitleBar
    get() = TODO("not implemented")
  actual val isLandscape: Boolean
    get() = TODO("not implemented")
  actual val isPortrait: Boolean
    get() = TODO("not implemented")
  actual val screenHeight: Int
    get() = TODO("not implemented")
  actual val isTouch: Boolean
    get() = TODO("not implemented")

  //  When we want to go to another page, we convert the request
  //  to a hash location and push it to the browser.
  //  That triggers onhashchange, which converts the hash back
  //  to an request and loads the page.
  //  All this is so "back" will go back to the previous page.
  actual fun sendRequest(request: Request) {}

  actual fun sendRequest(action: Request.Action, vararg params: Pair<String, String>) {}
  //  Update the current location like sendRequest but don't
  //  actually send a request
  actual fun updateLocation(request: Request) {}

  actual fun updateLocation(action: Request.Action, vararg params: Pair<String, String>) {}
  actual fun goBack(): Boolean {
    TODO("not implemented")
  }

}