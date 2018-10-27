package com.bradchristie.taminations.common.shapes
/*

  Taminations Square Dance Animations
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

import com.bradchristie.taminations.platform.*
import kotlin.math.PI

class SpeakerShape : Shape() {

  override fun draw(ctx: DrawingContext) {
    val h = height/2.0
    val w = h/3.0f
    val myp = DrawingStyle()
    ctx.translate(width*0.4,height*0.5)
    ctx.fillRect(Rect(-w*1.5,-h*0.33, w/2.0, h*0.66),myp)

    val path = DrawingPath()
    path.moveTo(-w*0.5,-h*0.33)
    path.lineTo(w*0.5,-h*0.7)
    path.lineTo(w*0.5,h*0.7)
    path.lineTo(-w*0.5,h*0.33)
    path.close()
    ctx.fillPath(path)

    val path2 = DrawingPath()
    path2.arc(w*0.5,0.0,0.5*w,PI*7/4,PI*9/4)
    ctx.drawPath(path2)
    val path3 = DrawingPath()
    path3.arc(w*0.5,0.0,1.25*w,PI*7/4,PI*9/4)
    ctx.drawPath(path3)
    val path4 = DrawingPath()
    path4.arc(w*0.5,0.0,2.0*w,PI*7/4,PI*9/4)
    ctx.drawPath(path4)
  }
}