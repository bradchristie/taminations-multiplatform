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

class Slip : Action("Slip") {

  override val level = LevelObject("a2")

  override fun perform(ctx: CallContext, i:Int) {
    //  If single wave in center, then very centers trade
    val ctx4 = CallContext(ctx,ctx.center(4))
    ctx4.analyze()
    val isInWave = ctx4.dancers.all { ctx4.isInWave(it)}
    if (ctx4.dancers.all { ctx4.isInWave(it)} && !ctx.isTidal())
      ctx.applyCalls("Very Centers Trade")

    else {
      //  Otherwise, all centers trade
      //  Check that it's not a partner trade
      val ctxc = CallContext(ctx,ctx.dancers.filter { it.data.center })
      ctxc.analyze()
      if (!ctxc.dancers.all { ctxc.isInWave(it) })
        throw CallError("Centers must be in a mini-wave.")
      ctx.applyCalls("Centers Trade")
    }
  }

}