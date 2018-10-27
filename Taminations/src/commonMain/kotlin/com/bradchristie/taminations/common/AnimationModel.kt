package com.bradchristie.taminations.common
/*

  Taminations Square Dance Animations for Web Browsers
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
import com.bradchristie.taminations.common.shapes.BackwardShape
import com.bradchristie.taminations.common.shapes.PauseShape
import com.bradchristie.taminations.common.shapes.PlayShape
import com.bradchristie.taminations.platform.*

open class AnimationPage : Page() {

  final override val view = AnimationLayout()
  private lateinit var model: AnimationModel
  private lateinit var panelmodel: AnimationPanelModel
  private var animnum = -1

  init {
    val av = view.animationView
    val ap = view.panelLayout
    var saveRequest = Request(Request.Action.ANIMATION)
    if (Setting("embed").b == true)
      view.buttonLayout.hide()
    onAction(Request.Action.ANIMATION) { request ->
      saveRequest = request
      animnum = if (request["animnum"].isNotEmpty())
        request["animnum"].i else -1
      model = AnimationModel(view,
          request["link"], animnum, request["name"])
      panelmodel = AnimationPanelModel(ap, av)
    }
    onMessage(Request.Action.ANIMATION_LOADED) {
      view.panelLayout.ticView.setTics(av.totalBeats, av.partsstr, isParts = av.hasParts)
      av.readAnimationSettings()
      if (Setting("Play").b == true) {
        ap.playButton.setImage(PauseShape())
        av.doPlay()
      }
      view.itemText.text = "${animnum+1} of ${model.tamcount}"
    }
    onMessage(Request.Action.SETTINGS_CHANGED) {
      av.readAnimationSettings()
    }
    onMessage(Request.Action.ANIMATION_PROGRESS) { message ->
      val beat = message["beat"].d
      ap.beatSlider.setValue(beat * 100.0 / av.totalBeats)
      val alpha = ((2.0 - beat) / 2.01 max 0.0)
      view.saysText.opacity = alpha
    }
    onMessage(Request.Action.ANIMATION_DONE) {
      ap.playButton.setImage(PlayShape())
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      if (Application.isPortrait) {
        when (request["button"]) {
          "Settings" -> Application.sendRequest(Request.Action.SETTINGS)
          "Definition" -> Application.sendRequest(Request.Action.DEFINITION,
              "link" to model.link)
        }
      }
    }
    if (Application.isPortrait) {
      view.itemText.show()
      view.swipeAction { direction ->
        when (direction) {
          View.SwipeDirection.LEFT -> {
            if (animnum < model.tamcount-1) {
              saveRequest["animnum"] = (animnum + 1).s
              Application.sendRequest(saveRequest)
            }
          }
          View.SwipeDirection.RIGHT -> {
            if (animnum > 0) {
              saveRequest["animnum"] = (animnum - 1).s
              Application.sendRequest(saveRequest)
            }
          }
          else -> {
          }
        }
      }
    } else
      view.itemText.hide()
  }
}

class AnimationModel(private val layout: AnimationLayout,
                     val link:String, anim:Int=(-1), name:String="") {

  val String.w get() = this.replace(Regex("\\W"),"")
  var tamcount = 0
  init {
    //  Fetch the XML animation and send it to the animation view
    System.getXMLAsset(link) { tamdoc ->
      val alltams = tamdoc.tamList().filter { tam -> tam.attr("display") != "none" }
      val tam = when {
        anim >= 0 -> alltams[anim]
        name.isNotBlank() ->
          alltams.firstOrNull { tam ->
            name.w == tam.attr("title").w +
                if (tam.hasAttribute("group")) ""
                else "from" + tam.attr("from").w
          } ?: alltams[0]  // title mismatch, should not happen
        else ->
          alltams[0]  //  no title or anim num given
      }
      Application.titleBar.title = tam.attr("title").replace(Regex("\\(.*\\)"),"")
      layout.animationView.setAnimation(tam)
      val tamsaysa = tam.evalXPath("taminator")
      if (tamsaysa.count() > 0) {
        val tamsays = tam.evalXPath("taminator")
            .firstOrNull()?.textContent?.trim()?.replace(Regex("\\s+")," ") ?: " "
        layout.saysText.text = tamsays
        layout.saysText.opacity = 1.0
        layout.saysText.show()
      } else
        layout.saysText.hide()
      tamcount = alltams.count()
    }
  }

}

class AnimationPanelModel(private val ap: AnimationPanelLayout,
                          private val av: AnimationView) {
  init {
    //  Hook up controls
    ap.startButton.clickAction { av.doPrevPart() }
    ap.backButton.clickAction { av.doBackup() }
    ap.playButton.clickAction {
      if (av.isRunning) {
        av.doPause()
        ap.playButton.setImage(PlayShape())
      } else {
        av.doPlay()
        ap.playButton.setImage(PauseShape())
      }
    }
    ap.forwardButton.clickAction { av.doForward() }
    ap.endButton.clickAction { av.doNextPart() }
    av.wheelAction { deltaY ->
      if (deltaY < 0)
        av.doBackup()
      else if (deltaY > 0)
        av.doForward()
    }
    ap.beatSlider.slideAction { value ->
      av.setTime(value*av.totalBeats/100.0)
    }
    //  Make sure play button is Play, in case user left previous
    //  animation while running
    ap.playButton.setImage(PlayShape())
  }
}

