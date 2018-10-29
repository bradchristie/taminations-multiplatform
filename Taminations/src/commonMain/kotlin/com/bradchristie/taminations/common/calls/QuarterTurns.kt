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
import com.bradchristie.taminations.common.CallContext.Companion.distance
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.Path
import com.bradchristie.taminations.common.TamUtils

abstract class QuarterTurns(name:String) : Action(name) {

  abstract fun select(ctx: CallContext, d: Dancer):String

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    var offsetX = 0.0
    val move = select(ctx,d)
    //  If leader or trailer, make sure to adjust quarter turn
    //  so handhold is possible
    /*
    if (move != "Stand") {
      if (d.data.leader) {
        val d2 = ctx.dancerInBack(d)!!
        val dist = distance(d,d2)
        if (dist > 2 && dist < 5)
          offsetX = -(dist-2)/2
      }
      if (d.data.trailer) {
        val d2 = ctx.dancerInFront(d)!!
        val dist = distance(d,d2)
        if (dist > 2 && dist < 5)
          offsetX = (dist-2)/2
      }
    } */
    return TamUtils.getMove(move).skew(offsetX,0.0)
  }

}