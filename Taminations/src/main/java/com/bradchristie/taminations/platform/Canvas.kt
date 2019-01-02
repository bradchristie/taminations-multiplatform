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

import android.content.Context
import com.bradchristie.taminations.Taminations

//  In Taminations, Canvas is a view to draw on
//  Android Canvas is not the same, rather is a DrawingContext
actual open class Canvas : View() {

  override val div = CustomDrawView(Taminations.context).apply {
    addListeners()
    myDraw = { ctx -> onDraw(DrawingContext(ctx)) }
  }

  class CustomDrawView(ctx:Context) : android.view.View(ctx) {
    var myDraw:(android.graphics.Canvas)->Unit = { }
    override fun onDraw(canvas: android.graphics.Canvas) {
      myDraw(canvas)
    }
  }

  protected actual open fun onDraw(ctx:DrawingContext) { }

  actual fun invalidate() {
    div.invalidate()
  }

}