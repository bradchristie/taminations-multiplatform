package com.bradchristie.taminations.common.calls.a1
/*

  Taminations Square Dance Animations for Web Browsers
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
import com.bradchristie.taminations.common.r

class Cloverleaf : Action("Cloverleaf") {

  override val level = LevelObject("ms")
  override val requires = listOf("a1/clover_and_anything","a1/cross_clover_and_anything")

  //  We get here only if standard Cloverleaf with all 8 dancers active fails.
  //  So do a 4-dancer cloverleaf
  override fun perform(ctx: CallContext, i:Int) {
    if (ctx.outer(4).all { it.data.active })
      ctx.applyCalls("Clover and Nothing")
    else {
      ctx.applyCalls("Clover and Step")
    }
  }

}

class CloverAnd(norm:String,name:String) : Action(norm,name) {

  override val level =
      if (name == "Clover and Nothing" || name == "Clover and Step")
        LevelObject("ms")
      else
        LevelObject("a1")

  override fun perform(ctx: CallContext, i: Int) {
    //  Find the 4 dancers to Cloverleaf
    //  First check the outer 4
    val outer4 = ctx.dancers.asSequence().sortedBy{ d -> d.location.length}.drop(4).toList()
    //  If that fails try for 4 dancers facing out
    val facingOut = ctx.dancers.filter { d -> d.isFacingOut }
    val clovers = when {
      //  Don't use outer4 directly, instead filter facingOut
      //  This preserves the original order, required for mapping
      facingOut.containsAll(outer4) -> facingOut.filter { d -> d in outer4 }
      facingOut.count() == 4 -> facingOut
      else -> throw CallError("Unable to find dancers to Cloverleaf")
    }
    //  Make those 4 dancers Cloverleaf
    val (clovercall,andcall) = name.split("and".r,2)
    ctx.subContext(clovers) {
      applyCalls("$clovercall and")
      //  "Clover and <nothing>" is stored in A-1 but is really Mainstream
      level = LevelObject("ms")
    }

    //  And the other 4 do the next call at the same time
    ctx.subContext(ctx.dancers.filterNot { d -> d in clovers }) {
      dancers.forEach { d -> d.data.active = true }
      applyCalls(andcall)
    }
  }

}