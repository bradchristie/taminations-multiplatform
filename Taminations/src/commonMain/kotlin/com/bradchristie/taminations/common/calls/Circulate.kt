package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations
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

import com.bradchristie.taminations.common.*

class Circulate : Action("Circulate") {

  override val requires = listOf("b1/circulate")

  override fun perform(ctx: CallContext, i:Int) {
    //  If just 4 dancers, try Box Circulate
    if (ctx.actives.count() == 4) {
      if (ctx.actives.all { d -> d.data.center }) {
        try {
          ctx.applyCalls("box circulate")
        } catch (err: CallError) {
          //  That didn't work, try to find a circulate path for each dancer
          super.perform(ctx, i)
        }
      }
      else
        super.perform(ctx,i)
    }
    //  If two-faced lines, do Couples Circulate
    else if (ctx.isTwoFacedLines())
      ctx.applyCalls("couples circulate")
    //  If in waves or lines, then do All 8 Circulate
    else if (ctx.isLines())
      ctx.applyCalls("all 8 circulate")
    //  If in columns, do Column Circulate
    else if (ctx.isColumns())
      ctx.applyCalls("column circulate")
    //  Otherwise ... ???
    else
      throw CallError("Cannot figure out how to Circulate.")
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    if (d.data.leader) {
      //  Find another active dancer in the same line and move to that spot
      val d2 = ctx.dancerClosest(d) { dx ->
        dx.data.active && (dx isRightOf d || dx isLeftOf d) }
      if (d2 != null) {
        val dist = d.distanceTo(d2)
        return TamUtils.getMove(if (d2 isRightOf d) "Run Right" else "Run Left")
        .scale(dist/3,dist/2).changebeats(4.0)
      }
    } else if (d.data.trailer) {
      //  Looking at active leader?  Then take its place
      //  TODO maybe allow diagonal circulate?
      val d2 = ctx.dancerInFront(d)
      if (d2 != null && d2.data.active) {
        val dist = d.distanceTo(d2)
        return TamUtils.getMove("Forward").scale(dist,1.0).changebeats(4.0)
      }
    }
    throw CallError("Cannot figure out how to Circulate.")
  }

}