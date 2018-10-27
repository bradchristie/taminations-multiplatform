package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import com.bradchristie.taminations.common.Color

actual open class SelectablePanel : LinearLayout(Direction.HORIZONTAL) {

  private var saveBackground = Color()
  private var borderView: View? = null
  private var _isSelected:Boolean = false
  actual var isSelected:Boolean
    get() = _isSelected
    set(b) {
      if (b != _isSelected) {
        _isSelected = b
        selectStyle()
      }
    }


  init {
    style.position = "relative"
    style.cursor = "pointer"
    div.onmouseenter = {
      if (borderView == null)
        borderView = appendView {
          style.position = "absolute"
          style.top = "0"
          style.left = "0"
          style.width = "100%"
          style.height = "100%"
          borders.color = Color.BLUE
          borders.width = 4
        }
      false
    }
    div.onmouseleave = {
      if (borderView != null)
        removeView(borderView!!)
      borderView = null
      false
    }
  }

  private fun selectStyle() {
    if (isSelected) {
      saveBackground = backgroundColor
      backgroundColor = Color.BLUE
      textColor = Color.WHITE
    } else {
      backgroundColor = saveBackground
      textColor = Color.BLACK
    }
  }

}