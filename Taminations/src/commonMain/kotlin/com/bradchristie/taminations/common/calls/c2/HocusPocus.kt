package com.bradchristie.taminations.common.calls.c2
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
import com.bradchristie.taminations.common.inOrder

class HocusPocus : Action("Hocus Pocus") {

  override val level = LevelObject("c2")

  override fun perform(ctx: CallContext, i: Int) {
    val outer4 = CallContext(ctx,ctx.outer(4).inOrder())
    val outerO = outer4.fillFormation("O RH")
      ?: outer4.fillFormation("O LH")
      ?: throw CallError("Cannot determine how outer dancers can circulate.")
    ctx.subContext(outerO.dancers) {
      applyCalls("O Circulate","O Circulate")
    }
    ctx.subContext(ctx.center(4)) {
      applyCalls("Trade")
    }
  }

}