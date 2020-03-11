package com.bradchristie.taminations.common.calls.c1
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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.common.calls.Action

class SqueezeTheHourglass : Action("Squeeze the Hourglass") {

  override val level = LevelObject("c1")
  override fun perform(ctx: CallContext, i: Int) {
    //  Match to any hourglass
    val hourglass = CallContext(TamUtils.getFormation("Hourglass RH BP"))
    val mm = hourglass.matchFormations(ctx,rotate = true) ?:
      throw CallError("Not an Hourglass formation")
    //  All but two of the dancers squeeze
    ctx.dancers[mm[2]].data.active = false
    ctx.dancers[mm[3]].data.active = false
    ctx.applyCalls("Squeeze")
  }

}