package com.bradchristie.taminations.common.calls.a1
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
import com.bradchristie.taminations.common.calls.Action

class SplitSquareThru(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject("a1")
  override val requires = listOf("b1/pass_thru","a1/quarter_in",
      "b1/square_thru","b1/face",
      "b2/ocean_wave","plus/explode_the_wave","b1/step_thru")

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() < 8)
      throw CallError("Use Heads Start or Sides Start Split Square Thru")
    val (left,right) = if (norm.startsWith("left")) Pair("","Left") else Pair("Left","")
    val count = norm.takeLast(1).toIntOrNull() ?: 4
    //  If the centers start, they need to face out to work with the ends
    //  Otherwise they will face in to work with the other dancers
    val face = if (ctx.actives.all {
          d -> d.data.center || (ctx.dancerFacing(d) == null)
        }) "Out" else "In"
    ctx.applyCalls("Facing Dancers $right Pass Thru and Face $face",
                   "$left Square Thru ${count-1}")
  }

}

class HeadsStart(norm: String, name: String) : Action(norm, name) {

  override fun perform(ctx: CallContext, i: Int) {
    if (norm.startsWith("head"))
      ctx.applyCalls("Heads Start")
    else
      ctx.applyCalls("Sides Start")
    ctx.applyCalls(name.replace("(head|side)(s)?\\s+start(a)?\\s+".ri,""))
  }

}