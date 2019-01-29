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

//    This class makes a dropdown with clever use of CSS
//    See https://www.w3schools.com/css/css_dropdowns.asp

actual class DropDown actual constructor(title:String) : TextView(title) {

  private val layout = LinearLayout(LinearLayout.Direction.HORIZONTAL,div)
  private val dropDown = LinearLayout(LinearLayout.Direction.VERTICAL)
  private var selectCode:(item:String)->Unit = { _: String -> }

  init {
    div.style.position = "relative"
    div.style.display = "inline-block"
    layout.appendView(dropDown) {
      div.style.position = "absolute"
      div.style.zIndex = "1"
      div.style.display = "none"
      div.style.backgroundColor = "white"
      div.style.border = "2px solid black"
      div.style.boxShadow = "0px 8px 16px 0px rgba(0,0,0,0.2)"
    }
    div.onmouseenter = {
      dropDown.div.style.display = "block"
      false
    }
    div.onmouseleave = {
      dropDown.div.style.display = "none"
      false
    }
  }

  actual fun addItem(name:String, code:View.()->Unit):View {
    val item = SelectablePanel().apply {
      textView(name) {
        margins = 4
      }
      clickAction {
        selectCode(name)
      }
    }
    dropDown.appendView(item)
    item.code()
    return item
  }

  actual fun selectAction(action:(item:String)->Unit) {
    selectCode = action
  }

}
