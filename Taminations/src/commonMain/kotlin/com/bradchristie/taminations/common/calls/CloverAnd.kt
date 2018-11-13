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
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.r
import com.bradchristie.taminations.platform.System

class Cloverleaf : Action("Cloverleaf") {

  override val level = LevelObject("ms")
  override val requires = listOf("a1/clover_and_anything")

  //  We get here only if standard Cloverleaf with all 8 dancers active fails.
  //  So do a 4-dancer cloverleaf
  override fun perform(ctx: CallContext, i:Int) {
    if (ctx.outer(4).all { it.data.active })
      ctx.applyCalls("Clover and Nothing")
    else {
      System.log("Inner 4 active")
      ctx.applyCalls("Clover and Step")
    }
  }

}

class CloverAnd(name:String) : Action(name) {

  override val level = LevelObject("a1")

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
    val (clovercall,andcall) = name.split(" and ".r,2)
    val ctx1 = CallContext(ctx,clovers)
    ctx1.applyCalls("$clovercall and")
    ctx1.appendToSource()
    //  And the other 4 do the next call at the same time
    val ctx2 = CallContext(ctx,ctx.dancers.filterNot { d -> d in clovers })
    ctx2.dancers.forEach { d -> d.data.active = true }
    System.log("Applying $andcall to ${ctx2.dancers.count()} dancers")
    ctx2.applyCalls(andcall)
    ctx2.appendToSource()
  }

}