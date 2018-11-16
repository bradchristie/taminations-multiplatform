package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.TamUtils

class CrossFold : Action("Cross Fold") {

  override val level = LevelObject("ms")

  override fun perform(ctx: CallContext, i:Int) {
    //  Centers and ends cannot both cross fold
    if (ctx.dancers.any {d -> d.data.active && d.data.center } &&
        ctx.dancers.any {d -> d.data.active && d.data.end } )
      throw CallError("Centers and ends cannot both Cross Fold")
    ctx.actives.forEach { d ->
      //  Must be in a 4-dancer wave or line
      if (!d.data.center && !d.data.end)
        throw CallError("General line required for Cross Fold")
      //  Center beaus and end belles fold left
      val isright = d.data.beau xor d.data.center
      val m = if (isright) "Fold Right" else "Fold Left"
      val d2 = d.data.partner!!
      val dist = d.distanceTo(d2)
      val dxscale = 0.75

      //  The y-distance of Fold is 2.0, here we adjust that value
      //  for various formations.  The dyoffset value computed is
      //  subtracted from the default 2.0 to get the final y offset.
      val dyoffset = when {
        ctx.isTidal() && d.data.end -> -0.5
        ctx.isTidal() && d.data.center -> 0.5
        d.data.end -> 2.0 - dist*2  // which wll generally be -2.0
        d.data.center -> 0.0
        else -> 0.0
      } * if (isright) 1 else -1

      d.path += TamUtils.getMove(m).scale(dxscale, 1.0).skew(0.0, dyoffset)

      //  Also set path for partner
      //  This is an adjustment to shift the dancers into a standard formation
      val m2 = when {
        d isRightOf d2 -> "Dodge Right"
        d isLeftOf d2 -> "Dodge Left"
        else -> "Stand"  // should never happen
      }
      val myscale = when {
        ctx.isTidal() -> 0.25
        d2.data.end -> 0.5
        d2.data.center -> 0.0
        else -> 0.25
      }
      d2.path += TamUtils.getMove(m2).scale(1.0, dist * myscale)

    }
  }

}