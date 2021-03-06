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
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.r
import com.bradchristie.taminations.platform.System

class While(norm:String,name:String) : Action(norm,name)  {

  override fun perform(ctx: CallContext, i: Int) {

    //  First strip off extra beats added to the inactive dancers
    ctx.contractPaths()

    //  Use another context to do the rest of the call
    val ctx2 = CallContext(ctx,beat=0.0).noSnap()
    if (norm.contains("others"))
      ctx2.dancers.forEach { it.data.active = !it.data.active }
    else
      ctx2.dancers.forEach { it.data.active = true }
    val whilecall = name.toLowerCase()
        .replace("while (the )?".r,"")
        .replace("(the )?others? ".r,"")
    ctx2.applyCalls(whilecall)
    ctx2.appendToSource()
  }

}