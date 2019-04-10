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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.TamUtils.calldoc
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.*

class PracticePage : Page() {

  override val view = PracticeLayout()
  private val model = PracticeModel(view)
  private var level = ""
  private var showingDefinition = false

  init {
    val av = view.animationView
    onAction(Request.Action.PRACTICE) { request ->
      if (!showingDefinition) {
        level = request["level"]
        model.nextAnimation(level)
      }
      showingDefinition = false
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      when (request["button"]) {
        "Repeat" -> {
          view.resultsPanel.hide()
          av.doPlay()
        }
        "Continue" -> model.nextAnimation(level)
        "Return" -> Application.goBack()
        "Definition" -> {
          showingDefinition = true
          Application.sendRequest(Request.Action.DEFINITION,
              "link" to model.link)
        }
      }
    }
    onMessage(Request.Action.ANIMATION_DONE) {
      model.animationDone()
    }
  }

}

open class PracticeModel(val layout: PracticeLayout) {

  var level:String = ""
  var link:String = ""
  protected val av = layout.animationView

  init {
    //  TODO remove readyListener, just use message
    av.readyListener = {
      animationReady()
      av.doPlay()
    }
  }

  fun animationReady() {
    av.setSpeed(Setting("PracticeSpeed").s ?: "Slow")
    av.setGridVisibility(true)
    layout.resultsPanel.hide()
  }

  fun animationDone() {
    layout.scoreNumbers.text = "${av.score.ceil.i} / ${(av.movingBeats * 10).i}"
    val result = av.score/(av.movingBeats*10)
    layout.scoreText.text = when {
      result >= 0.9 -> { success(); "Excellent!" }
      result >= 0.7 -> { success(); "Very Good!" }
      else -> { failure(); "Poor" }
    }
    layout.resultsPanel.show()
  }

  open fun failure() { }
  open fun success() { }

  open fun nextAnimation(level:String) {
    this.level = level
    val selector = LevelObject.find(level).selector
    val calls = calldoc.evalXPath(selector).toMutableList()
    var tam: TamElement
    calls.shuffle().first().let { e ->
      //  Remember link for definition
      link = e.attr("link")
      System.getXMLAsset(link) { tamdoc ->
        val tams = tamdoc.evalXPath("/tamination/tam")
            //  For now, skip any "difficult" animations
            .filter { e2 -> e2.getAttribute("difficulty") != "3" }
            //  Skip any call with parens in the title - it could be a cross-reference
            //  to a concept call from a higher level
            .filter { e2 -> !e2.attr("title").contains("(") }
        if (tams.isNotEmpty()) {
          tam = tams.shuffle().first()
          //  Normally select a random dancer
          var randomDancer = true
          //  But if the animations starts with "Heads" or "Sides"
          //  then select the first dancer.
          //  Otherwise the formation could rotate 90 degrees
          //  which would be confusing
          val title = tam.attr("title")
          if (title.contains("Heads") || title.contains("Sides"))
            randomDancer = false
          layout.animationView.setAnimation(tam, if (Setting("PracticeGender").s == "Boy") Gender.BOY else Gender.GIRL, randomDancer)
          Application.titleBar.title = tam.attr("title")
        } else {
          nextAnimation(level)
        }
      }
    }

  }
}