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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject

class AnythingChainThru(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject("c1")
  override val requires = listOf("b2/trade","ms/cast_off_three_quarters",
                                 "plus/diamond_circulate","c1/triangle_formation",
                                 "c1/interlocked_diamond_circulate")

  override fun performCall(ctx: CallContext, i: Int) {
    val firstCall = norm.replace("chainthru","")
        .replace("triangle","trianglecirculate")
        .replace("diamond","diamondcirculate")
    ctx.applyCalls(firstCall,"very centers trade","centers cast off 34")
  }

}