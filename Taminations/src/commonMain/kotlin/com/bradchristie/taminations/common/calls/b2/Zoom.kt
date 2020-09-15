package com.bradchristie.taminations.common.calls.b2
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
import com.bradchristie.taminations.common.TamUtils.getMove
import com.bradchristie.taminations.common.calls.Action

//  This class implements both Zoom and Zing
class Zoom(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("b2")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val a = d.angleToOrigin
    val centerLeft = ctx.dancersToRight(d).count()==2 &&
                     ctx.dancersToLeft(d).count()==1
    val centerRight = ctx.dancersToRight(d).count()==1 &&
                      ctx.dancersToLeft(d).count()==2
    val (c,c2,c3) = when {
      centerLeft -> listOf("Run Right","Lead Right","Quarter Left")
      centerRight -> listOf("Run Left","Lead Left","Quarter Right")
      a < 0 -> listOf("Run Left","Lead Left","Quarter Right")
      else -> listOf("Run Right","Lead Right","Quarter Left")
    }
    val s = if (centerLeft || centerRight) 0.25 else 1.0
    when {
      d.data.leader -> {
        val d2 = ctx.dancerInBack(d) ?: throw CallError("Dancer $d cannot $name")
        if (!d2.data.active)
          throw CallError("Trailer of dancer $d is not active")
        val dist = d.distanceTo(d2)
        return getMove(c).changebeats(2.0).skew(-dist/2,0.0).scale(1.0,s) +
            if (norm == "zoom")
               getMove(c).changebeats(2.0).skew(dist/2.0,0.0).scale(1.0,s)
            else
              getMove(c2).changebeats(2.0).scale(dist/2.0,2.0*s)
      }
      d.data.trailer -> {
        val d2 = ctx.dancerInFront(d) ?: throw CallError("Dancer $d cannot $name")
        if (!d2.data.active)
          throw CallError("Leader of dancer $d is not active")
        val dist = d.distanceTo(d2)
        return if (norm == "zoom")
          getMove("Forward").changebeats(4.0).scale(dist,1.0)
        else
          getMove("Forward").changebeats(2.0).scale(dist-1,1.0) +
          getMove(c3).changebeats(2.0).skew(1.0,0.0)
      }
      else -> throw CallError("Dancer $d cannot $name")
    }
  }

}