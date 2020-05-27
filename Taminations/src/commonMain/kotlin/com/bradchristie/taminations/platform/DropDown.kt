package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
  Copyright (C) 2020 Brad Christie

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

fun ViewGroup.dropDown(t:String, code: DropDown.()->Unit = { }) : DropDown =
    appendView(DropDown(t)).apply(code)

expect class DropDown(title:String) : TextView {

  fun addItem(name:String, code:View.()->Unit={ }):View
  fun selectAction(action:(item:String)->Unit)

}

expect class DropDownMenu() : LinearLayout {
  fun showAt(v:View, x: Int, y: Int)
  fun addItem(
      name: String,
      code: ViewGroup.() -> Unit = { }
  ): View

  fun selectAction(action: (item: String) -> Unit)

}