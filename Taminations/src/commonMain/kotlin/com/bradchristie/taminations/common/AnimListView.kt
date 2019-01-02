package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.platform.*

class AnimListView : LinearLayout(LinearLayout.Direction.VERTICAL) {

  private val animList = ScrollingLinearLayout()
  val keyView = LinearLayout(LinearLayout.Direction.HORIZONTAL)
  val buttonView = LinearLayout(LinearLayout.Direction.HORIZONTAL)
  var count = 0

  init {
    appendView(animList) {
      backgroundColor = Color.LIGHTGRAY
      isScrollable = true
      weight = 1
    }
    appendView(keyView) {
      weight = 0
      border.top.width = 1
      textView("Common") {
        backgroundColor = Color.COMMON
        weight = 1
        align = TextView.Align.CENTER
      }
      textView("Harder") {
        backgroundColor = Color.HARDER
        weight = 1
        align = TextView.Align.CENTER
      }
      textView("Expert") {
        backgroundColor = Color.EXPERT
        weight = 1
        align = TextView.Align.CENTER
      }
    }
    appendView(buttonView) {
      weight = 0
      backgroundColor = Color.BLACK
      appendView(Button("Definition")) {
        weight = 1
      }
      appendView(Button("Settings")) {
        weight = 1
      }
    }
  }

  fun clearItems() {
    animList.clear()
    count = 0
  }

  fun addItem(item: AnimListItem, view: View = View()): View =
    animList.appendView(view) {
      weight = 0  // don't try to force list to fit screen
       if (item.celltype == CellType.Separator)
         height = 4
      else {
        padding.left = if (item.celltype == CellType.Indented) 30 else 12
        padding.top = 4
        padding.bottom = 4
      }
      borders.width = 1

      textColor = when {
        item.celltype == CellType.Header -> Color.WHITE
        item.wasItemSelected -> Color.BLUE.darker().darker()
        else -> Color.BLACK
      }
      if (item.celltype == CellType.Separator || item.celltype == CellType.Header)
        backgroundColor = Color(0x804080)
      count += 1
    }

}