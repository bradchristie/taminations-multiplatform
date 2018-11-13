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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.TamUtils.getMove

class SlideThru : Action("Slide Thru") {

  override val level = LevelObject("ms")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Check if in wave, slide thru with adj dancer
    if (ctx.isInWave(d) && d.data.beau && ctx.dancerToRight(d)!!.data.active) {
      val dist = d.distanceTo(ctx.dancerToRight(d)!!)
      return if (d.gender == Gender.BOY)
            getMove("Lead Right").scale(1.0,dist/2.0)
          else
            getMove("Quarter Left").skew(1.0, -dist/2.0)
    } else {
      //  Not in wave
      //  Must be facing dancers
      val d2 = ctx.dancerFacing(d) ?: throw CallError("Dancer $d has nobody to Slide Thru with")
      val dist = d.distanceTo(d2)
      return getMove("Extend Left").scale(dist / 2, 0.5) +
          (if (d.gender == Gender.BOY)
            getMove("Lead Right").scale(1.0, 0.5)
          else
            getMove("Quarter Left").skew(1.0, -0.5))
    }

  }
}