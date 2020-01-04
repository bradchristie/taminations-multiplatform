package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
  Copyright (C) 2020 Brad Christie

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

import com.bradchristie.taminations.common.Color
import org.w3c.dom.HTMLElement
import kotlin.browser.document

/**
 *   A view to show one of a number of child views, each completely filling the view area
 */
actual open class StackLayout(div:HTMLElement) : ViewGroup(div)  {

  actual constructor() : this(document.createHTMLElement("div"))

  private lateinit var currentView: View

  init {
    backgroundColor = Color.FLOOR
    //  Set relative position so child views can use absolute position
    style.position = "relative"
  }

  //  Add a new child view and show it
  override fun <T: View> appendView(child: T, code: T.() -> Unit): T {
    //  Set child to completely fill the view
    child.style.height = "100%"
    child.style.width = "100%"
    child.style.position = "absolute"
    child.style.top = "0"
    child.style.left = "0"
    //  Set this (the last) child as the currently visible child
    super.appendView(child,code)
    currentView = child
    return child
  }

}