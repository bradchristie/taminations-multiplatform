package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.SelectablePanel
import com.bradchristie.taminations.platform.*

class LevelPage : Page() {
  override val view = LevelView()

  override fun doRequest(request: Request): Boolean = when (request.action) {
    Request.Action.LEVEL -> true
    Request.Action.STARTUP -> if (Application.isPortrait) {
      Application.titleBar.title = "Taminations"
      true
    } else false
    else -> false
  }

  override fun canDoAction(action: Request.Action): Boolean =
      action == Request.Action.LEVEL ||
          (Application.isPortrait && action == Request.Action.STARTUP)

  init {
    view.levelAction = { level ->
      val action = when (level) {
        "About" -> Request.Action.ABOUT
        "Settings" -> Request.Action.SETTINGS
        "Sequencer" -> Request.Action.SEQUENCER
        "Practice" -> Request.Action.STARTPRACTICE
        else -> Request.Action.CALLLIST
      }
      Application.sendRequest(action,"level" to level)
    }
  }

}



class LevelView : LinearLayout(Direction.VERTICAL) {

  private inner class OneLevelView(lev:String) : SelectablePanel() {

    val textView : TextView
    var text:String
      get() = textView.text
      set(t) { textView.text = t }
    init {
      border.top.width = 1
      val level = LevelObject.find(lev)
      backgroundColor = level.color
      textView = textView(level.name) {
        textSize = 25.pp
        textStyle = "bold"
        alignCenter()
        margin.left = 40.pp
      }
      clickAction {
        levelAction(text)
      }
    }
  }
  private fun ViewGroup.oneView(lev:String) : OneLevelView =
      appendView(OneLevelView(lev)).apply { weight = 1 }

  private fun ViewGroup.indentedOneLevelView(lev:String,sublev:String) {
    horizontalLayout {
      weight = 1
      appendView(View()) {
        backgroundColor = LevelObject.find(lev).color
        weight = 1
      }
      oneView(sublev).apply {
        border.left.width = 1
        weight = 9
      }
    }

  }

  var levelAction: (level:String)->Unit = { }

  init {
    backgroundColor = LevelObject.find("all").color
    border.right.width = 1
    //  Basic and Mainstream
    oneView("bms")
    indentedOneLevelView("bms", "b1")
    indentedOneLevelView("bms", "b2")
    indentedOneLevelView("bms", "ms")
    oneView("plus")
    //  Advanced
    oneView("adv")
    indentedOneLevelView("adv", "a1")
    indentedOneLevelView("adv", "a2")
    //  Challenge
    oneView("challenge")

    indentedOneLevelView("challenge", "c1")
    indentedOneLevelView("challenge", "c2")
    indentedOneLevelView("challenge", "c3a")
    indentedOneLevelView("challenge", "c3b")
    //  Other buttons
    oneView("all").text = "Index of All Calls"
    //  Tweaks for putting two items on one line
    horizontalLayout {
      weight = 1
      oneView("all").text = "Practice"
      oneView("all").apply {
        border.left.width = 1
        text = "Sequencer"
      }
    }
    horizontalLayout {
      weight = 1
      oneView("all").text = "About"
      oneView("all").apply {
        border.left.width = 1
        text = "Settings"
      }
    }

  }

}