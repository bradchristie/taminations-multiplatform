package com.bradchristie.taminations.common.calls.c2
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

class Rotate(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c2")
  override val requires = listOf("b2/wheel_around","ms/hinge")

  override fun perform(ctx: CallContext, i: Int) {
    if (!ctx.isLines() || !ctx.dancers.all { ctx.isInCouple(it) })
      throw CallError("Unable to Rotate from this formation")
    val leaders = ctx.dancers.filter { it.data.leader }
    val trailers = ctx.dancers.filter { it.data.trailer }
    if (leaders.count() > 0)
      ctx.subContext(leaders) {
        applyCalls("Half Wheel Around")
      }
    if (trailers.count() > 0)
      ctx.subContext(trailers) {
        applyCalls("Half Reverse Wheel Around")
      }
    ctx.applyCalls("Couples Hinge")
    if (norm.endsWith("12"))
      ctx.applyCalls("Couples Hinge")
    if (norm.endsWith("34"))
      ctx.applyCalls("Couples Hinge","Couples Hinge")
  }

}