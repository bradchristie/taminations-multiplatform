package com.bradchristie.taminations.common
/*

  Taminations Square Dance Animations for Web Browsers
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

class SequencerReferencePage : Page() {

  override val view = SequencerReferenceView()
  private val model = SequencerReferenceModel(view)

  init {
    onAction(Request.Action.CALLLIST) {
      model.reset()
    }
    listOf(view.b1Checkbox,view.b2Checkbox,view.msCheckbox,
        view.plusCheckbox,view.a1Checkbox,view.a2Checkbox).forEach {
      it.clickAction {
        model.reset()
      }
    }
  }

}

class SequencerReferenceView : LinearLayout(Direction.VERTICAL) {

  val b1Checkbox = Checkbox("Basic 1")
  val b2Checkbox = Checkbox("Basic 2")
  val msCheckbox = Checkbox("Mainstream")
  val plusCheckbox = Checkbox("Plus")
  val a1Checkbox = Checkbox("A-1")
  val a2Checkbox = Checkbox("A-2")
  val calllist = CallListView()

  private fun ViewGroup.checkBox(cb:Checkbox) {
    val lev = LevelObject.find(cb.text)
    appendView(cb).apply {
      backgroundColor = lev.color
      borders.width = 1
      weight = 1
    }
  }

  init {
    // not valid on Android weight = 1
    horizontalLayout {
      weight = 0
      checkBox(b1Checkbox)
      checkBox(msCheckbox)
      checkBox(a1Checkbox)
    }
    horizontalLayout {
      weight = 0
      checkBox(b2Checkbox)
      checkBox(plusCheckbox)
      checkBox(a2Checkbox)
    }
    appendView(calllist) {
      weight = 1
    }
    calllist.searchInput.hide()
  }

}

class SequencerReferenceModel(val view:SequencerReferenceView) {

  fun reset() {
    view.calllist.clearItems()
    val selector = listOf(view.b1Checkbox,view.b2Checkbox,view.msCheckbox,
        view.plusCheckbox,view.a1Checkbox,view.a2Checkbox)
        .filter { it.isChecked }
        .joinToString(" | ") { LevelObject(it.text).selector }
    if (selector.isNotBlank()) {
      val list = TamUtils.calldoc.evalXPath(selector)
                         .sortedBy { it.getAttribute("title") }
      list.forEach {
        view.calllist.addItem(CallListData(
            it.getAttribute("title")!!,
            it.getAttribute("link")!!,
            it.getAttribute("sublevel")!!))
      }
    }
  }


}