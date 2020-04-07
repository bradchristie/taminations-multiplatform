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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.ri

class CheckpointConcept(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c2")

  override fun perform(ctx: CallContext, i: Int) {
    //  Parse out the two calls
    val firstCall = name.replace("Checkpoint (.+) by (.+)".ri,"$1")
    val secondCall = name.replace("Checkpoint (.+) by (.+)".ri,"$2")

    //  Figure out who does the first call
    val centers = when (ctx.groupstr) {
      "2222" -> ctx.groups[1] + ctx.groups[2]
      "242" -> ctx.groups[1]
      "422" ->ctx.groups[1]
      "224" -> ctx.groups[1]
      else ->
        throw CallError("Not a valid formation for Checkpoint")
    }
    val ctx1 = CallContext(ctx,centers)
    val ctx2 = CallContext(ctx,ctx.dancers - centers)
    //  Do the first call
    ctx1.applyCalls("Concentric $firstCall").appendToSource()
    //  Slide in the outer 2 if needed
    if (ctx.groups[2].first().location.length > 4.0)
      ctx2.applyCalls("outer 2 slide in")
    //  Do the second call
    ctx2.applyCalls(secondCall).appendToSource()
  }

}