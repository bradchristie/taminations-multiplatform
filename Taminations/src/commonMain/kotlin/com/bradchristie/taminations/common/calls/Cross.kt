package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class Cross : Action("Cross") {

  override fun perform(ctx: CallContext, i: Int) {
    //  If dancers are not specified, then the trailers cross
    if (ctx.actives.count() == ctx.dancers.count())
      ctx.dancers.forEach { d ->
        d.data.active = d.data.trailer
      }
    if (ctx.actives.count() == ctx.dancers.count() ||
        ctx.actives.count() == 0)
      throw CallError("You must specify which dancers Cross.")
    super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Find the other dancer to cross with
    var d2:Dancer? = null
    ctx.actives.forEach {
      if (d.tx.angle.angleDiff(it.tx.angle).abs.isApprox(PI)) {
        when {
          d2 == null -> d2 = it
          d.distanceTo(d2!!).isApprox(d.distanceTo(it)) ->
            if (it.location.length > d2!!.location.length)
              d2 = it
          d.distanceTo(it) < d.distanceTo(d2!!) -> d2 = it
        }
      }
    }
    if (d2 == null)
      throw CallError("Cannot find dancer to Cross with $d")
    //  Now compute the X and Y values to travel
    //  The standard has x distance = 2 and y distance = 2
    val a = d.angleToDancer(d2!!)
    val dist = d.distanceTo(d2!!)
    val x = dist * cos(a)
    val y = dist * sin(a.abs)
    return TamUtils.getMove(if (a > 0) "Cross Left" else "Cross Right").scale(x/2.0,y/2.0)
  }

}