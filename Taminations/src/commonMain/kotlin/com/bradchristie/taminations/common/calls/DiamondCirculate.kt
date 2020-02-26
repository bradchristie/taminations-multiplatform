package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DiamondCirculate : Action("Diamond Circulate") {

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() != 4)
      throw CallError("Unable to calculate Diamond Circulate")
    super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Should be exactly 1 other active dancer
    //  in front of this dancer within 90 degrees
    val offset = 0.5
    val others = ctx.actives.filter { it != d } .filter { d2 ->
      val a = d.angleToDancer(d2).abs
      !a.isAround(PI/2) && a < PI/2
    }
    if (others.count() != 1)
      throw CallError("Cannot figure out how dancer $d can Diamond Circulate")
    val d2 = others.first()
    val a2 = d.angleToDancer(d2)
    if (a2.isAround(0.0))
      throw CallError("Doesn't look like dancer $d is in a Diamond")
    val dist = d.distanceTo(d2)
    val isLeft = a2 > 0
    val xScale = dist * cos(a2)
    val yScale = dist * sin(a2)
    val move = if (isLeft) "Lead Left" else "Lead Right"
    val simplePath = TamUtils.getMove(move).scale(xScale,yScale.abs).changebeats(2.0)
    val isFacing = d2.angleToDancer(d).abs < PI/2
    if (isFacing) {
      //  Calculate curves to pass right shoulders
      val bzrot = simplePath.movelist.first().brotate
      val p2 = Vector(xScale,yScale)
      if (isLeft) {
        val cx1 = xScale*sin(PI/6) - offset*sin(PI/6)
        val cy1 = yScale - yScale*cos(PI/6) + offset*cos(PI/6)
        val cx2 = xScale*sin(PI/3) - offset*sin(PI/3)
        val cy2 = yScale - yScale*cos(PI/3) + offset*cos(PI/3)
        val bz = Bezier.fromPoints(Vector(),Vector(cx1,cy1),Vector(cx2,cy2),p2)
        val m = Movement(2.0,Hands.NOHANDS,bz,bzrot)
        return Path(m)
      } else {
        val cx1 = xScale*sin(PI/6) + offset*sin(PI/6)
        val cy1 = yScale- yScale*cos(PI/6) + offset*cos(PI/6)
        val cx2 = xScale*sin(PI/3) + offset*sin(PI/3)
        val cy2 = yScale - yScale*cos(PI/3) + offset*cos(PI/3)
        val bz = Bezier.fromPoints(Vector(),Vector(cx1,cy1),Vector(cx2,cy2),p2)
        val m = Movement(2.0,Hands.NOHANDS,bz,bzrot)
        return Path(m)
      }
    }
    return simplePath
  }

}