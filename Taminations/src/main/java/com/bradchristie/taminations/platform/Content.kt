package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import com.bradchristie.taminations.Taminations

//  A view that holds just one child, occupying the entire space
//  Appending a new child removes the old child

fun ViewGroup.content(code: Content.()->Unit = { }) : Content =
    appendView(Content()).apply(code)

class Content : ViewGroup() {

  override val div = android.widget.RelativeLayout(Taminations.context)

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    clear()
    child.fillParent()
    return super.appendView(child, code)
  }

}