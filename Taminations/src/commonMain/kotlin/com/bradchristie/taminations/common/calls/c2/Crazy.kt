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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.r

class Crazy(norm:String,name:String) : Action(norm,name)  {

  override val level = LevelObject("c2")
  override val requires = listOf("a2/box_counter_rotate",
      "a2/split_counter_rotate",
      "b1/circulate")

  override fun perform(ctx: CallContext, i: Int) {
    val crazycall = name.toLowerCase().replace(".*crazy ".r,"")
    val crazy8 = when (crazycall) {
      in "counter rotate.*".r -> "Split $crazycall"
      in "circulate.*".r -> "Split $crazycall"
      else -> crazycall
    }
    val crazy4 = when (crazycall) {
      in "counter rotate.*".r -> "Center 4 Box $crazycall"
      else -> "Center 4 $crazycall"
    }

    ctx.applyCalls(if (norm.contains("reversecrazy")) crazy4 else crazy8)
    ctx.matchStandardFormation()
    ctx.applyCalls(if (norm.contains("reversecrazy")) crazy8 else crazy4)
    if (!norm.startsWith("12")) {
      ctx.matchStandardFormation()
      ctx.applyCalls(if (norm.contains("reversecrazy")) crazy4 else crazy8)
      if (!norm.startsWith("34")) {
        ctx.matchStandardFormation()
        ctx.applyCalls(if (norm.contains("reversecrazy")) crazy8 else crazy4)
      }
    }
  }
}