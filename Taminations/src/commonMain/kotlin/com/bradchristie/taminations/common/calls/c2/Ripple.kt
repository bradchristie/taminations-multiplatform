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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action

class Ripple(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c2")
  override val requires = listOf("b2/trade","ms/hinge","a1/partner_hinge")
  private lateinit var dirs:Map<Dancer,Boolean>

  private fun findTraders(ctx:CallContext,
                          actives:MutableList<Dancer>) : List<Dancer> {
    val traders = actives.toMutableList()
    val ended = mutableListOf<Dancer>()
    actives.forEach { d ->
      val d2 = if (dirs.getValue(d)) ctx.dancerToRight(d) else ctx.dancerToLeft(d)
      if (d2 != null)
        traders.add(d2)
      else {
        ended.add(d)
        traders.remove(d)
      }
    }
    actives.removeAll(ended)
    return traders
  }

  override fun perform(ctx: CallContext, i: Int) {
    val actives = ctx.actives.toMutableList()
    if (actives.count() == ctx.dancers.count())
      throw CallError("Who is going to Ripple?")
    val countstr = norm.replace("(right|left)?ripple".r,"").replace("the(line|wave)".r,"9")
    val half = countstr.endsWith("12")
    val count = countstr.replace("12","").toIntOrNull()
      ?: throw CallError("Ripple how much?")
    dirs = actives.associateWith { d ->
      when {
        norm.contains("right") -> true
        norm.contains("left") -> false
        else -> d.isCenterRight
      }
    }

    repeat(count) {
      val traders = findTraders(ctx,actives)
      ctx.subContext(traders) {
        dancers.forEach { it.data.active = true }
        applyCalls("Trade")
      }
      ctx.extendPaths()
      dirs = actives.associateWith { d -> !dirs.getValue(d) }
    }

    if (half) {
      val traders = findTraders(ctx,actives)
      ctx.subContext(traders) {
        dancers.forEach { it.data.active = true }
        applyCalls("Hinge")
      }
    }

  }

}