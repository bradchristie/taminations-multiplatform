package com.bradchristie.taminations.platform

import org.w3c.dom.HTMLElement
import kotlin.browser.document

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

actual open class LinearLayout(private val dir: Direction, div:HTMLElement) : ViewGroup(div) {

  actual enum class Direction {
    HORIZONTAL,
    VERTICAL
  }

  actual constructor(dir: Direction)
      : this(dir,document.createHTMLElement("div"))

  init {
    style.display = "flex"
    style.flexDirection = when (dir) {
      Direction.HORIZONTAL -> "row"
      Direction.VERTICAL -> "column"
    }
    //  Default is for each item to fill its row or column
    style.alignItems = "stretch"
  }

  //  Horizontally align
  fun View.alignLeft() {
    if (dir != Direction.HORIZONTAL)
      throw UnsupportedOperationException()
    div.style.alignSelf = "flex-start"
  }
  actual fun View.alignCenter() {
    if (dir != Direction.HORIZONTAL)
      throw UnsupportedOperationException()
    div.style.alignSelf = "center"
  }
  fun View.alignRight() {
    if (dir != Direction.HORIZONTAL)
      throw UnsupportedOperationException()
    div.style.alignSelf = "flex-end"
  }

  //  Vertically align
  fun View.alignTop() {
    if (dir != Direction.VERTICAL)
      throw UnsupportedOperationException()
    div.style.alignSelf = "flex-start"
  }
  fun View.alignMiddle() {
    if (dir != Direction.VERTICAL)
      throw UnsupportedOperationException()
    div.style.alignSelf = "center"
  }
  fun View.alignBottom() {
    if (dir != Direction.VERTICAL)
      throw UnsupportedOperationException()
    div.style.alignSelf = "flex-end"
  }

}
