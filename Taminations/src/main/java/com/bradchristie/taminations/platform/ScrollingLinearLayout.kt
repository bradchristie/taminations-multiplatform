package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
  Copyright (C) 2018 Brad Christie

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
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.bradchristie.taminations.Taminations

actual open class ScrollingLinearLayout : ViewGroup() {

  private val innerdiv = android.widget.LinearLayout(Taminations.context).apply {
    orientation = android.widget.LinearLayout.VERTICAL
  }
  override val div = android.widget.ScrollView(Taminations.context).apply {
    addView(innerdiv)
    layoutParams = android.widget.FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    child.div.layoutParams = android.widget.LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0f)
    innerdiv.addView(child.div)
    children.add(child)
    child.parentView = this
    child.code()
    return child
  }

  override fun clear() {
    innerdiv.removeAllViews()
    children.clear()
  }

}