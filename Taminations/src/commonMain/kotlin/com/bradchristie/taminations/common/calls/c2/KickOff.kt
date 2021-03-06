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
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.common.calls.Action

class KickOff(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c2")
  override val requires = listOf("b2/run","plus/anything_and_roll")

  override fun perform(ctx: CallContext, i: Int) {
    //  Active dancers [Cross] Run and Roll
    val cross = if (norm.startsWith("cross")) "Cross" else ""
    ctx.applyCalls("$cross Run and Roll")
    //  Inactive dancers that moved do a Partner Tag
    ctx.dancers.filter {
      !it.data.active && it.path.movelist.count() > 0
    }.forEach { d ->
      val m = d.path.shift()!!
      val dy = m.btranslate.endPoint.y
      if (dy > 0)
        d.path = TamUtils.getMove("Quarter Left").changebeats(3.0).skew(0.0,dy)
      else if (dy < 0)
        d.path = TamUtils.getMove("Quarter Right").changebeats(3.0).skew(0.0,dy)
    }
  }

}