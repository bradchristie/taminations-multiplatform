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

//  ViewGroup is any view that contains child views

expect abstract class ViewGroup : View {

  val children: MutableList<View>

  //  appendView can be overridden
  //  Various forms for creating a view or using an existing view
  open fun<T : View> appendView(child:T, code: T.()->Unit = { }) : T
  open fun appendView(code: View.()->Unit = { }) : View
  fun removeView(v: View)

  //  Apply code to all descendants
  fun onDescendants(code:View.()->Unit)

  open fun clear()

}

fun View.removeFromParent() {
  parentView?.removeView(this)
}