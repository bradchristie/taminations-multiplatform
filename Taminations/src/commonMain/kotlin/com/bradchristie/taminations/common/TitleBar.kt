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
import com.bradchristie.taminations.common.shapes.Logo
import com.bradchristie.taminations.common.shapes.SpeakerShape
import com.bradchristie.taminations.platform.*

class TitleBar : LinearLayout(Direction.HORIZONTAL) {

  private val titleView: TextView
  private val mylogo: ImageButton
  private val levelButton: Button
  private val shareButton: Button
  private val speakerButton: ImageButton
  init {
    linearGradient(Color(0, 192, 0), Color(0, 64, 0))
    mylogo = appendView(ImageButton("",Logo())) {
      gradientColor = Color.FLOOR.darker()
      height = 52.pp
      width = 52.pp
      margin.left = 10
      clickAction {
        Application.sendRequest(Request.Action.STARTUP)
      }
      weight = 0
      alignCenter()
    }
    titleView = appendView(TextView("Taminations")) {
      textColor = Color.WHITE
      textSize = 48.pp
      textStyle = "bold"
      shadow()
      weight = 1
      align = TextView.Align.CENTER
      alignCenter()
      autoSize = true
    }
    shareButton = appendView(System.shareButton()) {
      margin.right = 10
      weight = 0
      alignCenter()
      clickAction {
        System.share()
      }
    }
    speakerButton = appendView(ImageButton("",SpeakerShape())) {
      height = 52.pp
      width = 52.pp
      margin.right = 10
      weight = 0
      alignCenter()
      hide()
    }
    levelButton = appendView(Button(" ")) {
      margin.right = 10
      weight = 0
      alignCenter()
      clickAction {
        Application.sendRequest(Request.Action.CALLLIST,
            "level" to text)
      }
      hide()
    }

  }

  //  Get or set text for the level button on the right
  //  Setting to empty string hides the button
  var level:String
    get() = levelButton.text
    set(t) {
      if (t.isNotBlank()) levelButton.show() else levelButton.hide()
      levelButton.text = t
  }

  //  Get or set the title text in the middle
  var title:String
    get() = titleView.text
    set(value) {
      titleView.text = value
      //  See if there is an audio file for this title
      val calls = TamUtils.calldoc.evalXPath("/calls/call[@title=${title.quote()}]")
      if (calls.isNotEmpty() && calls[0].hasAttribute("audio")) {
        val audiofile = calls[0].attr("audio")
        val audio = System.audio(audiofile)
        //  force redraw so it shows
        speakerButton.setImage(SpeakerShape())
        speakerButton.show()
        speakerButton.clickAction {
          audio.play()
        }
      } else
        speakerButton.hide()
    }

}