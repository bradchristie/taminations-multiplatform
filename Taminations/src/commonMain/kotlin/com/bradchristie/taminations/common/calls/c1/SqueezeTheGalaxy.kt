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

class SqueezeTheGalaxy : Action("Squeeze the Galaxy") {

  override val level = LevelObject("c1")
  override fun perform(ctx: CallContext, i: Int) {
    //  Match to any galaxy
    val galaxy = CallContext(TamUtils.getFormation("Galaxy RH GP"))
    val mm = galaxy.matchFormations(ctx,rotate = 180) ?:
      throw CallError("Not a Galaxy formation")
    //  All but two of the dancers squeeze
    ctx.dancers[mm[2]].data.active = false
    ctx.dancers[mm[3]].data.active = false
    ctx.applyCalls("Squeeze")
  }

}