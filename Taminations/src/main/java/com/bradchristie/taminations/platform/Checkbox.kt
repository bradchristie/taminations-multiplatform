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

import android.graphics.Typeface
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.platform.System.later

actual class Checkbox actual constructor(t:String) : View() {

  override val div = android.widget.CheckBox(Taminations.context).apply {
    text = t
    setOnClickListener { clickCode() }
  }

  init {
    //  Android checkbox is larger than usual, but resize
    //  has to be done after view is added to group
    later {
      height = 60
    }
  }
  actual var text = t
  actual var textStyle:String
    get() { throw UnsupportedOperationException() }
    set(value) { if (value=="bold") div.typeface = Typeface.DEFAULT_BOLD }
  actual var textSize:Int
    get() { throw UnsupportedOperationException() }
    set(value) { div.height = value.dip }

  actual var isChecked:Boolean
    get() = div.isChecked
    set(value) { div.isChecked = value }


}