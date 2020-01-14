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
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.Path
import com.bradchristie.taminations.common.TamUtils.getMove

class SquareThru(norm: String, name: String) : Action(norm, name) {

  override val requires = listOf("b2/ocean_wave","plus/explode_the_wave","b1/step_thru")

  override fun perform(ctx: CallContext, i: Int) {
    //  Set up alternating hands
    val (left,right) = if (norm.startsWith("left"))
      Pair("","Left-Hand")
    else
      Pair("Left-Hand","")
    //  Find out how many hands
    val count = norm.replace("toawave","").takeLast(1).toIntOrNull() ?: 4
    //  First hand is step to a wave if not already there
    if (ctx.actives.any { d -> ctx.isInCouple(d) }) {
      ctx.applyCalls("Facing Dancers Step to a Compact $right Wave")
      ctx.analyze()
    }
    //  Check that wave is the correct hand
    if (ctx.actives.any { d ->
              (!d.data.belle && left == "") ||
              (!d.data.beau && right == "") ||
              !ctx.isInWave(d)  })
      throw CallError("Cannot $name from this formation")
    //  Square thru remaining hands
    (1 until count).forEach { c ->
      val hand = if (c % 2 == 0) right else left
      ctx.applyCalls("Explode and Step to a Compact $hand Wave")
    }
    if (!norm.endsWith("toawave"))
      ctx.applyCalls("Step Thru")
  }

}

class StepToACompactWave(norm: String, name: String) : Action(norm, name) {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = ctx.dancerFacing(d) ?:
        throw CallError("Cannot find dancer facing $d")
    val dist = d.distanceTo(d2)
    val dir = if (norm.contains("left")) "Right" else "Left"
    return getMove("Extend $dir").scale(dist/2,0.5)
  }

  override fun postProcess(ctx: CallContext, i: Int) {
    //  Do not snap to formation, which parent does
  }

}