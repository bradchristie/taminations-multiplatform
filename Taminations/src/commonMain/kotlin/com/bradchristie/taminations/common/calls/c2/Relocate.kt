package com.bradchristie.taminations.common.calls.c2
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
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.calls.ms.CastOffThreeQuarters

class Relocate(norm:String,name:String) : Action(norm,name)  {

  override val level = LevelObject("c2")
  override val requires = listOf("ms/cast_off_three_quarters",
      "c1/counter_rotate") +
      CastOffThreeQuarters().requires

  override fun perform(ctx: CallContext, i: Int) {
    ctx.applyCalls("Outer 6 Counter Rotate While Very Centers Cast Off 3/4")
  }

}