package com.bradchristie.taminations.common.shapes
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

class PauseShape : Shape() {

  override fun draw(ctx: DrawingContext) {
    val h = height/2.0
    val w = h/3.0f
    val myp = DrawingStyle()
    ctx.translate(width / 2.0, height / 2.0)
    ctx.fillRect(Rect(-w*1.5,-h/2.0, w, h),myp)
    ctx.fillRect(Rect(w*0.5,-h/2.0, w, h),myp)
  }

}