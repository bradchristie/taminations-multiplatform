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
import com.bradchristie.taminations.platform.*
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.SelectablePanel

class StartPracticePage : Page() {

  override val view = StartPracticeLayout()

  init {
    onAction(Request.Action.STARTPRACTICE) { }
  }

}

class StartPracticeLayout : LinearLayout(Direction.HORIZONTAL) {

  inner class StartPracticeLevelView(level:String) : SelectablePanel() {
    init {
      backgroundColor = if (level == "Tutorial")
        Color.LIGHTGRAY
      else
        LevelObject.find(level).color
      borders.width = 1
      textView(level) {
        fillHorizontal()
        alignCenter()
        textSize = 25.pp
        textStyle = "bold"
        align = TextView.Align.CENTER
      }
      clickAction {
        if (level == "Tutorial")
          Application.sendRequest(Request.Action.TUTORIAL)
        else
          Application.sendRequest(Request.Action.PRACTICE,"level" to level)
      }
    }
  }
  private fun ViewGroup.spLevelView(level:String) {
    appendView(StartPracticeLevelView(level)) {
      weight = 1
    }
  }

  init {
    //  Left side - options
    verticalLayout {
      weight = 1
      paddings = 8
      textView("Choose a Gender").textSize = 36.pp
      radioGroup {
        margins = 8
        radioButton("Boy") {
          isChecked = Setting("PracticeGender").s == "Boy"
          clickAction {
            Setting("PracticeGender").s = "Boy"
          }
        }
        radioButton("Girl") {
          isChecked = Setting("PracticeGender").s != "Boy"
          clickAction {
            Setting("PracticeGender").s = "Girl"
          }
        }
      }

      textView("Speed for Practice").textSize = 36.pp
      radioGroup {
        radioButton("Slow") {
          isChecked = Setting("PracticeSpeed").s == "Slow"
          clickAction {
            Setting("PracticeSpeed").s = "Slow"
          }
        }
        radioButton("Moderate") {
          isChecked = Setting("PracticeSpeed").s != "Slow" &&
                      Setting("PracticeSpeed").s != "Normal"
          clickAction {
            Setting("PracticeSpeed").s = "Moderate"
          }

        }
        radioButton("Normal") {
          isChecked = Setting("PracticeSpeed").s == "Normal"
          clickAction {
            Setting("PracticeSpeed").s = "Normal"
          }
        }
      }

      if (Application.isTouch) {
        textView("Primary Control")
        radioGroup {
          radioButton("Right Finger") {
            isChecked = Setting("PrimaryControl").s != "Left"
            clickAction {
              Setting("PrimaryControl").s = "Right"
            }
          }
          radioButton("Left Finger") {
            isChecked = Setting("PrimaryControl").s == "Left"
            clickAction {
              Setting("PrimaryControl").s = "Left"
            }
          }
        }


      } else {
        textView("Mouse Control").textSize = 36
        radioGroup(Direction.VERTICAL) {
          radioButton("Dancer moves only when mouse button is pressed") {
            isChecked = Setting("PracticeMousePressed").b != false
            clickAction {
              Setting("PracticeMousePressed").b = true
            }
          }
          radioButton("Dancer moves only when mouse button is released") {
            isChecked = Setting("PracticeMousePressed").b == false
            clickAction {
              Setting("PracticeMousePressed").b = false
            }
          }
        }
      }

    }

    //  Right side - levels and tutorial
    verticalLayout {
      weight = 1
      spLevelView("Tutorial")
      horizontalLayout {
        weight = 1
        spLevelView("Basic 1")
        spLevelView("Basic 2")
      }
      horizontalLayout {
        weight = 1
        spLevelView("Mainstream")
        spLevelView("Plus")
      }
      horizontalLayout {
        weight = 1
        spLevelView("A-1")
        spLevelView("A-2")
      }
      horizontalLayout {
        weight = 1
        spLevelView("C-1")
        spLevelView("C-2")
      }
      horizontalLayout {
        weight = 1
        spLevelView("C-3A")
        spLevelView("C-3B")
      }

    }
  }

}