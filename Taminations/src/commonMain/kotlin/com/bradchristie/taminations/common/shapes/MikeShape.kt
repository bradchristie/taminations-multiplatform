package com.bradchristie.taminations.common.shapes
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
import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.platform.*
import kotlin.math.PI

class MikeShape : Shape() {

  //  Use a changeable color to show when recording is active
  var color:Color = Color.BLACK

  override fun draw(ctx: DrawingContext) {

    val s = height / 250.0
    val myp = DrawingStyle()
    myp.color = color
    //  Body of mike
    ctx.fillCircle(s*128,s*60,s*24,myp)
    ctx.fillCircle(s*128,s*120,s*24,myp)
    ctx.fillRect(Rect(104*s,60*s,48*s,60*s),myp)

    //  Mike support
    myp.lineWidth = 12*s
    val path = DrawingPath()
    path.arc(128*s,120*s,48*s, PI,0.0)
    ctx.drawPath(path,myp)
    ctx.fillRect(Rect(120*s,170*s,16*s,30*s),myp)

  }

}