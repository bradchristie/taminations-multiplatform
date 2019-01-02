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
import com.bradchristie.taminations.platform.WebView
import com.bradchristie.taminations.platform.Page

class AboutPage : Page() {

  override val view = WebView("info/about.html")

  //  Note that showing the About page is the default
  //  if nothing is specified (i.e. startup)
  override fun doRequest(request: Request): Boolean =
          if (request.action == Request.Action.ABOUT ||
              (Application.isLandscape && request.action == Request.Action.STARTUP)) {
            Application.titleBar.title = "Taminations"
            true
          } else
            false

  override fun canDoAction(action: Request.Action): Boolean =
      action == Request.Action.ABOUT ||
          (Application.isLandscape && action == Request.Action.STARTUP)

}
