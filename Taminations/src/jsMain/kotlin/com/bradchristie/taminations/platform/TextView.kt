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

//  A view that contains one text node

import kotlin.dom.appendText
import kotlin.dom.clear

actual open class TextView actual constructor(private var t:String) : View() {

  actual enum class Align {
    LEFT,
    CENTER,
    RIGHT
  }

  actual var autoSize = false  // not implemented
  actual var textStyle:String
    get() { throw UnsupportedOperationException() }
    set(value) { style.fontWeight = value }
  actual var textSize:Int
    get() { throw UnsupportedOperationException() }
    set(value) { style.fontSize = value.dips }
  actual fun nowrap() {
    style.whiteSpace = "nowrap"
  }
  //  Text shadow is only used for the title bar
  actual fun shadow() {
    style.textShadow = "2px 2px black"
  }

  init {
    div.appendText(t)
    textSize = 20
  }

  actual var text:String
    get() = t
    set(value) {
      t = value
      div.clear()
      div.appendText(value)
    }

  //  Text alignment - more often done by alignment of this view in its parent
  actual var align:Align
  get() = when (div.style.textAlign) {
    "center" -> Align.CENTER
    "right" -> Align.RIGHT
    else -> Align.LEFT
  }
  set(v) {
    div.style.textAlign = when (v) {
      Align.LEFT -> "left"
      Align.CENTER -> "center"
      Align.RIGHT -> "right"
    }
  }

}