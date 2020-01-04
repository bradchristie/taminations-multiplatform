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

import com.bradchristie.taminations.platform.*

class DefinitionView : LinearLayout(Direction.VERTICAL) {

  private val frameView = WebView("")
  val buttonView: View
  lateinit var abbrevRB: RadioButton
  lateinit var fullRB: RadioButton

  init {
    appendView(frameView) {
      weight = 1
    }
    buttonView = radioGroup {
      weight = 0
      abbrevRB = radioButton("Abbreviated") { weight = 1; margins = 10 }
      fullRB = radioButton("Full") { weight = 1; margins = 10 }
    }
  }

  fun setSource(src:String, afterload:WebView.()->Unit) {
    frameView.setSource(src,afterload)
  }

  fun eval(script:String, code:(String)->Unit = { }) = frameView.eval(script,code)

}
