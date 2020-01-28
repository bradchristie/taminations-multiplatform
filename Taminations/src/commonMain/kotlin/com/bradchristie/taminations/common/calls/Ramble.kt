package com.bradchristie.taminations.common.calls

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject

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

class Ramble : Action("Ramble") {

  override val level = LevelObject("c1")
  override val requires = listOf("a2/single_wheel","ms/slide_thru","b1/separate")

  override fun perform(ctx: CallContext, i: Int) {
    val ctx2 = CallContext(ctx,beat=0.0).noSnap().noExtend()
    ctx2.applyCalls("Center 4 Single Wheel and Slide Thru")
    ctx2.applyCalls("Outer 4 Separate and Slide Thru")
    ctx2.appendToSource()
  }

}