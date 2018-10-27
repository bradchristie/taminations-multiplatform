package com.bradchristie.taminations.platform
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

import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window

actual open class Canvas : View() {

  private val canvas:HTMLCanvasElement
      = document.createHTMLElement("canvas") { } as HTMLCanvasElement
  private var sized = false

  init {
    div.appendChild(canvas)
    invalidate()
    displayAction {
      setSize()
      invalidate()
    }
  }

  //  Canvas drawing does not work well unless the canvas dimensions
  //  are set to the pixel dimensions
  private fun setSize() {
    if (!sized && parentView!!.width > 0 && parentView!!.height > 0) {
      canvas.width = parentView!!.width
      canvas.height = parentView!!.height
      sized = true
    }
  }

  private fun getContext():DrawingContext {
    return DrawingContext(canvas.getContext("2d")!! as CanvasRenderingContext2D)
  }

  protected actual open fun onDraw(ctx:DrawingContext) { }

  actual fun invalidate() {
    window.requestAnimationFrame {
      setSize()  // just to make sure
      //  Seems that we need to avoid clobbering the context
      //  provided by the canvas
      val ctx = getContext()
      ctx.save()
      onDraw(ctx)
      ctx.restore()
    }
  }

}