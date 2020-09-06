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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action

class Bounce(norm: String, name: String) : Action(norm, name) {


  override val level = LevelObject("c2")
  override val requires = listOf("b1/veer","b1/turn_back")

  override fun perform(ctx: CallContext, i: Int) {
    //  Figure out which way to veer
    val centerBelles = ctx.actives.filter {
      d -> d.data.center && d.data.belle
    }
    val centerBeaus = ctx.actives.filter {
      d -> d.data.center && d.data.beau
    }
    val veer = if (centerBeaus.count()==0 && centerBelles.count() > 0)
      "Veer Right"
    else if (centerBeaus.count() > 0 && centerBelles.count() == 0)
      "Veer Left"
    else
      throw CallError("Unable to calculate Bounce")

    //  Remember who to bounce
    val who = norm.replace("bounce(the)?".r,"")
    val whoctx = CallContext(ctx,ctx.actives)
    //  Do the veer
    ctx.applyCalls(veer)
    //  Do the bounce
    if (!who.matches("no(body|one)".r))
      whoctx.applyCalls("$who Turn Back").appendToSource()
  }

}