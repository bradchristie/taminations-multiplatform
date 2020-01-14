package com.bradchristie.taminations.common.calls
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
import com.bradchristie.taminations.common.TamUtils.getMove

class TurnThru(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("ms")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val (dir1, dir2) = if (norm.contains("left"))
      listOf("Right", "Left")
    else
      listOf("Left", "Right")
    //  Check for dancers in mini-wave
    if (ctx.isInWave(d)) {
      //  If in waves, Turn Thru has to be right-handed,
      //  Left Turn Thru left-handed
      val d2 = (if (norm.contains("left")) ctx.dancerToLeft(d) else ctx.dancerToRight(d))
      if (d2 != null && d2.data.active) {
        val dist = d.distanceTo(d2)
        return getMove("Swing $dir2").scale(dist / 2, 0.5) +
            getMove("Extend $dir2").scale(1.0, 0.5)
      }
    }
    //  Otherwise has to be facing dancers
    val d2 = ctx.dancerFacing(d)
    if (d2 == null || !d2.data.active || ctx.dancerInFront(d2) != d)
      return ctx.dancerCannotPerform(d,name)
    val dist = d.distanceTo(d2)
    return getMove("Extend $dir1").scale(dist / 2, 0.5) +
        getMove("Swing $dir2").scale(0.5, 0.5) +
        getMove("Extend $dir2").scale(dist / 2, 0.5)
  }

}