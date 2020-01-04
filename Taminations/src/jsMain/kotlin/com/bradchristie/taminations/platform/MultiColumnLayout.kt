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

import kotlin.browser.window

actual class MultiColumnLayout actual constructor(private val adapter: CachingAdapter) : ViewGroup() {

  init {
    style.display = "flex"
    style.flexDirection = "column"
    style.flexWrap = "wrap"
    style.overflowX = "auto"
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    super.appendView(child, code)
    //  Hack to fix display on Edge
    if (window.navigator.userAgent.contains("Edge"))
      if (children.count() == 150) {
        children.forEach { it.width = 300 }
      } else if (children.count() > 150)
        child.width = 300
    return child
  }

  override fun clear() {
    super.clear()
    fillView()
  }

  private fun fillView() {
    val nitems = adapter.numberOfItems()
    for (i in 0 until nitems) {
      val view = adapter.getItem(i)
      appendView(view)
    }
  }


}