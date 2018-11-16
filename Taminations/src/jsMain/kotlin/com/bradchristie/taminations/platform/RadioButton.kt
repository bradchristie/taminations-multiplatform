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

actual class RadioButton actual constructor(t:String) : View() {

  private val textView: TextView
  private val layout = LinearLayout(LinearLayout.Direction.HORIZONTAL,div)

  private val radioButton = layout.appendHTMLElement("input") {
    setAttribute("type","radio")
    setAttribute("value",t)
    onclick = { event ->
      clickCode()
      event.stopPropagation()
      System.later {
        //  doesn't seem to work unless done later
        isChecked = true
      }
      false
    }
  } as HTMLInputElement

  init {
    style.cursor = "pointer"
    textView = layout.textView(t) {
      textSize = 20
      style.display = "inline"
    }
    div.onclick = { event ->
      clickCode()
      isChecked = true
      event.stopPropagation()
      false
    }
  }

  actual var isChecked:Boolean
      get() = radioButton.checked
      set(v) { radioButton.checked = v }

  fun setGroup(group:String) {
    radioButton.setAttribute("name",group)
  }

}