package com.bradchristie.taminations.common.calls.ms
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
import com.bradchristie.taminations.common.calls.Action

class CastOffThreeQuarters : Action("Cast Off Three Quarters") {

  override val level = LevelObject("ms")
  override val requires = listOf("ms/hinge","b2/wheel_around")

  override fun perform(ctx: CallContext, i: Int) {
    //  Dancers in mini-waves hinge three times
    val wavedancers = ctx.actives.filter { d -> ctx.isInWave(d) }
    if (wavedancers.isNotEmpty())
      CallContext(ctx,wavedancers).applyCalls("Hinge","Hinge","Hinge").appendToSource()
    //  Couples right of center (they look left to view center)
    //  reverse wheel around 1.5
    val couplesLeft = ctx.actives.filter {
      d -> ctx.isInCouple(d) && d.isCenterLeft &&
        d.data.partner!!.data.active && d.data.partner!!.isCenterLeft }
    if (couplesLeft.isNotEmpty())
      CallContext(ctx,couplesLeft).applyCalls("Reverse Wheel Around 1.5").appendToSource()
    //  Couples left of center wheel around 1.5
    val couplesRight = ctx.actives.filter {
      d -> ctx.isInCouple(d) && d.isCenterRight &&
        d.data.partner!!.data.active && d.data.partner!!.isCenterRight }
    if (couplesRight.isNotEmpty())
      CallContext(ctx,couplesRight).applyCalls("Wheel Around 1.5").appendToSource()

    //  If nobody fell in any of these three categories then something's wrong
    if (wavedancers.isEmpty() && couplesLeft.isEmpty() && couplesRight.isEmpty())
      throw CallError("Unable to calculate Cast Off 3/4")
  }

}