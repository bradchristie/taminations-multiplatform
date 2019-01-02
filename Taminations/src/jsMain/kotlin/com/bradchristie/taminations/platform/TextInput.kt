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

import com.bradchristie.taminations.common.Color
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent

actual class TextInput: View() {

  actual var text
    get() = textInput.value
    set(v) { textInput.value = v }
  actual var hint
    get() = textInput.getAttribute("placeholder") ?: ""
    set(v) { textInput.setAttribute("placeholder",v) }

  private var onReturn = { }
  private var onKey = { }


  private val textInput = div.appendHTMLElement("input") {
    setAttribute("type","text")
    style.width = "100%"  // of the div
    style.height = "100%"
    style.fontSize = "18px"
    onkeydown = { event ->
      val code = (event as KeyboardEvent).keyCode
      if (code == 13)
        onReturn()
    }
    oninput = { onKey() }
  } as HTMLInputElement

  override var backgroundColor: Color
    get() = cssColor(textInput.style.backgroundColor)
    set(c) {
      textInput.style.backgroundColor = c.css
    }


  override var textColor get() = cssColor(textInput.style.color)
    set(c) {
      textInput.style.color = c.css
    }

  override fun focus() = textInput.focus()

  actual fun returnAction(code:()->Unit) {
    onReturn = code
  }

  actual fun keyAction(code:()->Unit) {
    onKey = code
  }

}