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
import com.bradchristie.taminations.common.Dancer

/**
 *   Parent class of all classes that select a group of dancers
 *   such as boys, leaders, centers, belles
 */
abstract class FilterActives(norm:String,name:String=norm) : CodedCall(norm,name) {

  /**
   *  Child classes need to define one of these isActive methods
   *  according to which dancers should be selected
   * @param d Dancer
   * @param ctx CallContext
   * @return true to select dancer
   */
  open fun isActive(d: Dancer, ctx:CallContext, i:Int):Boolean = isActive(d,ctx)
  open fun isActive(d: Dancer, ctx: CallContext):Boolean = isActive(d)
  open fun isActive(d: Dancer) = true

  override fun performCall(ctx: CallContext, i: Int) {
    ctx.dancers.filter { d -> d.data.active }
        .forEach { d -> d.data.active = isActive(d,ctx,i) }
    if (ctx.actives.isEmpty())
      throw CallError("Unable to identify any $name")
  }

}