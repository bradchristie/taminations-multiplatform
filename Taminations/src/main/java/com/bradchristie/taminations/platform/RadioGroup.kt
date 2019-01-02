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
import com.bradchristie.taminations.Taminations


actual class RadioGroup actual constructor(dir:Direction) : LinearLayout(dir) {

  override val div = android.widget.RadioGroup(Taminations.context).apply {
    orientation = if (dir == Direction.VERTICAL)
      android.widget.LinearLayout.VERTICAL
    else
      android.widget.LinearLayout.HORIZONTAL
  }
  actual fun radioButton(text:String, code: RadioButton.()->Unit) =
    appendView(RadioButton(text)).apply(code)

}

actual class RadioButton actual constructor(t:String) : View() {

  override val div = android.widget.RadioButton(Taminations.context).apply {
    text = t
    setOnClickListener { clickCode() }
  }

  var textSize:Int
    get() { throw UnsupportedOperationException() }
    set(value) { div.height = value.dip }

  actual var isChecked:Boolean
    get() = div.isChecked
    set(value) { div.isChecked = value }

}