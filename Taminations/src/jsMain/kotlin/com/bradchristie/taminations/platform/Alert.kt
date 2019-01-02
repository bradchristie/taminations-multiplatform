package com.bradchristie.taminations.platform
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

import com.bradchristie.taminations.common.Color
import org.w3c.dom.HTMLElement
import kotlin.browser.document

/*
   Alert displays a model dialog when it is created.
   Add content like any other View.
   Use okAction to add an OK button and optional Cancel button.
 */

actual class Alert
  actual constructor(title:String)
  : LinearLayout(Direction.HORIZONTAL) {

  private val body = RelativeLayout(document.querySelector("body") as HTMLElement)
  private val content: LinearLayout

  init {
    style.position = "absolute"
    style.top = "0"
    style.left = "0"
    style.width = "100%"
    style.height = "100%"
    style.background = "rgba(196,196,196,0.6)"
    style.justifyContent = "center"
    style.alignItems = "center"
    content = LinearLayout(Direction.VERTICAL).apply {
      borders.width = 1
      backgroundColor = Color.WHITE
      textView(title) {
        textStyle = "bold"
        backgroundColor = Color.BLUE.darker()
        textColor = Color.WHITE
        paddings = 8
      }
    }
    //  Be careful not to call the override method below!
    super.appendView(content)
    body.appendView(this)
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit) =
      content.appendView(child, code).apply {
        style.margin = 8.dips
      }

  override fun appendHTMLElement(tag: String,
                                 code: HTMLElement.() -> Unit) =
    content.appendHTMLElement(tag, code)

  actual fun okAction(cancel:Boolean, code:()->Unit) {
    horizontalLayout {
      style.justifyContent = "center"
      button("Ok") {
        style.margin = 8.dips
        weight = 0
        clickAction {
          body.removeView(this@Alert)
          code()
        }
      }
      if (cancel) {
        button("Cancel") {
          style.margin = 8.dips
          weight = 0
          clickAction {
            body.removeView(this@Alert)
          }
        }
      }
    }
  }

}
