package com.bradchristie.taminations.platform
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
import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.common.Request
import com.bradchristie.taminations.common.i
import com.bradchristie.taminations.common.s
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import kotlin.browser.document

actual class Slider : View() {

  private val layout = RelativeLayout(div)
  private val slideBar: View
  private val slideBox: View
  private var currentPos = 0
  private var currentValue = 0.0
  private var range = Pair(0.0,100.0)
  private var slideCode:(Double)->Unit = { }

  init {
    style.position = "relative"
    slideBar = layout.appendView {
      style.position = "absolute"
      style.width = "100%"
      style.height = "40%"
      margin.top = 12
      backgroundColor = Color.LIGHTGRAY
    }
    slideBox = layout.appendView {
      style.position = "absolute"
      width = 34
      height = 34
      backgroundColor = Color.YELLOW
      borderRadius = 10
      borders.width = 2
      div.onmousedown = { event -> startDrag(event.screenX) }
    }
  }

  actual fun slideAction(code:(Double)->Unit) {
    slideCode = code
  }

  private fun startDrag(startX:Int) {
    slideBox.backgroundColor = Color.RED
    val startPos = currentPos
    document.body?.onmousemove = { ev: Event ->
      val mev = ev as MouseEvent
      val dx = mev.screenX - startX
      if (dx+startPos >= 0 && dx+startPos <= slideBar.width-40) {
        currentPos = startPos + dx
        slideBox.style.left = "${currentPos}px"
        currentValue = (currentPos)*(range.second-range.first)/(slideBar.width-40)
        slideCode(currentValue)
        Application.sendMessage(Request.Action.SLIDER_CHANGE,
            "value" to currentValue.s)
      }
      0
    }
    document.body?.onmouseup = {
      slideBox.backgroundColor = Color.YELLOW
      document.body?.onmousemove = { }
      0
    }
  }

  actual fun setValue(v:Double) {
    currentValue = v
    currentPos = ((currentValue-range.first)/(range.second-range.first) * (slideBar.width-40.0)).i
    slideBox.style.left = "${currentPos}px"
  }

}