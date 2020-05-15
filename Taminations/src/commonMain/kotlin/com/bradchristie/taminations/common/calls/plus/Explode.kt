package com.bradchristie.taminations.common.calls.plus
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
import com.bradchristie.taminations.common.calls.Action

//  This is for Explode applied to a mini-wave,
//  e.g. to take a thar to a 1/4 tag
class Explode : Action("Explode") {

  override val level = LevelObject("plus")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    d.data.partner?.let { d2 ->
      val dist = d.distanceTo(d2)
      if (d2.location.length.isLessThan(d.location.length)) {
        if (d.data.beau)
          return getMove("Lead Right").scale(1.0,dist/2.0)
        else if (d.data.belle)
          return getMove("Lead Left").scale(1.0,dist/2.0)
      } else if (d2.location.length.isGreaterThan(d.location.length)) {
        if (d.data.beau)
          return getMove("Quarter Left").skew(1.0,-dist/2.0)
        else if (d.data.belle)
          return getMove("Quarter Right").skew(1.0,dist/2.0)
      }
    }

    throw CallError("Unable to Explode from this formation.")
  }

}