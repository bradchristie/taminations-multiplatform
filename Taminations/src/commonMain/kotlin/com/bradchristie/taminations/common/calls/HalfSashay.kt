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

class HalfSashay : Action("Half Sashay") {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Figure out who we sashay with
    return TamUtils.getMove( when {
      d.data.partner in ctx.actives && d.data.beau -> "BackSashay Right"
      d.data.partner in ctx.actives && d.data.belle -> "Sashay Left"
      ctx.dancerToRight(d) in ctx.actives -> "BackSashay Right"
      ctx.dancerToLeft(d) in ctx.actives -> "Sashay Left"
      else -> throw CallError("Dancer $d has nobody to Sashay with")
    })
  }

}