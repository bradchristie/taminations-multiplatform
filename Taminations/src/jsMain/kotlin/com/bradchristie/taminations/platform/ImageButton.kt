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

import kotlin.properties.Delegates

actual class ImageButton actual constructor(name:String, c: Shape) : Button(name) {

  private val canvasView = object : ViewGroup() { }
  var canvas: Shape by Delegates.observable(c) { _, _, _ ->
    canvasView.clear()
    canvasView.appendView(canvas)
    canvas.invalidate()
  }

  init {
    textView.hide()
    style.justifyContent = "center"
    layout.appendView(canvasView) {
      style.width = 32.dips
      style.height = 32.dips
      style.marginBottom = 4.dips
      appendView(canvas)
    }
  }

  actual fun setImage(shape:Shape) {
    canvas = shape
  }

}