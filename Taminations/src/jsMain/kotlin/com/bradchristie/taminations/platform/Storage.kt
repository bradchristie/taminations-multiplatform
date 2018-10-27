package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.browser.window

actual object Storage {

  actual operator fun get(key:String):String? = window.localStorage[key]
  actual operator fun set(key:String, value:String) {
    window.localStorage[key] = value
  }
  actual val keys:List<String> get() = (0 until window.localStorage.length).map {
    window.localStorage.key(it)!!
  }
  actual fun remove(key:String) = window.localStorage.removeItem(key)

}