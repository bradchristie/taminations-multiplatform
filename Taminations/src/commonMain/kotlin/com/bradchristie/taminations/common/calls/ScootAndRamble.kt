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

class ScootAndRamble : Action("Scoot and Ramble") {

  override val level = LevelObject("c1")
  override val requires = listOf("ms/scoot_back","a2/single_wheel","ms/slide_thru","b1/separate")


  override fun performCall(ctx: CallContext, i: Int) {
    ctx.applyCalls("Scoot Back","Ramble")
  }

}