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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action

class Slither : Action("Slither") {

  override val level = LevelObject("a2")

  override fun perform(ctx: CallContext, i:Int) {
    //  If single wave in center, then very centers trade
    val ctx4 = CallContext(ctx,ctx.center(4))
    if (ctx4.isLines() && !ctx.isTidal())
      ctx.dancers.filter { !it.data.verycenter }.forEach { it.data.active = false }

    else {
      //  Otherwise, all centers trade
      //  Check that it's not a partner trade
      val ctxc = CallContext(ctx,ctx.dancers.filter { it.data.center })
      if (!ctxc.isWaves())
        throw CallError("Centers must be in a mini-wave.")
      ctx.dancers.filter { !it.data.verycenter }.forEach { it.data.active = false }
    }
    super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    if (ctx.dancerToRight(d)?.data?.active == true)
      return TamUtils.getMove("BackSashay Right").scale(2.0,1.0)
    else if (ctx.dancerToLeft(d)?.data?.active == true)
      return TamUtils.getMove("BackSashay Left").scale(2.0,1.0)
    else
      throw CallError("Unable to calculate Sither.")
  }

}