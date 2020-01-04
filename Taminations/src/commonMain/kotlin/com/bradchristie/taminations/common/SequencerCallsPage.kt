package com.bradchristie.taminations.common
/*

  Taminations Square Dance Animations
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.shapes.MikeShape
import com.bradchristie.taminations.platform.*

class SequencerCallsPage : Page() {

  val textInput = TextInput()
  private val callList = LinearLayout(LinearLayout.Direction.VERTICAL).apply {
    isScrollable = true
  }
  val errorText = TextView("")
  private var highlightedCall = -1
  private var savedBackground = Color.BLACK
  private val mike = MikeShape()
  private val mikeButton = ImageButton("",mike)
  var listening = false

  override val view = LinearLayout(LinearLayout.Direction.VERTICAL).apply {
    backgroundColor = Color.LIGHTGRAY
    border.right.width = 1
    appendView(callList)
    horizontalLayout {
      weight = 0
      appendView(textInput) {
        weight = 1
        borders.width = 2
        borders.color = Color.ORANGE
        margin.top = 4
        margin.bottom = 4
        margin.left = 12
        margin.right = 12
      }
      if (CallListener.available) {
        appendView(mikeButton) {
          height = 52
          width = 52
          margin.right = 10
          weight = 0
        }
        mikeButton.clickAction {
          listening = !listening
          mike.color = if (listening) Color.RED else Color.BLACK
          Application.sendMessage(Request.Action.SEQUENCER_LISTEN)
        }
      }
    }
    appendView(errorText) {
      backgroundColor = Color.WHITE
      textColor = Color.RED
      margin.left = 20
      weight = 0
    }
  }

  fun addCall(call:String, level: LevelObject.LevelData) {
    callList.selectablePanel {
      val item = callList.children.lastIndex
      clickAction {
        Application.sendMessage(Request.Action.SEQUENCER_CURRENTCALL,
            "item" to "$item")
      }
      backgroundColor = level.color
      weight = 0
      textView(call) {
        textSize = 18
        weight = 1
        margin.top = 4
        margin.bottom = 4
        margin.right = 12
        margin.left = 4
      }
      textView(level.name) {
        textSize = 12
        weight = 0
      }
    }
    callList.scrollToBottom()
  }

  fun highlightCall(call:Int) {
    if (highlightedCall >= 0)
      callList.children[highlightedCall].backgroundColor = savedBackground
    if (call >= 0 && call < callList.children.count()) {
      val v = callList.children[call]
      savedBackground = v.backgroundColor
      v.backgroundColor = Color.YELLOW
      highlightedCall = call
    } else
      highlightedCall = -1
  }

  fun removeLastCall() {
    callList.removeView(callList.children[callList.children.lastIndex])
    if (highlightedCall >= callList.children.count())
      highlightedCall = -1
  }

  fun clearError() {
    errorText.text = ""
    errorText.hide()
  }

  fun clear() {
    callList.clear()
    clearError()
    textInput.text = ""
    highlightedCall = -1
  }

}