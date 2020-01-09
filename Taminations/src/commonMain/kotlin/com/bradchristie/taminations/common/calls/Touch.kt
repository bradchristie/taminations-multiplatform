package com.bradchristie.taminations.common.calls

import com.bradchristie.taminations.common.*

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

class Touch(norm:String,name:String) : Action(norm,name) {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = ctx.dancerFacing(d)
      ?: throw CallError("Dancer $d has noone to Touch")
    val dist = d.distanceTo(d2)
    val dir = if (norm.startsWith("left")) "Right" else "Left"
    return TamUtils.getMove("Extend $dir").scale(dist/2,1.0)
  }

}
