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
import com.bradchristie.taminations.platform.Alert
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.*

class TutorialPage : Page() {

  override val view = PracticeLayout()
  private val model = TutorialModel(view)

  init {
    val av = view.animationView
    onAction(Request.Action.TUTORIAL) {
      model.nextAnimation("")
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      when (request["button"]) {
        "Repeat" -> {
          view.resultsPanel.hide()
          av.doPlay()
        }
        "Continue" -> model.nextAnimation("Continue")
        "Return" -> Application.goBack()
      }
    }
    onMessage(Request.Action.ANIMATION_DONE) {
      model.animationDone()
    }
  }

}

class TutorialModel(layout: PracticeLayout) : PracticeModel(layout) {

  companion object {
    val tutdata = if (Application.isTouch)
      arrayOf("Use your %1 finger on the %1 side of the screen. " +
        "Do not put your finger on the dancer. " +
        "Slide your finger forward to move the dancer forward. " +
        "Try to keep pace with the adjacent dancer.",

        "Use your %1 finger to follow the turning path."+
        "Try to keep pace with the adjacent dancer.",

        "Normally your dancer faces the direction you are moving. " +
            "But you can use your %2 finger to hold or change the facing direction. " +
            "Press and hold your %2 finger on the %2 side " +
            "of the screen.  This will keep your dancer facing forward. " +
            "Then use your %1 finger on the %1 side " +
            "of the screen to slide your dancer horizontally.",

        "Use your %2 finger to turn in place. " +
            "To U-Turn Left, make a 'C' movement with your %2 finger.")

      else arrayOf("Use your mouse to move the dancer forward",
        "Now use your mouse to follow a turning path",
        "Normally your dancer turns to face the direction you are moving. " +
            "Hold down the Shift key to keep your dancer from turning.",
        "Hold the Control key to turn your dancer in place without moving")
  }

  private var tutnum = 0

  private val primaryName get() = if (Setting("PrimaryControl").s == "Left") "Left" else "Right"
  private val secondaryName get() = if (Setting("PrimaryControl").s == "Left") "Right" else "Left"


  init {
    layout.definitionButton.hide()
    av.readyListener = {
      animationReady()
    }
  }

  override fun failure() {
    layout.continueButton.hide()
  }

  override fun success() {
    layout.continueButton.show()
    if (tutnum+1 >= tutdata.count()) {
      Alert("Tutorial Complete").apply {
        textView("Congratulations!  You have successfully completed the tutorial." +
            "  Now select the level you would like to practice.")
        okAction { Application.goBack() }
      }
      tutnum = 0
    }
  }

  override fun nextAnimation(level:String) {
    if (level == "Continue")
      tutnum += 1
    if (tutnum >= tutdata.count())
      tutnum = 0
    System.getXMLAsset("src/tutorial") { tutdoc ->
      val gender = if (Setting("PracticeGender").s == "Girl")
        Gender.GIRL else Gender.BOY
      val tamlist = tutdoc.evalXPath("/tamination/tam")
      val tam = tamlist[tutnum]
      av.setAnimation(tam, gender, intrand = false)
      Application.titleBar.title = tam.attr("title")
      Alert("Tutorial ${tutnum + 1}").apply {
        textView(tutdata[tutnum]
            .replace("%1",primaryName)
            .replace("%2",secondaryName))
        okAction {
          av.doPlay()
        }
      }
    }
  }

}