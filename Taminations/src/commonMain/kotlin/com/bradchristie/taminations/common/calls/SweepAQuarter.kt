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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject

class SweepAQuarter : Action("and Sweep a Quarter") {

  override val level = LevelObject("b2")
  override val requires = listOf("b2/sweep_a_quarter")

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.any {
          d -> !ctx.isInCouple(d)
        })
      throw CallError("Only couples can Sweep a Quarter")
    var isLeft = true
    var isRight = true
    ctx.actives.forEach { d ->
      val roll = ctx.roll(d)
      if (!roll.isLeft)
        isLeft = false
      if (!roll.isRight)
        isRight = false
    }
    //  Sweeping direction is opposite rolling direction
    if (isRight)
      ctx.applyCalls("Sweep a Quarter Left")
    else if (isLeft)
      ctx.applyCalls("Sweep a Quarter Right")
    else
      throw CallError("All dancers must be moving the same direction to Sweep a Quarter")
  }

}