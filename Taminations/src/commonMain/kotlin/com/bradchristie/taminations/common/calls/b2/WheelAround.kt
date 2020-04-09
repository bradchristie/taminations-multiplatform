package com.bradchristie.taminations.common.calls.b2
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
import com.bradchristie.taminations.common.calls.ActivesOnlyAction

class WheelAround(norm:String,name:String) : ActivesOnlyAction(norm,name) {

  override val level = LevelObject("b2")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val d2 = d.data.partner
      ?: throw CallError("Dancer $d must Wheel Around with partner")
    val dist = d.distanceTo(d2)
    val move =
        if (norm.startsWith("reverse")) {
          if (d2 isRightOf d)
            "Beau Reverse Wheel"
          else
            "Belle Reverse Wheel"
        } else {
          if (d2 isRightOf d)
            "Beau Wheel"
          else
            "Belle Wheel"
        }
    return TamUtils.getMove(move).scale(dist/2.0,dist/2.0)
  }

}