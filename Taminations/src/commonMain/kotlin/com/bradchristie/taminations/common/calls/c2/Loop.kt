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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action

class Loop(norm:String,name:String) : Action(norm,name)  {

  override val level = LevelObject("c1")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val dir = when {
      norm.startsWith("left") -> "Left"
      norm.startsWith("right") -> "Right"
      norm.startsWith("in") -> if (d.isCenterLeft) "Left" else "Right"
      norm.startsWith("out") -> if (d.isCenterLeft) "Right" else "Left"
      else -> throw CallError("Invalid Loop direction")
    }
    val amount = norm.takeLast(1).toInt().toDouble()
    return TamUtils.getMove("Run $dir").scale(1.0,amount)
  }

}