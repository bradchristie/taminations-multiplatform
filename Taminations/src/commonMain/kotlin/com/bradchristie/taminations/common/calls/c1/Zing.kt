package com.bradchristie.taminations.common.calls.c1
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

class Zing : Action("Zing") {

  override val level = LevelObject("c1")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val a = d.angleToOrigin
    val c1 = if (a < 0) "Run Left" else "Run Right"
    val c2 = if (a < 0) "Lead Left" else "Lead Right"
    val c3 = if (a < 0) "Quarter Right" else "Quarter Left"
    when {
      d.data.leader -> {
        val d2 = ctx.dancerInBack(d) ?: throw CallError("Dancer $d cannot Zing")
        if (!d2.data.active)
          throw CallError("Trailer of dancer $d is not active")
        val dist = d.distanceTo(d2)
        return getMove(c1).changebeats(2.0).skew(-dist/2,0.0) +
               getMove(c2).changebeats(2.0).scale(dist/2.0,2.0)
      }
      d.data.trailer -> {
        val d2 = ctx.dancerInFront(d) ?: throw CallError("Dancer $d cannot Zoom")
        if (!d2.data.active)
          throw CallError("Leader of dancer $d is not active")
        val dist = d.distanceTo(d2)
        return getMove("Forward").changebeats(2.0).scale(dist-1,1.0) +
               getMove(c3).changebeats(2.0).skew(1.0,0.0)
      }
      else -> throw CallError("Dancer $d cannot Zing")
    }
  }


}