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

//  This class is only for the variation "Cast a Shadow, Centers go 3/4"
//  All the normal Cast a Shadow formations are handled in the xml animations
class CastAShadow(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("ms")
  override val requires = listOf("a1/cast_a_shadow")

  override fun perform(ctx: CallContext, i: Int) {
    if (norm.matches("castashadowcenter.*34".ri)) {
      val incenters = ctx.dancers.filter { it.data.center && it.data.trailer }
      if (incenters.count() != 2)
        throw CallError("Need exactly 2 trailing centers to go 3/4.")
      ctx.applyCalls("Cast a Shadow")
      val castdir = if (incenters.first().isCenterLeft) "Left" else "Right"
      incenters.forEach { d ->
        d.path = TamUtils.getMove("Forward 2") +
            TamUtils.getMove("Cast $castdir") +
            TamUtils.getMove("Forward 2")
      }
    } else
      throw CallError("Improper variation for Cast a Shadow")
  }

}