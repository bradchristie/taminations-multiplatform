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

fun ViewGroup.horizontalLayout(code: LinearLayout.()->Unit = { }) : LinearLayout =
    appendView(LinearLayout(LinearLayout.Direction.HORIZONTAL)).apply(code)
fun ViewGroup.verticalLayout(code: LinearLayout.()->Unit = { }) : LinearLayout =
    appendView(LinearLayout(LinearLayout.Direction.VERTICAL)).apply(code)

expect open class LinearLayout(dir: Direction) : ViewGroup {

  enum class Direction {
    HORIZONTAL,
    VERTICAL
  }


  //  Horizontally align
  //  Only alignCenter is used
  //  TODO maybe refactor uses to RelativeLayout?
  //fun View.alignLeft()
  fun View.alignCenter()
  //fun View.alignRight()

  //  Vertically align
  //fun View.alignTop()
  //fun View.alignMiddle()
  //fun View.alignBottom()

}
