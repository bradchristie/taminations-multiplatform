package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations for Web Browsers
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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.platform.System

class Separate : Action("Separate") {

  //  We need to look at all the dancers, not just actives
  //  because sometimes the inactives need to move in
  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() != 4)
      throw CallError("Who is going to Separate?")

    System.log("outer 4: "+ctx.outer(4).map { it.number }.joinToString(" "))

    when {

      //  Case 1 - Outer 4 Separate
      ctx.outer(4).all { it.data.active } -> ctx.actives.forEach { d ->
        val d2 = ctx.dancerClosest(d) { it.data.active }!!
        when {
          CallContext.isRight(d,d2) && CallContext.isFacingIn(d) ->
            d.path += TamUtils.getMove("Quarter Left") +
              TamUtils.getMove("Lead Right").changebeats(2.0).scale(2.0,2.0)
          CallContext.isRight(d,d2) && CallContext.isFacingOut(d) ->
            d.path += TamUtils.getMove("Quarter Left") +
              TamUtils.getMove("Lead Left").changebeats(2.0).scale(2.0,2.0)

          CallContext.isLeft(d,d2) && CallContext.isFacingIn(d) ->
            d.path += TamUtils.getMove("Quarter Right") +
              TamUtils.getMove("Lead Left").changebeats(2.0).scale(2.0,2.0)
          CallContext.isLeft(d,d2) && CallContext.isFacingOut(d) ->
            d.path += TamUtils.getMove("Quarter Right") +
              TamUtils.getMove("Lead Right").changebeats(2.0).scale(2.0,2.0)
          else -> throw CallError("Unable to figure out how to Separate")
        }
      }

      //  Case 2 - Centers facing out Separate
      ctx.actives.all { CallContext.isFacingOut(it) } -> {
        ctx.actives.forEach { d ->
          val d2 = ctx.dancerClosest(d) { it.data.active &&
              (CallContext.isRight(d,it) || CallContext.isLeft(d,it)) }
          when {
            d2 != null && CallContext.isRight(d, d2) -> d.path +=
                TamUtils.getMove("Run Left")
            d2 != null && CallContext.isLeft(d, d2) -> d.path +=
                TamUtils.getMove("Run Right")
            else -> throw CallError("Unable to figure out how to Separate")
          }
        }
        //  Other dancers need to move in
        ctx.dancers.filter { !it.data.active }.forEach { d ->
          val d2 = ctx.dancerClosest(d) { !it.data.active && CallContext.isInFront(d,it) }
          if (d2 != null) {
            val dist = CallContext.distance(d,d2)/2-1
            d.path += TamUtils.getMove("Forward").scale(dist,1.0).changebeats(3.0)
          }
        }
      }

      //  Did not match Case 1 or Case 2
      else -> throw CallError("Cannot figure out how to Separate")
    }

  }

}