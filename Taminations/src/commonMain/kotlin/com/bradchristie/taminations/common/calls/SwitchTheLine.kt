package com.bradchristie.taminations.common.calls

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.TamUtils

/*

  Taminations Square Dance Animations
  Copyright (C) 2019 Brad Christie

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

class SwitchTheLine : Action("Switch the Line") {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {
    //  Start with Ends Cross Run
    ctx.applyCalls("Ends Cross Run")
    //  And now make tne centers Run instead of Dodge
    ctx.dancers.forEach { it.animate(0.0) }
    ctx.dancers.filter { it.data.center }.forEach { d ->
      d.path.clear()
      val d2 = d.data.partner
      if (d2 != null)  // better not be
        d.path.add(TamUtils.getMove(if (d.data.beau) "Flip Right" else "Flip Left")
            .scale(1.0,d.distanceTo(d2)/2.0))
    }
  }

}