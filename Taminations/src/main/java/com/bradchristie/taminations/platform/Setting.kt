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

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.bradchristie.taminations.Taminations


/**
 *   Class to read and write settings.
 *   Usage:
 *     stringvalue = Setting("MySetting").s
 *     Setting("MySetting").s = newstringvalue
 *     boolvalue = Setting("MyToggle").b
 *     Setting('MyToggle").b = newboolvalue
 */
actual class Setting actual constructor(val name:String) {

  companion object {
    val prefs: SharedPreferences =
        Taminations.context.getSharedPreferences("com.bradchristie.taminationsapp_preferences",MODE_PRIVATE)
  }

  actual var s:String? get() = if (prefs.contains(name)) prefs.getString(name,"") else null
                set(sval) { prefs.edit().putString(name,sval!!).apply() }

  actual var b:Boolean? get() = if (prefs.contains(name)) prefs.getBoolean(name,false) else null
                 set(bval) { prefs.edit().putBoolean(name,bval!!).apply() }

}