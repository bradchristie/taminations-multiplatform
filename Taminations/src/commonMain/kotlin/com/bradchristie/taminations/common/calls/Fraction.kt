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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.platform.attr

class Fraction(norm: String, name: String) : Action(norm, name) {

  private var prevbeats = 0.0
  private var partbeats = 0.0
  private var call:Call = this

  private var numerator = norm[0].toString().i
  private var denominator = norm[1].toString().i

  override fun perform(ctx: CallContext, i: Int) {
    if (i+1 < ctx.callstack.count()) {
      //  Steal the next call off the stack
      call = ctx.callstack[i + 1]
      //  For XML calls there should be an explicit number of parts
      if (call is XMLCall) {
        //  Figure out how many beats are in the fractional call
        //  Calls could have either "parts" or "fractions"
        val parts = (call as XMLCall).xelem.attr("parts") +
                    (call as XMLCall).xelem.attr("fractions")
        if (parts.isNotEmpty()) {
          val partnums = parts.split(";")
          val numParts = partnums.count() + 1
          if (numParts.rem(denominator) != 0)
            throw CallError("Unable to divide ${call.name} into $denominator parts.")
          if (numerator < 1 || numerator >= denominator)
            throw CallError("Invalid fraction.")
          val partsToDo = numParts * numerator / denominator
          partbeats = partnums.slice(0 until partsToDo).map { it.d }.sum()
        }
        //  If parts is empty, will calculate fraction below
        //  in postProcess
      }
      prevbeats = ctx.maxBeats()
    }
    else
      throw CallError("$name of what?")
  }

  //  Call is performed between these two methods

  override fun postProcess(ctx: CallContext, i: Int) {
    //  Coded calls so far do not have explicit parts
    //  so just divide them by the given fraction
    //  Also if an XML call does not have parts just divide the beats
    if (call is Action || partbeats == 0.0) {
      partbeats = (ctx.maxBeats() - prevbeats) * numerator / denominator
    }

    //  Chop off the excess fraction
    ctx.dancers.forEach { d ->
      var mo: Movement? = null
      while (d.path.beats > prevbeats + partbeats)
        mo = d.path.pop()
      //  OK if there's no movement, part of nothing is nothing
      mo?.let {
        if (d.path.beats < prevbeats + partbeats)
          d.path.add(it.clip(prevbeats + partbeats - d.path.beats))
      }
    }

    super.postProcess(ctx, i)

  }

}