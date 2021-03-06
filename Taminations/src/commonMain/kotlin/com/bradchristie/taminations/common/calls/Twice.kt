package com.bradchristie.taminations.common.calls
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

class Twice(norm:String,name:String) : CodedCall(norm,name) {

  override fun performCall(ctx: CallContext, i: Int) {
    if (ctx.callstack.count() < 2)
      throw CallError("Twice what?")
    //  At this point the call has already been done once
    //  Make sure everyone waits to finish the first time
    ctx.extendPaths()
    //  So just do it again
    ctx.applyCalls(ctx.callstack.dropLast(1).joinToString(" ",transform={ it.name }))
  }

}