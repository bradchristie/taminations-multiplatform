package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.Path

abstract class Action(norm:String,name:String=norm) : CodedCall(norm,name) {

  override fun performCall(ctx: CallContext, i: Int) {
    perform(ctx,i)
    ctx.dancers.forEach { d ->
      d.path.recalculate()
      d.animateToEnd()
    }
  }

  //  Default method to perform one call
  //  Pass the call on to each active dancer
  //  Then append the returned paths to each dancer
  open fun perform(ctx: CallContext, i:Int=0) {
    //  Get all the paths with performOne calls
    ctx.actives.forEach { d ->
      d.path.add(performOne(d,ctx))
    }
  }

  //  Default method for one dancer to perform one call
  //  Returns an empty path (the dancer just stands there)
  open fun performOne(d: Dancer, ctx: CallContext): Path = Path()

}