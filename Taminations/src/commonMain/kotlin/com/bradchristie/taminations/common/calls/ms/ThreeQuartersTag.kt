package com.bradchristie.taminations.common.calls.ms
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

class ThreeQuartersTag(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("ms")
  override val requires = listOf("ms/fraction_tag","b2/extend")

  override fun perform(ctx: CallContext, i: Int) {
    val dir = if (norm.startsWith("left")) "Left" else ""
    //  All the 4-dancer formations are in Taminations
    if (ctx.actives.count() < 8)
      ctx.applyCalls("$dir 3/4 Tag the Line")
    else if (ctx.isTidal())
      ctx.applyCalls("$dir Quarter Tag","Extend","Extend")
    else if (!ctx.isLines())
      throw CallError("Dancers must be in lines")
    else if (dir == "Left")
      ctx.applyCalls("Face In","Centers Step to a Left-Hand Wave","Extend","Extend")
    else
      super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val dist = d.distanceTo(ctx.dancerInBack(d) ?: ctx.dancerInFront(d)!!)/2.0
    return when (ctx.dancersToRight(d).count() + (if (d.isFacingOut) 4 else 0)) {
      0 -> TamUtils.getMove("Quarter Left").skew(dist-3, 1.0) +
           TamUtils.getMove("Forward 2")
      1 -> TamUtils.getMove("Quarter Left").skew(dist-2,1.0) +
           TamUtils.getMove("Forward") +
           TamUtils.getMove("Extend Right").scale(2.0,1.0).changebeats(2.0)
      2 -> TamUtils.getMove("Lead Right").skew(dist-2,0.0) +
           TamUtils.getMove("Forward 3")
      3 -> TamUtils.getMove("Lead Right").skew(dist-2,0.0) +
           TamUtils.getMove("Forward 2")
      4 -> TamUtils.getMove("Quarter Left").skew(1-dist,1.0) +
           TamUtils.getMove("Forward 2")
      5 -> TamUtils.getMove("Quarter Left").skew(1-dist,1.0) +
           TamUtils.getMove("Forward 3")
      6 -> TamUtils.getMove("Lead Right").skew(2-dist,0.0) +
           TamUtils.getMove("Forward") +
           TamUtils.getMove("Extend Right").scale(2.0,2.0).changebeats(2.0)
      7 -> TamUtils.getMove("Lead Right").skew(2-dist,0.0) +
           TamUtils.getMove("Forward 2")
      else -> Path()  // should never happen
    }
  }

}