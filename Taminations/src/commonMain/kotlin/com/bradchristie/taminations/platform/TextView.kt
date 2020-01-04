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

//  A view that contains one text node

fun ViewGroup.textView(t:String, code: TextView.()->Unit = { }) : TextView
    = appendView(TextView(t)).apply(code)

expect open class TextView(t:String) : View {

  enum class Align {
    LEFT,
    CENTER,
    RIGHT
  }

  var autoSize : Boolean   // not implemented
  var textStyle:String
  var textSize:Int
  fun nowrap()
  //  Text shadow is only used for the title bar
  fun shadow()

  var text:String

  //  Text alignment - more often done by alignment of this view in its parent
  var align:Align

}