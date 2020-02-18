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

class BendTheLine : Action("Bend the Line") {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    if (!ctx.isInCouple(d))
      throw CallError("Only couples can Bend the Line")
    if (d.data.beau) {
      if (d.isCenterRight)
        return TamUtils.getMove("Hinge Right")
      else if (d.isCenterLeft)
        return TamUtils.getMove("BackHinge Right")
    } else if (d.data.belle) {
      if (d.isCenterRight)
        return TamUtils.getMove("BackHinge Left")
      else if (d.isCenterLeft)
        return TamUtils.getMove("Hinge Left")
    }
    throw CallError("Cannot figure out how to Bend the Line")
  }

}