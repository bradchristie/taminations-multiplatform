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

class PromenadeHome : Action("Promenade Home") {

  var startPoints = listOf<Vector>()

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.dancers.count() != 8)
      throw CallError("Only for 4 couples at this point.")
    //   Compute the center point of each couple
    startPoints = (1 .. 4).map { coupleNumber ->
      val couple = ctx.dancers.filter { it.number_couple.i == coupleNumber }
      val boy = couple[0]
      val girl = couple[1]
      val center = (boy.location + girl.location) / 2.0
      //  Snap to the nearest axis in the promenade direction
      when {
        //  In 1st quadrant, off X-axis -> snap to Y axis
        !center.x.isLessThan(0.0) && center.y.isGreaterThan(0.0) -> Vector(0.0,2.0)
        //  2nd quadrant, off Y-axis -> snap to -X axis
        center.x.isLessThan(0.0) && !center.y.isLessThan(0.0) -> Vector(-2.0,0.0)
        //  3rd quadrant, off X-axis -> snap to -Y axis
        !center.x.isGreaterThan(0.0) && center.y.isLessThan(0.0) -> Vector(0.0,-2.0)
        else -> Vector(2.0,0.0)
      }
    }
    //  Should be one couple at each axis point
    if (startPoints.fold(Vector()) { a,b -> a + b } != Vector()) {
      throw CallError("Dancers not positioned properly for Promenade.")
    }
    //  Check that dancers are in sequence
    startPoints.forEachIndexed { i2,v ->
      val a1 = v.angle
      val a2 = startPoints[(i2 + 1).rem(4)].angle
      val adiff = a2.angleDiff(a1)
      if (!adiff.angleEquals(PI/2.0))
        throw CallError("Dancers are not resolved, cannot promenade home.")
    }
    //  Now get each dancer to move to the calculated promenade position
    super.perform(ctx, i)
    //  Promenade to home
    do {
      ctx.applyCalls("Counter Rotate")
    } while (ctx.dancers[0].path.movelist.count() < 5 && !ctx.dancers[0].anglePosition.angleEquals(PI))
    //  Adjust to squared set
    ctx.applyCalls("Half Wheel Around")
    ctx.level = LevelObject("b1")  // otherwise Counter Rotate would set level to C-1
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val startCouple = startPoints[d.number_couple.i - 1]
    val startLocation = startCouple * (if (d.gender == Gender.BOY) 1.0 else 1.5)
    val startAngle = startCouple.angle + PI/2.0
    return ctx.moveToPosition(d,startLocation,startAngle)
  }

}