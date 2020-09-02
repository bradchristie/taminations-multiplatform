package com.bradchristie.taminations.common.calls.b1
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

class Dosado(norm: String, name: String) : Action(norm, name) {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = ctx.dancerFacing(d) ?:
      throw CallError("Dancer $d has no one to Dosado with.")
    val dist = d.distanceTo(d2)
    val (dir1,dir2) = if (norm.startsWith("left"))
      listOf("Right","Left") else listOf("Left","Right")
    return TamUtils.getMove("Extend $dir1").scale(dist/2.0,0.5).changebeats(dist/2.0) +
        TamUtils.getMove("Extend $dir2").scale(1.0,0.5) +
        TamUtils.getMove("Retreat $dir2").scale(1.0,0.5) +
        TamUtils.getMove("Retreat $dir1").scale(1.0,0.5)
  }

}