package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
  Copyright (C) 2018 Brad Christie

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

actual class ImageButton actual constructor(name:String, c: Shape) : Button(name,c) {
  actual fun setImage(shape: Shape) {}
}

actual open class Button actual constructor(t:String) : View() {
  actual var gradientColor: Color
    get() = TODO("not implemented")
    set(value) {}
  actual var text: String
    get() = TODO("not implemented")
    set(value) {}
  actual override var weight: Int
    get() = TODO("not implemented")
    set(value) {}

  constructor(text:String,image:Shape) : this(text) {
  }

}