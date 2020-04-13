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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action

class TagYourNeighbor(norm:String,name:String) : Action(norm,name)  {

  override val level = LevelObject("c2")
  override val requires = listOf("ms/fraction_tag","plus/follow_your_neighbor",
                  "c1/cross_your_neighbor","c2/criss_cross_your_neighbor")

  override fun perform(ctx: CallContext, i: Int) {
    val left = if (norm.startsWith("left")) "Left" else ""
    val basecall = when (norm.replace("left","")) {
      "tagyourneighbor" -> "Follow Your Neighbor"
      "tagyourcrossneighbor" -> "Cross Your Neighbor"
      "tagyourcrisscrossneighbor" -> "Criss Cross Your Neighbor"
      else -> throw CallError("Tag what?")  // should not happen
    }
    ctx.applyCalls("$left Half Tag",basecall)
  }

}