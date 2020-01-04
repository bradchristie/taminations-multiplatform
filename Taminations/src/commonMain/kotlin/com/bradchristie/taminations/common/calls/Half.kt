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
import com.bradchristie.taminations.common.Movement
import com.bradchristie.taminations.platform.attr
import com.bradchristie.taminations.common.d

class Half : Action("Half") {

  var prevbeats = 0.0
  var halfbeats = 0.0
  var call:Call = this

  override fun perform(ctx: CallContext, i: Int) {
    if (i+1 < ctx.callstack.count()) {
      //  Steal the next call off the stack
      call = ctx.callstack[i + 1]
      //  For XML calls there should be an explicit number of parts
      if (call is XMLCall) {
        //  Figure out how many beats are in half the call
        val parts = (call as XMLCall).xelem.attr("parts")
        if (parts.isNotEmpty()) {
          val partnums = parts.split(";")
          halfbeats = partnums.slice(0 until (partnums.count() + 1) / 2).map { it.d }.sum()
        }
      }
      prevbeats = ctx.maxBeats()
    }
    else
      throw CallError("Half of what?")
  }

  //  Call is performed between these two methods

  override fun postProcess(ctx: CallContext, i: Int) {
    //  Coded calls so far do not have explicit parts
    //  so just divide them in two
    //  Also if an XML call does not have parts just divide beats in two
    if (call is Action || halfbeats == 0.0) {
      halfbeats = (ctx.maxBeats() - prevbeats) / 2.0
    }

    //  Chop off the excess half
    ctx.dancers.forEach { d ->
      var mo: Movement? = null
      while (d.path.beats > prevbeats + halfbeats)
        mo = d.path.pop()
      //  OK if there's no movement, half of nothing is nothing
      mo?.let {
        if (d.path.beats < prevbeats + halfbeats)
          d.path.add(mo.clip(prevbeats + halfbeats - d.path.beats))
      }
    }

    super.postProcess(ctx, i)

  }

}