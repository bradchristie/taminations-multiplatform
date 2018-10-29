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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.LevelObject

class Roll : QuarterTurns("Roll") {

  override val level = LevelObject("plus")

  override fun perform(ctx: CallContext, i: Int) {
    //  TODO should also check that there is a preceding action
    if (i == 0)
      throw CallError("'and Roll' must follow another call.")
    super.perform(ctx, i)
  }

  override fun select(ctx: CallContext, d: Dancer): String {
    val roll = ctx.roll(d)
    return when {
      roll.isRight -> "Quarter Right"
      roll.isLeft -> "Quarter Left"
      else -> "Stand"
    }
  }

}