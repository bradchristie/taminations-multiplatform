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


actual abstract class ViewGroup : View() {

  abstract override val div: android.view.ViewGroup
  actual val children = mutableListOf<View>()

  //  Add a child view in various ways
  //  appendThisView cannot be overridden by other classes
  private fun <T : View> appendThisView(child:T, code: T.()->Unit = { }) : T {
    div.addView(child.div)
    children.add(child)
    child.parentView = this
    child.code()
    return child
  }

  //  appendView can be overridden
  actual open fun<T : View> appendView(child:T, code: T.()->Unit) : T =
      appendThisView(child,code)
  actual open fun appendView(code: View.()->Unit) = appendView(View(),code)
  actual open fun removeView(v: View) {
    v.parentView = null
    div.removeView(v.div)
    children.remove(v)
  }
  actual open fun clear() {
    div.removeAllViews()
    children.clear()
  }

  //  Apply code to all descendants
  actual fun onDescendants(code:View.()->Unit) {
    children.forEach { child ->
      child.code()
      if (child is ViewGroup)
        child.onDescendants(code)
    }
  }


}