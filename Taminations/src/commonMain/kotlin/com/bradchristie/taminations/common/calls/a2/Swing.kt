package com.bradchristie.taminations.common.calls.a2
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

class Swing : Action("Swing") {

  override val level = LevelObject("a2")
  override val requires = listOf("b2/trade")

  override fun perform(ctx: CallContext, i: Int) {
    //  If single wave in center, just those 4 Swing
    val ctx4 = CallContext(ctx,ctx.center(4))
    if (ctx4.isLines() && ctx4.isWaves() && !ctx.isTidal())
      ctx4.applyCalls("Trade").appendToSource()
    else if (ctx.isWaves())
      ctx.applyCalls("Trade")
    else
      throw CallError("Dancers must be in mini-waves to Swing")
  }

}