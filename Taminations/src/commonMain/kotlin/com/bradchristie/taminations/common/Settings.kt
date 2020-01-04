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
import com.bradchristie.taminations.platform.*
import com.bradchristie.taminations.platform.Page

class SettingsPage : Page() {

  override val view = SettingsView()

  init {
    onAction(Request.Action.SETTINGS) {
      view.showAnimationSettings()
      if (Application.isPortrait) {
        Application.titleBar.title = "Settings"
        Application.titleBar.level = ""
      }
    }
  }

}

class SettingsView : ScrollingLinearLayout() {

  private fun ViewGroup.checkBox(text:String, hint:String,
                                 default:Boolean = false) {
    verticalLayout {
      margin.bottom = 10
      backgroundColor = Color.FLOOR
      appendView(Checkbox(text)).apply {
        backgroundColor = Color.FLOOR
        textSize = 24
        textStyle = "bold"
        margin.left = 10
        isChecked = Setting(text).b ?: default
        clickAction {
          Setting(text).b = isChecked
          Application.sendMessage(Request.Action.SETTINGS_CHANGED)
        }
      }
      textView(hint) {
        margin.left = 10
      }
    }
  }

  private fun ViewGroup.radioButtons(name:String, default: String,
                                     vararg buttons:Pair<String,String>,
                                     clickCode:(String)->Unit={ }):LinearLayout =
    verticalLayout {
      margin.bottom = 10
      backgroundColor = Color.FLOOR
      val hintText = TextView("")
      textView(name) {
        textSize = 24
        textStyle = "bold"
        margin.left = 10
      }
      var showHints = false
      radioGroup {
        margin.left = 10
        buttons.forEach { (buttonText, hint) ->
          radioButton(buttonText) {
            isChecked = Setting(name).s == buttonText ||
                (Setting(name).s.isNullOrBlank() && buttonText == default)
            if (isChecked)
              hintText.text = hint
            clickAction {
              Setting(name).s = buttonText
              hintText.text = hint
              clickCode(buttonText)
              Application.sendMessage(Request.Action.SETTINGS_CHANGED)
            }
            if (hint.isNotBlank())
              showHints = true
          }
        }
      }
      if (showHints)
        appendView(hintText).margin.left = 10
    }


  private fun ViewGroup.dancerColors(withRadio:Boolean) {
    val colorBar = LinearLayout(LinearLayout.Direction.HORIZONTAL).apply {
      dancerColorDropBox("Couple 1")
      dancerColorDropBox("Couple 2")
      dancerColorDropBox("Couple 3")
      dancerColorDropBox("Couple 4")
    }
    verticalLayout {
      margin.bottom = 10
      backgroundColor = Color.FLOOR
      if (withRadio)
        radioButtons("Dancer Colors", "By Couple",
          "By Couple" to "",
          "Random" to "",
          "None" to "") {
          if (it == "By Couple")
            colorBar.show()
          else
            colorBar.hide()
        }
      else {
        textView("Dancer Colors") {
          textSize = 24
          textStyle = "bold"
          margin.left = 10
        }
      }
      appendView(colorBar).margin.left = 10
      if ((Setting("Dancer Colors").s ?: "By Couple") != "By Couple")
        colorBar.hide()
      else
        colorBar.show()
    }
  }

  private fun colorForCouple(name:String):Color =
      Color(Setting(name).s ?: when (name) {
    "Couple 1" -> "red"
    "Couple 2" -> "green"
    "Couple 3" -> "blue"
    "Couple 4" -> "yellow"
    else -> "white"})

  private fun ViewGroup.dancerColorDropBox(name:String) {
    //  Not enough room for "Couple 1" ...
    //  These strings with spaces contain unicode 160 so HTML doesn't collapse them
    dropDown("    "+name.replace("Couple ","")+"    ") {
      selectAction { item ->
        Setting(name).s = item
        Application.sendMessage(Request.Action.SETTINGS_CHANGED)
        backgroundColor = Color(item)
        textColor = when (item) {
          "Black", "Blue" -> Color.WHITE
          else -> Color.BLACK
        }
      }
      margin.right = 20
      backgroundColor = colorForCouple(name)
      textColor = when (colorForCouple(name)) {
        Color.BLACK, Color.BLUE -> Color.WHITE
        else -> Color.BLACK
      }
      addItem("Black") {
        backgroundColor = Color.BLACK
        textColor = Color.WHITE
      }
      addItem("Blue") {
        backgroundColor = Color.BLUE
        textColor = Color.WHITE
      }
      listOf(
          addItem("Cyan") { backgroundColor = Color.CYAN },
          addItem("Gray") { backgroundColor = Color.GRAY },
          addItem("Green") { backgroundColor = Color.GREEN },
          addItem("Magenta") { backgroundColor = Color.MAGENTA },
          addItem("Orange") { backgroundColor = Color.ORANGE },
          addItem("Red") { backgroundColor = Color.RED },
          addItem("White") { backgroundColor = Color.WHITE },
          addItem("Yellow") { backgroundColor = Color.YELLOW }).forEach {
        it.textColor = Color.BLACK
      }
    }
  }

  fun showAnimationSettings() {
    clear()
    backgroundColor = Color.GRAY

    radioButtons("Dancer Speed", "Normal",
        "Slow" to "Dancers move at a Slow pace",
        "Normal" to "Dancers move at a Normal pace",
        "Fast" to "Dancers move at a Fast pace")

    checkBox("Loop", "Repeat the animation continuously")

    checkBox("Grid", "Show a dancer-sized grid")

    checkBox("Paths", "Draw a line for each dancer's route")

    radioButtons("Numbers", "None",
        "None" to "Dancers not numbered",
        "1-8" to "Number dancers 1-8",
        "1-4" to "Number couples 1-4")

    dancerColors(withRadio = false)

    checkBox("Phantoms", "Show phantom dancers where used for Challenge calls")

    radioButtons("Special Geometry", "None",
        "None" to "",
        "Hexagon" to "",
        "Bi-gon" to "")

    radioButtons("Transitions", "Fade",
        "None" to "",
        "Fade" to "",
        "Fade and Zoom" to "")

    radioButtons("Language for Definitions","System",
        "System" to "Prefer system language, else English",
        "English" to "Always show English",
        "German" to "Prefer German, else English",
        "Japanese" to "Prefer Japanese, else English")

  }


  fun showSequencerSettings() {
    clear()
    backgroundColor = Color.GRAY
    val other = TextInput()
    radioButtons("Starting Formation", "Squared Set",
        "Facing Couples" to "",
        "Squared Set" to "",
        "Normal Lines" to "") {
      //  action when any of these buttons are set
      other.text = ""
    }.apply {
      horizontalLayout {
        backgroundColor = Color.FLOOR
        textView("Other:")
        other.keyAction {
          try {
            TamUtils.getFormation(other.text)
            //  if no exception thrown, formation exists
            Setting("Starting Formation").s = other.text
            other.textColor = Color.BLACK
            //  Unset all the radio buttons when custom formation set
            this@apply.onDescendants {
              if (this is RadioButton)
                isChecked = false
            }
            Application.sendMessage(Request.Action.SETTINGS_CHANGED)
          } catch (e: NoSuchElementException) {
            other.textColor = Color.RED
          }
        }
        if (!listOf("Facing Couples", "Squared Set", "Normal Lines").contains(Setting("Starting Formation").s))
          other.text = Setting("Starting Formation").s ?: ""
        appendView(other) {
          height = 20
        }
      }
    }

    radioButtons("Dancer Speed", "Normal",
        "Slow" to "Dancers move at a Slow pace",
        "Normal" to "Dancers move at a Normal pace",
        "Fast" to "Dancers move at a Fast pace")

    checkBox("Grid", "Show a dancer-sized grid")

    dancerColors(withRadio = true)

    checkBox("Dancer Shapes", "", true)
    radioButtons("Dancer Identification", "None",
        "None" to "",
        "Dancer Numbers" to "",
        "Couple Numbers" to "",
        "Names" to "")

    /*  not yet
      radioButtons("Special Geometry", "None",
          "None" to "",
          "Hexagon" to "",
          "Bi-gon" to "")
       */


  }

}