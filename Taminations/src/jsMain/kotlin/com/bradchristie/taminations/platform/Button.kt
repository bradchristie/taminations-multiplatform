package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.common.Request
import kotlin.properties.Delegates

//  A button is a view (div) styled as a button
actual open class Button actual constructor(t:String) : View() {

  actual var gradientColor: Color by Delegates.observable(Color(0xa0, 0xa0, 0xa0)) {
    _, _, new ->
    linearGradient(new.veryBright(),new)
  }
  protected val textView: TextView
  protected val layout = LinearLayout(LinearLayout.Direction.HORIZONTAL,div)
  actual var text:String
    get() = textView.text
    set(t) { textView.text = t }
  //  Edge has a problem sizing buttons when text weight is 1
  //  and button weight is 0
  actual override var weight: Int
    get() = super.weight
    set(value) {
      super.weight = value
      if (value == 0)
        textView.weight = 0
    }
  override var displayCode = { layout.displayCode() }

  init {
    style.alignItems = "center"
    style.borderRadius = 10.dips
    style.margin = 2.dips + " " + 4.dips
    style.borderStyle = "outset"
    borders.color = Color.GRAY
    borders.width = 2
    style.cursor = "pointer"
    linearGradient(gradientColor.veryBright(),gradientColor)
    div.onmouseover = { linearGradient(gradientColor,gradientColor.veryBright()) }
    div.onmouseout = { linearGradient(gradientColor.veryBright(),gradientColor) }
    textView = layout.textView(t) {
      padding.top = 8
      padding.bottom = 8
      padding.right = 16
      padding.left = 16
      textSize = 18
      textStyle = "bold"
      weight = 1
      align = TextView.Align.CENTER
      nowrap()
    }
    div.onclick = { event ->
      clickCode()
      Application.sendMessage(Request.Action.BUTTON_PRESS, "button" to text)
      event.stopPropagation()
      false
    }
  }



}
