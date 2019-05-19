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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.r

class WheelAnd(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")
  override val requires = listOf("c1/wheel_and_anything")

  override fun perform(ctx: CallContext, i: Int) {
    val (wheelcall,andcall) = name.split("and".r,2)
    val reverse = if (wheelcall.toLowerCase().contains("reverse")) "Reverse" else ""
    //  Find the 4 dancers to Wheel
    val facingOut = ctx.dancers.filter { d -> d.isFacingOut }
    when {
      facingOut.containsAll(ctx.outer(4)) ->
        ctx.applyCalls("Outer 4 $reverse Wheel While Center 4 $andcall")
      facingOut.containsAll(ctx.center(4)) ->
        ctx.applyCalls("Center 4 $reverse Wheel While Outer 4 Step And $andcall")
      else -> throw CallError("Unable to find dancers to Wheel")
    }
  }

}