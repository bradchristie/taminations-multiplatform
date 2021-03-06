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

actual open class TextView actual constructor(t: String) : View() {
  actual enum class Align {
    LEFT,
    CENTER,
    RIGHT
  }

  actual var autoSize: Boolean   // not implemented
    get() = TODO("not implemented")
    set(value) {}
  actual var textStyle: String
    get() = TODO("not implemented")
    set(value) {}
  actual var textSize: Int
    get() = TODO("not implemented")
    set(value) {}

  actual fun nowrap() {}
  //  Text shadow is only used for the title bar
  actual fun shadow() {}

  actual var text: String
    get() = TODO("not implemented")
    set(value) {}
  //  Text alignment - more often done by alignment of this view in its parent
  actual var align: Align
    get() = TODO("not implemented")
    set(value) {}

}