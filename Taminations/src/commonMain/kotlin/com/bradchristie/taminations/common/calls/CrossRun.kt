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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.TamUtils

class CrossRun : Action("Cross Run") {

  override val level = LevelObject("b2")

  override fun perform(ctx: CallContext, i:Int) {
    //  Centers and ends cannot both cross run
    if (ctx.dancers.any {d -> d.data.active && d.data.center } &&
        ctx.dancers.any {d -> d.data.active && d.data.end } )
    throw CallError("Centers and ends cannot both Cross Run")
    //  We need to look at all the dancers, not just actives
    //  because partners of the runners need to dodge
    ctx.dancers.forEach { d ->
      if (d.data.active) {
        //  Must be in a 4-dancer wave or line
        if (!d.data.center && !d.data.end)
          throw CallError("General line required for Cross Run")
        //  Partner must be inactive
        val d2 = d.data.partner ?: throw CallError("Nobody to Cross Run around")
        if (d2.data.active)
          throw CallError("Dancer and partner cannot both Cross Run")
        //  Center beaus and end belles run left
        val isright = d.data.beau xor d.data.center
        //  TODO check for runners crossing paths
        //    dancers would need to pass right shoulders
        val m = if (isright) "Run Right" else "Run Left"
        val d3 = if (isright)
          ctx.dancersToRight(d).elementAtOrNull(1)
        else
          ctx.dancersToLeft(d).elementAtOrNull(1)
        when {
          d3 == null -> throw CallError("Unable to calcluate Cross Run")
          d3.data.active -> throw CallError("Dancers cannot Cross Run each other")
          else ->
            d.path.add(TamUtils.getMove(m).scale(1.0, d.distanceTo(d3)/2.0))
        }
      } else {
        //  Not an active dancer
        //  If partner is active then this dancer needs to dodge
        val d2 = d.data.partner
        if (d2 != null && d2.data.active)
          d.path.add(TamUtils.getMove(if (d.data.beau) "Dodge Right" else "Dodge Left")).scale(1.0,d.distanceTo(d2)/2.0)
      }
    }
  }
}