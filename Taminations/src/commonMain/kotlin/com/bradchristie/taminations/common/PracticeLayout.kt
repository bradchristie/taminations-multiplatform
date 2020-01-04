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
import com.bradchristie.taminations.platform.Button

class PracticeLayout : RelativeLayout() {

  val animationView = AnimationView()
  val repeatButton = Button("Repeat")
  val continueButton = Button("Continue")
  val returnButton = Button("Return")
  val definitionButton = Button("Definition")
  var resultsPanel: View
  val scoreNumbers = TextView("0 / 0")
  val scoreText = TextView("Poor")
  val definitionView = DefinitionView()

  init {
    //appendView(definitionView)
   // relativeLayout {
      //  Animation
      appendView(animationView) {
        fillParent()
      }
      //  Results panel
      resultsPanel = verticalLayout {
        width = Application.screenHeight/2
        margins = 20
        alignParentLeft()
        alignParentTop()
        listOf(textView("Animation Complete"),
        textView("Your Score"),
        appendView(scoreNumbers),
        appendView(scoreText)).forEach {
          it.align = TextView.Align.CENTER
          it.textSize = 32.pp
          it.margins = 8
        }
        horizontalLayout {
          listOf(appendView(repeatButton),
          appendView(continueButton),
          appendView(returnButton)).forEach {
            it.weight = 1
          }
        }
        horizontalLayout {
          appendView(definitionButton).weight = 1
        }
      }
      //  Running score

  }


}