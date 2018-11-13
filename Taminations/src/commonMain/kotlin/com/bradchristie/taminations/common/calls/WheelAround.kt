package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations
  Copyright (C) 2018 Brad Christie

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

class WheelAround : Action("Wheel Around") {

  override val level = LevelObject("b2")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = listOfNotNull(d.data.partner,ctx.dancerToRight(d),ctx.dancerToLeft(d))
      .firstOrNull { ctx.isInCouple(d,it) }
      ?: throw CallError("Dancer $d is not part of a Facing Couple")
    if (!d2.data.active)
      throw CallError("Dancer $d must Wheel Around with partner")
    return TamUtils.getMove(if (d2 isRightOf d) "Beau Wheel" else "Belle Wheel")
  }

}