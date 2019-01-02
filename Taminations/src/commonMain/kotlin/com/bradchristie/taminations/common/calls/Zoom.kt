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
import com.bradchristie.taminations.common.TamUtils.getMove

class Zoom : Action("Zoom") {

  override val level = LevelObject("b2")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    when {
      d.data.leader -> {
        val d2 = ctx.dancerInBack(d) ?: throw CallError("Dancer $d cannot Zoom")
        val a = ctx.angle(d)
        val c = if (a < 0) "Run Left" else "Run Right"
        if (!d2.data.active)
          throw CallError("Trailer of dancer $d is not active")
        val dist = d.distanceTo(d2)
        return getMove(c).changebeats(2.0).skew(-dist/2,0.0) +
            getMove(c).changebeats(2.0).skew(dist/2.0,0.0)
      }
      d.data.trailer -> {
        val d2 = ctx.dancerInFront(d) ?: throw CallError("Dancer $d cannot Zoom")
        if (!d2.data.active)
          throw CallError("Leader of dancer $d is not active")
        val dist = d.distanceTo(d2)
        return getMove("Forward").changebeats(4.0).scale(dist,1.0)
      }
      else -> throw CallError("Dancer $d cannot Zoom")
    }
  }

}