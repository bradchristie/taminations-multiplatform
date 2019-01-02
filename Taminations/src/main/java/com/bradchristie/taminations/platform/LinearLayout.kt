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

import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.bradchristie.taminations.Taminations

actual open class LinearLayout actual constructor(private val dir: Direction) : ViewGroup() {

  override val div = android.widget.LinearLayout(Taminations.context)
      .apply {
    if (dir == Direction.VERTICAL)
      orientation = android.widget.LinearLayout.VERTICAL
  }

  actual enum class Direction {
    HORIZONTAL,
    VERTICAL
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    if (dir == Direction.HORIZONTAL) {
      child.div.layoutParams = android.widget.LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT, 0f)
    }
    else {
      child.div.layoutParams = android.widget.LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0f)
    }
    return super.appendView(child, code)
  }

  actual fun View.alignCenter() {
    if (div.layoutParams.height == MATCH_PARENT)
      div.layoutParams.height = WRAP_CONTENT
    weightLayout.gravity = Gravity.CENTER
  }


}
