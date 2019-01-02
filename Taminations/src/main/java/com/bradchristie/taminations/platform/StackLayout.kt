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

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.Color

/**
 *   A view to show one of a number of child views, each completely filling the view area
 */

actual open class StackLayout actual constructor() : ViewGroup() {

  override val div = android.widget.RelativeLayout(Taminations.context)
  private lateinit var currentView: View

  init {
    backgroundColor = Color.FLOOR
  }

  //  Add a new child view and show it
  override fun <T: View> appendView(child: T, code: T.() -> Unit): T {
    super.appendView(child,code)
    child.div.layoutParams =
        android.widget.RelativeLayout.LayoutParams(MATCH_PARENT,MATCH_PARENT)
    div.bringChildToFront(child.div)
    return child
  }

}