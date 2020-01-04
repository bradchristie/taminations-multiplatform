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

class StarThru(norm:String,name:String) : Action(norm,name) {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Must be facing dancers, opposite gender
    val d2 = ctx.dancerFacing(d) ?: throw CallError("Dancer $d has nobody to Star Thru with")
    if (d2.gender == d.gender)
      throw CallError("Cannot Star Thru with same gender.")
    val dist = d.distanceTo(d2)
    return (getMove("Extend Left").scale(dist / 2, 0.5) +
        (if (d.gender == Gender.BOY)
          getMove("Lead Right").scale(1.0, 0.5)
        else
          getMove("Quarter Left").skew(1.0, -0.5)))
            //  "left" star thru is used by double/triple star thru
            .alsoIf(norm.startsWith("left")) { reflect() }
  }

}