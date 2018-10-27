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


actual class TamElement  /*(private val e: Element)*/ {
  actual val tag: String
    get() = TODO("not implemented")
  actual val textContent: String
    get() = TODO("not implemented")

  actual fun hasAttribute(tag: String): Boolean {
    TODO("not implemented")
  }

  actual fun getAttribute(tag: String): String? {
    TODO("not implemented")
  }

  actual fun evalXPath(expr: String): List<TamElement> {
    TODO("not implemented")
  }
}

actual class TamDocument  /*(private val doc:XMLDocument)*/ {
  actual fun evalXPath(expr: String): List<TamElement> {
    TODO("not implemented")
  }
}