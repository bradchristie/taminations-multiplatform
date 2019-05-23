package com.bradchristie.taminations.common.calls

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject

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

class ThreeByTwoAceyDeucey : Action("Three By Two Acey Deucey") {

  override val level = LevelObject("c1")
  override val requires = listOf("c1/triangle_formation","b2/trade")


  override fun performCall(ctx: CallContext, i: Int) {
    ctx.applyCalls("Outside Triangle Circulate While Very Centers Trade")
  }

}