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

import com.bradchristie.taminations.common.*

class And : FilterActives("and") {

  override fun performCall(ctx: CallContext, i: Int) {
    if (i < 1)
      throw CallError("Use \"and\" to combine calls")
    super.performCall(ctx, i)
  }

  //  If the previous call was retrieved from XML and has a selector
  //  such as Boys or Heads then we need to filter the actives here
  override fun isActive(d: Dancer, ctx: CallContext, i:Int): Boolean {
    var retval = d.data.active
    ctx.callstack.take(i).forEach { call ->
      var prevword = ""
      call.name.split(" ").forEach {
        when (TamUtils.normalizeCall(it)) {
          "boy"-> retval = retval && d.gender == Gender.BOY
          "girl" -> retval = retval && d.gender == Gender.GIRL
          "head" -> retval = retval && d.number_couple.i % 2 == 1
          "side" -> retval = retval && d.number_couple.i % 2 == 0
          "beau" -> retval = retval && d.data.beau
          "belle" -> retval = retval && d.data.belle
          "center" -> retval = if (prevword == "very")
              retval && d.data.verycenter
            else
              retval && d.data.center
          "end" -> retval = retval && d.data.end
          "lead" -> retval = retval && d.data.leader
          "trail" -> retval = retval && d.data.trailer
        }
        prevword = it.toLowerCase()
      }
    }
    return retval
  }

}