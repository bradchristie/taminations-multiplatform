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

import com.bradchristie.taminations.common.*

class HalfSashay : Action("Half Sashay") {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Figure out who we sashay with
    val d2 = when {
      d.data.partner in ctx.actives && d.data.beau -> d.data.partner!!
      d.data.partner in ctx.actives && d.data.belle -> d.data.partner!!
      ctx.dancerToRight(d) in ctx.actives -> ctx.dancerToRight(d)!!
      ctx.dancerToLeft(d) in ctx.actives -> ctx.dancerToLeft(d)!!
      else -> throw CallError("Dancer $d has nobody to Sashay with")
    }
    val move = if (d2.isLeftOf(d)) "Sashay Left" else "BackSashay Right"
    val dist = d.distanceTo(d2)

    return TamUtils.getMove(move).scale(1.0,dist/2.0)
  }

}