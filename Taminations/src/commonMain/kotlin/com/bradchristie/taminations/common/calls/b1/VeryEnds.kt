package com.bradchristie.taminations.common.calls.b1
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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.calls.FilterActives
import kotlin.math.max
import kotlin.math.min

class VeryEnds : FilterActives("Very Ends") {

  override fun isActive(d: Dancer, ctx:CallContext): Boolean {
    val leftCount = ctx.dancersToLeft(d).count()
    val rightCount = ctx.dancersToRight(d).count()
    return min(leftCount,rightCount) == 0 &&
           max(leftCount,rightCount) >= 5
  }

}