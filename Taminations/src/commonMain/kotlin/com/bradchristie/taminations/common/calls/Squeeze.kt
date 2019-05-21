package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.TamUtils.getMove

class Squeeze : Action("Squeeze") {

  override val level = LevelObject("c1")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = ctx.dancerToLeft(d) ?: ctx.dancerToRight(d) ?: throw CallError("No dancer to Squeeze with $d")
    val dist = d.distanceTo(d2)
    val isClose = dist < 2.0 || dist isAbout 2.0
    val sameDirection = d.angleFacing isAround d2.angleFacing
    val tradePath = when {
      d2 isLeftOf d && sameDirection -> getMove("Flip Left")
      d2 isRightOf d && sameDirection -> getMove("Run Right")
      d2 isLeftOf d -> getMove("Swing Left")
      else -> getMove("Swing Right")
    }
    val dodgePath = if (d2 isLeftOf d xor isClose)
      getMove("Dodge Left") else getMove("Dodge Right")
    return if (isClose)
      tradePath + dodgePath
    else
      dodgePath.scale(1.0, (dist - 2.0) / 4.0) + tradePath
  }

}