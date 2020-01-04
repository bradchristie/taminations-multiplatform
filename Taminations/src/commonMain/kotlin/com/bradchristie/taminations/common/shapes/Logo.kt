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

import com.bradchristie.taminations.platform.Canvas
import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.platform.*
import kotlin.math.min

class Logo : Shape() {

  override fun draw(ctx:DrawingContext) {
    val range = min(width,height)
    ctx.scale(range/175.0,range/175.0)
    val p = DrawingStyle(lineWidth = 4.0)

    //  Handhold
    p.color = Color.ORANGE
    ctx.fillCircle(88.0,88.0,9.0,p)
    ctx.drawLine(62.0,88.0,138.0,88.0,p)

    //  Boy
    p.color = Color.BLUE.darker()
    ctx.fillCircle(37.0,60.0,15.0,p)
    p.color = Color.BLUE
    ctx.fillRect(Rect(11.0,61.0,52.0,52.0),p)
    p.color = Color.BLUE.darker()
    ctx.drawRect(Rect(11.0,61.0,52.0,52.0),p)

    //  Girl
    p.color = Color.RED.darker()
    ctx.fillCircle(138.0,114.0,15.0,p)
    p.color = Color.RED
    ctx.fillCircle(138.0,87.0,26.0,p)
    p.color = Color.RED.darker()
    ctx.drawCircle(138.0,87.0,26.0,p)
  }

}