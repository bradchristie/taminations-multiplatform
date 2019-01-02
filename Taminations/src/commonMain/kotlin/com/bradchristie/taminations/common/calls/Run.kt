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

class Run : Action("Run") {

  override val level = LevelObject("b2")

  override fun perform(ctx: CallContext, i: Int) {
    //  We need to look at all the dancers, not just actives
    //  because partners of the runners need to dodge
    ctx.dancers.forEach { d ->
      if (d.data.active) {
        //  Find dancer to run around
        //  Usually it's the partner
        var d2 = d.data.partner ?: throw CallError("Dancer ${d.number} has nobody to Run around")
        //  But special case of t-bones, could be the dancer on the other side,
        //  check if another dancer is running around this dancer's "partner"
        val d3 = d2.data.partner
        if (d != d3 && d3!=null && d3.data.active) {
          d2 = (if (d3 isRightOf d)
            ctx.dancerToRight(d) else ctx.dancerToLeft(d))
              ?: throw CallError("Dancer ${d.number} has nobody to Run around")
        }
        if (d2.data.active)
          throw CallError("Dancers cannot Run around each other.")
        val m = if (d2 isRightOf d) "Run Right" else "Run Left"
        val dist = d.distanceTo(d2)
        d.path.add(TamUtils.getMove(m).scale(1.0,dist/2))
        //  Also set path for partner
        val m2 = when {
          d isRightOf d2 -> "Dodge Right"
          d isLeftOf d2 -> "Dodge Left"
          d isInFrontOf d2 -> "Forward 2"
          d isInBackOf d2 -> "Back 2"   //  really ???
          else -> "Stand"  // should never happen
        }
        d2.path.add(TamUtils.getMove(m2).scale(1.0,dist/2))
      }
    }
  }
}