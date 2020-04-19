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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.calls.Action

class AroundToALine(norm:String,name:String) : Action(norm,name) {

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() < ctx.dancers.count()) {
      ctx.matchStandardFormation()
      ctx.dancers.forEach { it.data.active = true }
      when {
        norm.contains("1andcomeintothemiddle") ->
          ctx.applyCalls("Around One and Come Into the Middle")
        norm.contains("1toaline") -> ctx.applyCalls("Around One To A Line")
        norm.contains("2toaline") -> ctx.applyCalls("Around Two To A Line")
        else -> throw CallError("Go Around What?")
      }
    } else
      throw CallError("Cannot Go Around to a Line")
  }

}
