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

class SequencerLayout : LinearLayout(Direction.VERTICAL) {

  val animationView = AnimationView()
  val panelLayout = AnimationPanelLayout()
  val beatText = TextView("")
  val callText = TextView("")
  private val abbrButton = Button("Abbrev")
  private val instructionsButton = Button("Help")
  private val settingsButton = Button("Settings")
  private val callsButton = Button("Calls")
  private val undoButton = Button("Undo")
  private val resetButton = Button("Reset")
  private val copyButton = Button("Copy")
  private val pasteButton = Button("Paste")
  val editButtons = LinearLayout(Direction.HORIZONTAL)
  val pageButtons = LinearLayout(Direction.HORIZONTAL)
  init {
    backgroundColor = Color.BLACK
    //  DOM layout only works with animation view inside
    //  a relative layout
    relativeLayout {
      weight = 1
      appendView(animationView) {
        fillParent()
      }
      appendView(beatText) {
        margins = 10
        textSize = 24
        alignParentRight()
        alignParentBottom()
      }
      appendView(callText) {
        margins = 10
        textSize = 24
        alignParentLeft()
        alignParentTop()
      }
    }
    appendView(panelLayout) {
      weight = 0
    }
    appendView(editButtons) {
      weight = 0
      appendView(undoButton) { weight = 1 }
      appendView(resetButton) { weight = 1 }
      appendView(copyButton) { weight = 1 }
      appendView(pasteButton) { weight = 1 }
    }
    appendView(pageButtons) {
      weight = 0
      appendView(instructionsButton) { weight = 1 }
      appendView(settingsButton) { weight = 1 }
      appendView(abbrButton) { weight = 1 }
      appendView(callsButton) { weight = 1 }
    }
  }

}