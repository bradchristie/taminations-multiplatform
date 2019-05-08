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

actual class CachingLinearLayout actual constructor(private val adapter: CachingAdapter) : ViewGroup() {

  init {
    style.display = "flex"
    style.flexDirection = "column"
    style.alignItems = "stretch"
    style.overflowY = "auto"
    style.overflowX = "hidden"
  }

  override fun clear() {
    super.clear()
    fillView()
  }

  private fun fillView() {
    val nitems = adapter.numberOfItems()
    for (i in 0..nitems) {
      val view = adapter.getItem(i)
      appendView(view)
    }
  }


}