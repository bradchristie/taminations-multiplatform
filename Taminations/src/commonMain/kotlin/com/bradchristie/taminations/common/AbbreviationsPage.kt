package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.Color.Companion.RED
import com.bradchristie.taminations.common.Color.Companion.WHITE
import com.bradchristie.taminations.platform.*

//  These are abbreviations used in the sequencer.
//  There is a standard list of abbreviations,
//  the user can add or delete to them.
class AbbreviationsPage : Page() {

  override val view = AbbreviationsView()
  val model = AbbreviationModel(view)

  init {
    onAction(Request.Action.ABBREVIATIONS) {
      model.loadAbbreviations()
    }
    onMessage(Request.Action.ABBREVIATIONS_CHANGED) {
      model.saveAbbreviations()
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      when (request["button"]) {
        "Save" -> {
          model.copyAbbreviationsToClipboard()
          Alert("Sequencer").apply {
            textView("Abbreviations copied to clipboard")
            okAction { }
          }
        }
        "Load" -> {
          model.pasteAbbreviationsFromClipboard()
        }
        "Clear" -> {
          Alert("Sequencer").apply {
            textView("WARNING! This will erase ALL your abbreviations!")
            okAction(true) {
              model.clearAbbreviations()
            }
          }
        }
        "Default" -> {
          Alert("Sequencer").apply {
            textView("WARNING! This will REPLACE ALL your abbreviations!")
            okAction(true) {
              model.defaultAbbreviations()
            }
          }
        }
      }
    }
  }

}

//  The view is just two columns of editable text boxes
//  Pressing return in any box triggers the program to
//  register and save changes
class AbbreviationsView : LinearLayout(Direction.VERTICAL) {

  data class AbbreviationItem(val abbr:String, val expa:String)
  private val abbreviationList = ScrollingLinearLayout()
  private val saveButton = Button("Save")
  private val loadButton = Button("Load")
  private val clearButton = Button("Clear")
  private val resetButton = Button("Default")
  private val buttonLayout = LinearLayout(Direction.HORIZONTAL)

  init {
    appendView(abbreviationList) {
      weight = 1
    }
    appendView(buttonLayout) {
      backgroundColor = Color.BLACK
      weight = 0
      listOf(saveButton,loadButton,clearButton,resetButton).forEach {
        appendView(it)
        it.weight = 1
        it.margins = 4
      }
    }
  }

  fun addItem(abbrev:String="",expansion:String="") {
    abbreviationList.horizontalLayout {
      weight = 0
      appendView(TextInput()) {
        backgroundColor = WHITE
        text = abbrev
        weight = 1
        returnAction {
          Application.sendMessage(Request.Action.ABBREVIATIONS_CHANGED)
        }
        focus()
      }
      appendView(TextInput()) {
        backgroundColor = WHITE
        text = expansion
        weight = 4
        returnAction {
          Application.sendMessage(Request.Action.ABBREVIATIONS_CHANGED)
        }
      }
    }
  }

  override fun clear() {
    abbreviationList.clear()
  }

  val numItems:Int get() = abbreviationList.children.count()

  private fun abbrView(i:Int):TextInput =
      (abbreviationList.children[i] as ViewGroup).children[0] as TextInput
  private fun expaView(i:Int):TextInput =
      (abbreviationList.children[i] as ViewGroup).children[1] as TextInput

  operator fun get(i:Int):AbbreviationItem =
      AbbreviationItem(abbrView(i).text,expaView(i).text)

  fun clearErrors() {
    abbreviationList.children.forEach { child ->
      (child as ViewGroup).children[0].backgroundColor = WHITE
    }
  }
  fun markError(i:Int) {
    abbrView(i).backgroundColor = RED
  }
}



class AbbreviationModel(val view:AbbreviationsView) {

  companion object {
    private val initialAbbrev = mapOf(
        "g" to "Girls",
        "b" to "Boys",
        "c" to "Centers",
        "e" to "Ends",
        "h" to "Heads",
        "s" to "Sides",
        "ct" to "Courtesy Turn",
        "hs" to "Half Sashay",
        "pt" to "Pass Thru",
        "al" to "Allemande Left",
        "btl" to "Bend the Line",
        "rlg" to "Right and Left Grand",
        "rlt" to "Right and Left Thru",
        "sq2" to "Square Thru 2",
        "sq3" to "Square Thru 3",
        "sq4" to "Square Thru 4",
        "dpt" to "Double Pass Thru",
        "vl" to "Veer Left",
        "vr" to "Veer Right",
        "x" to "Cross",
        "xt" to "Extend",
        "fw" to "Ferris Wheel",
        "fl" to "Flutterwheel",
        "rf" to "Reverse Flutterwheel",
        "pto" to "Pass the Ocean",
        "st" to "Swing Thru",
        "tq" to "Touch a Quarter",
        "tb" to "Trade By",
        "whd" to "Wheel and Deal",
        "wa" to "Wheel Around",
        "zo" to "Zoom",
        "c34" to "Cast Off 3/4",
        "circ" to "Circulate",
        "ci" to "Centers In",
        "cl" to "Cloverleaf",
        "dx" to "Dixie Style to a Wave",
        "ht" to "Half Tag",
        "ptc" to "Pass to the Center",
        "sb" to "Scoot Back",
        "stt" to "Spin the Top",
        "ttl" to "Tag the Line",
        "wad" to "Walk and Dodge"
    )
  }

  init {
    //  Initialize with abbrevs above if 1st time
    if (Storage["+abbrev stored"] == null) {
      defaultAbbreviations()
      Storage["+abbrev stored"] = "true"
    }
  }

  fun clearAbbreviations() {
    Storage.keys.forEach {
      if (it.matches("abbrev \\S+".r))
        Storage.remove(it)
    }
    loadAbbreviations()
  }

  fun defaultAbbreviations() {
    clearAbbreviations()
    initialAbbrev.forEach { (key, value) ->
      Storage["abbrev $key"] = value
    }
    loadAbbreviations()
  }

  fun loadAbbreviations() {
    //  Read abbreviations previously stored and fill table
    view.clear()
    Storage.keys.sorted().forEach { key ->
      //  skip "*abbrev stored" and any other non-user stuff
      if (key.matches("abbrev \\S+".r))
        view.addItem(key.replace("abbrev ",""),Storage[key]!!)
    }
    //  Blank entry at end for writing a new abbrev
    view.addItem("","")
  }

  //  This routine is called when the user presses return
  fun saveAbbreviations() {
    //  First remove all the old abbreviations
    Storage.keys.forEach {
      if (it.matches("abbrev \\S+".r))
        Storage.remove(it)
    }
    //  Clear any old errors
    view.clearErrors()
    //  Process all the current abbreviations
    (0 until view.numItems).forEach { i ->
      if (!addOneAbbreviation(view[i].abbr,view[i].expa))
        view.markError(i)
    }
    //  Be sure we have a blank row at the bottom
    //  for adding a new abbreviation
    if (view[view.numItems-1].abbr.isNotBlank())
      view.addItem("","")
  }

  private fun addOneAbbreviation(abbr: String, expansion: String) : Boolean {
    return when {
      //  error if duplicate
      Storage["abbrev $abbr"] != null -> false
      //  error if a word used in calls
      abbr.lc in TamUtils.words -> false
      //  ok if a single non-blank string
      abbr.matches("\\S+".r) && expansion.isNotBlank() -> {
        Storage["abbrev $abbr"] = expansion
        true
      }
      //  otherwise (embedded spaces) error
      abbr.isNotBlank() -> false
      else -> true
    }
  }

  fun copyAbbreviationsToClipboard() {
    val text = Storage.keys.sorted().filter { it.matches("abbrev \\S+".r) }
        .map { "${it.replace("abbrev ","")} ${Storage[it]}" }
    System.copyTextToClipboard(text)
  }

  fun pasteAbbreviationsFromClipboard() {
    System.pasteTextFromClipboard { text ->
      text.split("\n").forEach { line ->
        val (abbr,expan) = line.split("\\s".r,2)
        addOneAbbreviation(abbr,expan)
      }
    }
  }


}