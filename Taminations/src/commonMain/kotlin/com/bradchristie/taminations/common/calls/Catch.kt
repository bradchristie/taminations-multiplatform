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

class Catch(norm:String,name:String) : Action(norm,name)  {

  override val level = LevelObject("c2")
  override val requires = listOf("b1/square_thru","b2/trade","c1/step_and_fold")


  override fun perform(ctx: CallContext, i: Int) {
    val dir = if (norm.startsWith("left")) "Left" else ""
    val count = norm.takeLast(1).toIntOrNull() ?:
      throw CallError("Catch how much?")
    ctx.applyCalls("$dir Square Thru $count to a Wave",
                   "Centers Trade","Step and Fold")
  }

}