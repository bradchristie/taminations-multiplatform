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
import org.w3c.dom.HTMLElement
import kotlin.browser.document

//  Relative layout aligns children to the borders of the parent
actual open class RelativeLayout(div:HTMLElement) : ViewGroup(div) {

  actual constructor() : this(document.createHTMLElement("div"))

  init {
    div.style.position = "relative"
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    return super.appendView(child,code).apply {
      div.style.position = "absolute"
    }
  }

  actual fun View.alignParentTop():View {
    div.style.top = "0"
    return this
  }

  actual fun View.alignParentBottom():View {
    div.style.bottom = "0"
    return this
  }
  actual fun View.alignParentLeft():View {
    div.style.left = "0"
    return this
  }
  actual fun View.alignParentRight():View {
    div.style.right = "0"
    return this
  }


}