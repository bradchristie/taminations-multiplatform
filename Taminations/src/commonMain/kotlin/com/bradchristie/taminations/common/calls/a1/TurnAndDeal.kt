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

class TurnAndDeal(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("a1")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val dir = ctx.tagDirection(d)
    val amount = if (ctx.isTidal()) 1.5 else 1.0
    val dist = if (!ctx.isTidal()) 2.0 else
      if (d.data.center) 1.5 else 0.5
    val sign = if (dir=="Left") 1.0 else -1.0
    return TamUtils.getMove("U-Turn $dir")
        .skew(sign*(if (norm.startsWith("left")) amount else -amount),dist*sign)
  }

}