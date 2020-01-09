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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject

class QuarterTag(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("ms")
  override val requires = listOf("ms/hinge","b1/face")

  private fun centersHoldLeftHands(ctx:CallContext): Boolean =
      ctx.actives.filter { d -> d.data.center } .all { d ->
        ctx.dancerToLeft(d)?.data?.center ?: false
      }

  private fun centersHoldRightHands(ctx:CallContext): Boolean =
      ctx.actives.filter { d -> d.data.center } .all { d ->
        ctx.dancerToRight(d)?.data?.center ?: false
      }

  override fun performCall(ctx: CallContext, i: Int) {
    val dir = if (norm.startsWith("left")) "Left" else ""
    if (ctx.isTidal()) {
      ctx.applyCalls("Center 4 Face Out While Outer 4 Face In","Facing Dancers $dir Touch")
    } else {
      if (centersHoldLeftHands(ctx) && dir == "" ||
          centersHoldRightHands(ctx) && dir == "Left")
        ctx.applyCalls("Center 4 Hinge and Spread While Ends Face In")
      else
        ctx.applyCalls("Centers $dir Hinge While Ends Face In")
    }
  }

}