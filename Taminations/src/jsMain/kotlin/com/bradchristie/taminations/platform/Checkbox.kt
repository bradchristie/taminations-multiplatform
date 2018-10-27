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

import org.w3c.dom.HTMLInputElement

actual class Checkbox actual constructor(t:String) : View() {

  private val layout = LinearLayout(LinearLayout.Direction.HORIZONTAL,div)
  private val textView:TextView
  private val checkbox  = div.appendHTMLElement("input") {
    setAttribute("type","checkbox")
    setAttribute("name",t)
    onclick = { clickCode() }
  } as HTMLInputElement

  init {
    style.cursor = "pointer"
    textView = layout.textView(t) {
      style.display = "inline"
      div.onclick = {event ->
        isChecked = !isChecked
        clickCode()
        event.stopPropagation()
        false
      }
    }
  }

  actual var text:String
    get() = textView.text
    set(value) { textView.text = value }

  actual var textStyle:String
    get() { throw UnsupportedOperationException() }
    set(value) { textView.textStyle = value }

  actual var textSize:Int
    get() { throw UnsupportedOperationException() }
    set(value) { textView.textSize = value }

  actual var isChecked:Boolean
    get() = checkbox.checked
    set(v) { checkbox.checked = v }

}
