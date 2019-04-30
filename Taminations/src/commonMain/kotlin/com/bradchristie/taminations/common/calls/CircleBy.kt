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
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.r

class CircleBy(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")
  override val requires = listOf("b1/circle","b2/ocean_wave","b2/trade",
                                 "ms/hinge","ms/cast_off_three_quarters")

  override fun perform(ctx: CallContext, i: Int) {
    //  Make sure we have "Circle By <fraction> and <something>"
    val a = norm.replace("circleby","").split("and".r,2)
    if (a.count() != 2)
      throw CallError("Circle By <fraction> and <fraction or call>")
    val (frac1,frac2) = a
    //  Do the first fraction
    when (frac1) {
      "nothing" -> { }
      "14", "12", "34" -> ctx.applyCalls("Circle Four Left $frac1")
      else -> throw CallError("Circle by what?")
    }
    //  Step to a Wave
    ctx.applyCalls("Step to a Wave")
    //  Do the second fraction or call
    when (frac2) {
      "nothing" -> { }
      "14" -> ctx.applyCalls("Hinge")
      "12" -> ctx.applyCalls("Trade")
      "34" -> ctx.applyCalls("Cast Off 3/4")
      else -> ctx.applyCalls(frac2)
    }
  }

}