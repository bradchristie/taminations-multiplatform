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
import com.bradchristie.taminations.common.Gender
import com.bradchristie.taminations.common.LevelObject

class BraceThru : Action("Brace Thru") {

  override val level = LevelObject("a1")
  override val requires = listOf("b1/courtesy_turn","b1/turn_back")


  override fun perform(ctx: CallContext, i: Int) {
    ctx.applyCalls("Pass Thru")
    val normal = ctx.actives.filter { it.data.beau xor (it.gender==Gender.GIRL) }
    val sashay = ctx.actives.filter { it.data.beau xor (it.gender==Gender.BOY) }
    if (normal.count() > 0)
      CallContext(ctx,normal).applyCalls("Courtesy Turn").appendToSource()
    if (sashay.count() > 0)
      CallContext(ctx,sashay).applyCalls("Turn Back").appendToSource()
  }

}