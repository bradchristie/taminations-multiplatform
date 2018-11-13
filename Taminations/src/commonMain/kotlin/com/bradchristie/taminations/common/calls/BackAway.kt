package com.bradchristie.taminations.common.calls

import com.bradchristie.taminations.common.*

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

class BackAway : Action("Back Away") {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    if (d.isFacingIn && ctx.dancersInBack(d).count()==0)
      //  TODO hold hands with partner?
      return TamUtils.getMove("Back 2")
    else
      throw CallError("Dancer $d cannot Back Away")
  }

}