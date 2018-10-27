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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.CallError

class StarThru : Action("Star Thru") {

  override val requires = listOf("ms/slide_thru")

  override fun perform(ctx: CallContext, i:Int) {
    //  Check that facing dancers are opposite genders
    ctx.actives.forEach { d ->
      val d2 = ctx.dancerInFront(d)
      if (d2 == null || !d2.data.active || ctx.dancerInFront(d2) != d)
        throw CallError("Dancer ${d.number} has nobody to Star Thru with")
      if (d2.gender == d.gender)
        throw CallError("Dancer ${d.number} cannot Star Thru with another dancer of the same gender")
    }
    //  All ok
    ctx.applyCalls("Slide Thru")
  }

}