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
import com.bradchristie.taminations.common.shapes.*
import com.bradchristie.taminations.platform.*

class AnimationPanelLayout : LinearLayout(Direction.VERTICAL) {

  val beatSlider: Slider = Slider()
  val ticView = SliderTicView()
  val startButton = ImageButton("<<",StartShape())
  val backButton = ImageButton("<",BackwardShape())
  val playButton = ImageButton("Play",PlayShape())
  val forwardButton = ImageButton(">",ForwardShape())
  val endButton = ImageButton(">>", EndShape())

  init {
    //  Add slider
    appendView(beatSlider) {
      height = 44
    }
    //  Add slider tics
    //  Wrapped in a dummy view to workaround a Chrome bug
    //  which disables some buttons below the tic view
    appendView(LinearLayout(Direction.HORIZONTAL)) {
      appendView(ticView) {
        backgroundColor = Color.TICS
        height = 40 max Application.screenHeight / 20
        weight = 0
      }
    }
    //  Add play buttons
    appendView(LinearLayout(Direction.HORIZONTAL)) {
      backgroundColor = Color.BLACK
      weight = 0
      listOf(appendView(startButton),
          appendView(backButton),
          appendView(playButton),
          appendView(forwardButton),
          appendView(endButton)).forEach {
        it.weight = 1
        it.margins = 4
      }
    }
  }

}