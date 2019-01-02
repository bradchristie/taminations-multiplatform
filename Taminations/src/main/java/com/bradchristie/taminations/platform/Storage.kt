package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
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

import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.bradchristie.taminations.Taminations

actual object Storage {

  private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(Taminations.context)

  actual operator fun get(key:String):String? = prefs.getString(key,null)
  actual operator fun set(key:String, value:String) {
    prefs.edit().putString(key,value).apply()
  }
  actual fun remove(key: String) = prefs.edit().remove(key).apply()
  actual val keys:List<String> get() = prefs.all.keys.toList()
}