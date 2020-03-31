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

class Slide : Action("Slide") {

  override val level = LevelObject("a2")

  override fun perform(ctx: CallContext, i: Int) {
    //  If single wave in center, just those 4 Slide
    val ctx4 = CallContext(ctx,ctx.center(4))
    if (ctx.dancers.count() > 4 &&
        ctx4.isLines() && ctx4.isWaves() && !ctx.isTidal()) {
      ctx4.analyze()
      ctx4.applyCalls("Slide").appendToSource()
    }
    else if (ctx.isWaves())
      super.perform(ctx, i)
    else
      throw CallError("Dancers must be in mini-waves to Swing")
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = d.data.partner
      ?: throw CallError("Unable to calculate Slide.")
    val dist = d.distanceTo(d2)
    if (d.data.beau)
      return TamUtils.getMove("BackSashay Right").scale(1.0,dist/2.0)
    else if (d.data.belle)
      return TamUtils.getMove("BackSashay Left").scale(1.0,dist/2.0)
    else
      throw CallError("Unable to calculate Slide.")
  }

}