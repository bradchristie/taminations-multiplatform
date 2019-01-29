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
import com.bradchristie.taminations.platform.Button

class AnimationLayout : LinearLayout(Direction.VERTICAL) {

  val animationView = AnimationView()
  val panelLayout = AnimationPanelLayout()
  val saysText = TextView("")
  val itemText = TextView("0 of 0")
  private val definitionButton = Button("Definition")
  private val settingsButton = Button("Settings")
  val buttonLayout = LinearLayout(Direction.HORIZONTAL)

  init {
    borders.width = 1
    backgroundColor = Color.GRAY
    //  Animation area
    relativeLayout {
      weight = 1
      appendView(animationView) {
        fillParent()
      }
      appendView(saysText) {
        backgroundColor = Color.WHITE
        margins = 6
        alignParentTop()
        alignParentRight()
      }
      appendView(itemText) {
        margins = 6
        alignParentBottom()
        alignParentRight()
      }
    }
    //  Animation panel - slider, buttons, etc
    appendView(panelLayout) {
      weight = 0
    }
    //  Buttons on bottom
    appendView(buttonLayout) {
      weight = 0
      backgroundColor = Color.BLACK
      appendView(definitionButton) {
        weight = 1
        margin.left = 4
        margin.right = 4
      }
      appendView(settingsButton) {
        weight = 1
        margin.left = 4
        margin.right = 4
      }
    }
  }

}