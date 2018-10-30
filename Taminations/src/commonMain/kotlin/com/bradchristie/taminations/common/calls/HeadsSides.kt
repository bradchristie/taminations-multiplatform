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
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.abs
import com.bradchristie.taminations.common.isApprox

class HeadsSides(name:String) : FilterActives(name) {

  private var square = false
  override fun performCall(ctx: CallContext, i: Int) {
    square = ctx.isSquare()
    super.performCall(ctx, i)
  }

  override fun isActive(d: Dancer) =
    when {
      square && name == "Heads" -> d.location.x.abs.isApprox(3.0)
      square && name == "Sides" -> d.location.y.abs.isApprox(3.0)
      name == "Heads" -> d.number_couple == "1" || d.number_couple == "3"
      name == "Sides" -> d.number_couple == "2" || d.number_couple == "4"
      else -> false
    }

}