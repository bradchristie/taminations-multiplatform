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

class VerticalTag(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")
  override val requires = listOf("b2/extend")

  override fun perform(ctx: CallContext, i: Int) {
    ctx.analyzeActives()
    //  This calls performOne below, which performs Vertical 1/4 Tag
    super.perform(ctx, i)
    //  Now extend as requested
    if (norm.contains("12"))
      ctx.applyCalls("extend")
    else if (norm.contains("34"))
      ctx.applyCalls("extend", "extend")
    else if (!norm.contains("14"))
      ctx.applyCalls("extend", "extend", "extend")
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    if (!d.data.beau && !d.data.belle)
      throw CallError("Dancer $d is not part of a couple")
    if (!d.data.leader && !d.data.trailer)
      throw CallError("Dancer $d is not in a box")
    val dp = d.data.partner ?: throw CallError("Cannot find partner for $d")
    val dt = (if (d.data.leader) ctx.dancerInBack(d) else ctx.dancerInFront(d))
      ?: throw CallError("Cannot find dancer in front or behind $d")
    //  Distance from this dancer to center point of box
    val w = d.distanceTo(dp) / 2.0
    val h = d.distanceTo(dt) / 2.0

    if (norm.contains("left")) {
      if (d.data.leader) {
        return if (d.data.beau && dp.data.belle)
          TamUtils.getMove("Flip Right").skew(-h, 3.0 - w)
        else if (d.data.belle)
          TamUtils.getMove("Flip Left").skew(0.5, w - 2.0)
        else
          TamUtils.getMove("Flip Right").skew(0.5, 2.0 - w)
      } else {  // trailer
        return if (d.data.belle && dp.data.beau)
          TamUtils.getMove("Dodge Left").changebeats(3.0).skew(-0.5, w - 2.0)
        else if (d.data.belle)
          TamUtils.getMove("Forward").changebeats(3.0).skew(h - 1.0, w - 1.0)
        else
          TamUtils.getMove("Extend Right").changebeats(3.0).skew(h - 1.0, w - 2.0)
      }

    } else {
      if (d.data.leader) {
        //  Leader always goes behind unless belle of a couple facing out
        return if (d.data.belle && dp.data.beau)
          TamUtils.getMove("Flip Left").skew(-h, w - 3.0)
        else if (d.data.beau)
          TamUtils.getMove("Flip Right").skew(0.5, 2.0 - w)
        else
          TamUtils.getMove("Flip Left").skew(0.5, w - 2.0)
      } else {  // trailer
        //  Trailer always goes in front unless beau of a couple facing in
        return if (d.data.beau && dp.data.belle)
          TamUtils.getMove("Dodge Right").changebeats(3.0).skew(-0.5, 2.0 - w)
        else if (d.data.beau)
          TamUtils.getMove("Forward").changebeats(3.0).skew(h - 1.0, 1.0 - w)
        else
          TamUtils.getMove("Extend Left").changebeats(3.0).skew(h - 1.0, w)
      }
    }
  }

}