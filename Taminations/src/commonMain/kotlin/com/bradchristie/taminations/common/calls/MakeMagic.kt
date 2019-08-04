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

class MakeMagic : Action("Make Magic") {

  override val level = LevelObject("c1")
  override val requires = listOf("a1/cross_trail_thru")

  override fun performCall(ctx: CallContext, i: Int) {
    //  If center 4 dancers are facing each other, they do a Cross Trail Thru
    if (ctx.center(4).all { d -> d.isFacingIn }) {
      ctx.applyCalls("Center 4 Cross Trail Thru")
    } else {
      //  Otherwise, process each dancer
      super.performCall(ctx, i)
      if (ctx.dancers.all { d -> d.path.movelist.isEmpty()})
        throw CallError("Make Magic does nothing")
    }
  }


  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Center and outside dancers facing each other pass thru
    ctx.dancerFacing(d)?.let { d2 ->
      if (ctx.center(4).contains(d) xor ctx.center(4).contains(d2)) {
        val dist = d.distanceTo(d2)
        return getMove("Extend Left").scale(dist/2,0.5) +
               getMove("Extend Right").scale(dist/2,0.5)
      }
    }
    //  Center dancers facing in cross
    if (ctx.center(4).contains(d) && d.isFacingIn) {
      //  Compute the X and Y values to travel
      //  The standard has x distance = 2 and y distance = 2
      val a = d.angleToOrigin
      val dx = d.location.length * a.cos
      val dy = d.location.length * a.abs.sin
      return TamUtils.getMove(if (a > 0) "Cross Left" else "Cross Right").scale(dx,dy)
    }
    return Path()
  }

}