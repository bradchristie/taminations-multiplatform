package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations
  Copyright (C) 2018 Brad Christie

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
import com.bradchristie.taminations.common.r
import com.bradchristie.taminations.platform.System

class While(norm:String,name:String) : Action(norm,name)  {

  override fun performCall(ctx: CallContext, i: Int) {

    //  First strip off extra beats added to the inactive dancers
    ctx.contractPaths()
    ctx.dancers.forEach { d ->
      d.data.active = true
      System.log("Dancer $d has ${d.data.actionBeats} action beats.")
    }

    //  Use another context to do the rest of the call
    val ctx2 = CallContext(ctx)
    ctx2.dancers.forEach { d -> d.data.active = true }
    val whilecall = name.toLowerCase().replace("while(\\s+the)?\\s+".r,"")
    ctx2.applyCalls(whilecall)
    ctx2.contractPaths()
    ctx2.appendToSource()
  }

}