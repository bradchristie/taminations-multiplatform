package com.bradchristie.taminations.common.calls.c1
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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action
import kotlin.math.PI

class Jaywalk : Action("Jaywalk") {

  override val level = LevelObject("c1")

  //  Find dancer to Jaywalk with this dancer
  //  Only looks from this dancer's perspective
  //  Returns null if no dancer found or if
  //  cannot decide between two other dancers
  private fun bestJay(ctx:CallContext, d: Dancer) : Dancer? {
    var bestDancer:Dancer? = null
    var bestDistance = 10.0
    ctx.actives.filter { it != d }.forEach { d2 ->
      val a1 = d.angleToDancer(d2)
      val a2 = d2.angleToDancer(d)
      //  Dancers must approx. face each other
      if (a1.abs.isLessThan(PI/2) && a2.abs.isLessThan(PI/2)) {
        val dist = d.distanceTo(d2)
        if (dist.isApprox(bestDistance)) {
          bestDancer = null
        } else if (dist < bestDistance) {
          bestDancer = d2
          bestDistance = dist
        }
      }
    }
    return bestDancer
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Find dancer to Jaywalk with
    val d2 = bestJay(ctx,d)
      ?: ctx.actives.firstOrNull { d3 -> bestJay(ctx,d3) == d }
      ?: throw CallError("Cannot find dancer to Jaywalk with $d")
    //   Calculate Jay path
    val v = d.vectorToDancer(d2)
    val da = d.angleFacing - d2.angleFacing
    if (da isAround PI/2.0)
      return TamUtils.getMove("Lead Left Passing").scale(v.x,v.y)
    else if (da isAround PI*3.0/2.0)
      return TamUtils.getMove("Lead Right Passing").scale(v.x,-v.y)
    return if (v.y > 0) {
      //   Pass right shoulders
      TamUtils.getMove("Extend Left")
              .scale(v.x-1,v.y)
              .changebeats(v.length.ceil-1) +
      TamUtils.getMove("Forward")
    } else {
      TamUtils.getMove("Forward") +
      TamUtils.getMove("Extend Right")
              .scale(v.x-1,-v.y)
              .changebeats(v.length.ceil-1)
    }
  }

}