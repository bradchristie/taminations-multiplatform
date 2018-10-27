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

import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.RelativeLayout.*
import com.bradchristie.taminations.Taminations

//  Relative layout aligns children to the borders of the parent
actual open class RelativeLayout : ViewGroup() {

  override val div = android.widget.RelativeLayout(Taminations.context)

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    child.div.layoutParams = android.widget.RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    return super.appendView(child,code)
  }

  //  Return the RelativeLayout params of the receiver
  private val View.layout:android.widget.RelativeLayout.LayoutParams
    get() = div.layoutParams as android.widget.RelativeLayout.LayoutParams

  //  Functions specific to Relative Layout
  actual fun View.alignParentTop():View {
    layout.addRule(ALIGN_PARENT_TOP)
    return this
  }

  actual fun View.alignParentBottom():View {
    layout.addRule(ALIGN_PARENT_BOTTOM)
    return this
  }
  actual fun View.alignParentLeft():View {
    layout.addRule(ALIGN_PARENT_LEFT)
    return this
  }
  actual fun View.alignParentRight():View {
    layout.addRule(ALIGN_PARENT_RIGHT)
    return this
  }

}