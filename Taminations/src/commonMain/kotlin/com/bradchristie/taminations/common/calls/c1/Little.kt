package com.bradchristie.taminations.common.calls.c1
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

class Little(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject("c1")
  override val requires = listOf("b1/face","c1/counter_rotate","c1/step_and_fold")

  override fun perform(ctx: CallContext, i: Int) {
    //  Figure out which way the outside dancers turn
    var turn = "Face Right"
    if (norm.startsWith("left") || norm.endsWith("left"))
      turn = "Face Left"
    else if (norm.startsWith("right") || norm.endsWith("right"))
      turn = "Face Right"
    else if (norm.startsWith("in") || norm.endsWith("in"))
      turn = "Face In"
    else if (norm.startsWith("out") || norm.endsWith("out"))
      turn = "Face Out"
    else if (norm.endsWith("forward") || norm.endsWith("asyouare"))
      turn = ""
    try {
      if (ctx.actives.count() == 8)
        ctx.applyCalls("Outer 4 $turn Counter Rotate While Center 4 Step and Fold")
      else if (ctx.actives.count()==4 && ctx.actives.containsAll(ctx.outer(4)))
        ctx.applyCalls("Outer 4 $turn Counter Rotate")
      else
        throw CallError("Don't know how to Little for these dancers.")
    } catch (_: CallError) {
      throw CallError("Unable to do Little from this formation.")
    }
  }

}