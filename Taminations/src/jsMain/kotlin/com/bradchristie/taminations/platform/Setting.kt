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

import com.bradchristie.taminations.common.Request
import kotlin.browser.document
import kotlin.browser.window

//  A Setting for a web browser is stored as a cookie
actual class Setting
  actual constructor(val name:String) {

  //  All settings share one Map of the decoded cookie
  companion object {
    val settings = document.cookie.split("&")
        .map { it.decodeURI() }
        .filter { it.contains("=") }
        .map { it.split("=") }
        .map { Pair(it[0],it[1]) }
        .toMap().toMutableMap()
    fun save() {
      document.cookie = settings
          .map { "${it.key}=${it.value}".encodeURI() }
          .joinToString("&")
    }
    //  Also accept a setting in a link, overriding user setting
    fun fromUrl(name:String):String? =
        Request(window.location.hash)[name].let {
          if (it.isBlank()) null else it
        }
  }


  actual var s:String?
    get() = fromUrl(name) ?: settings[name]
    set(v) {
      settings[name] = v!!
      save()
    }

  actual var b:Boolean?
    get() = fromUrl(name)?.equals("true") ?: settings[name]?.equals("true")
    set(v) {
      settings[name] = v!!.toString()
      save()
    }


}