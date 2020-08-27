package com.bradchristie.taminations.common.calls.b1
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
import kotlin.math.PI

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
    //  (if there are 6 dancers, must be 2 columns of 3)
    else if (ctx.isColumns())
      ctx.applyCalls("column circulate")
    else if (ctx.actives.count() == 6 && ctx.isColumns(3))
      ctx.applyCalls("column circulate")
    //  If none of those, but tBones, or 6 dancers, calculate each path individually
    else if (ctx.actives.count() == 6 || ctx.isTBone()) {
      super.perform(ctx, i)
      if (ctx.isCollision())
        throw CallError("Cannot handle dancer collision here.")
    }
    //  Otherwise ... ???
    else
      throw CallError("Cannot figure out how to Circulate.")
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  The "easier" case - 4 dancer in any type of box
    if (ctx.actives.count() == 4) {
      if (d.data.leader) {
        //  Find another active dancer in the same line and move to that spot
        val d2 = ctx.dancerClosest(d) { dx ->
          dx.data.active && (dx isRightOf d || dx isLeftOf d)
        }
        if (d2 != null) {
          val dist = d.distanceTo(d2)
          //  Pass right shoulders if crossing another dancer
          val xScale = if (d2.data.leader && d2 isRightOf d) 1 + dist / 3 else dist / 3
          return TamUtils.getMove(if (d2 isRightOf d) "Run Right" else "Run Left")
              .scale(xScale, dist / 2).changebeats(4.0)
        }
      } else if (d.data.trailer) {
        //  Looking at active dancer?  Then take its place
        //  TODO maybe allow diagonal circulate?
        val d2 = ctx.dancerInFront(d)
        if (d2 != null && d2.data.active) {
          val dist = d.distanceTo(d2)
          return if (d2.data.leader)
            TamUtils.getMove("Forward").scale(dist, 1.0).changebeats(4.0)
          else  //  Facing dancers - pass right shoulders
            TamUtils.getMove("Extend Left").scale(dist / 2.0, 0.5).changebeats(2.0) +
                TamUtils.getMove("Extend Right").scale(dist / 2.0, 0.5).changebeats(2.0)
        }
      }
    }

    //  A littler harder - 6 dancers not in columns
    else if (ctx.actives.count() == 6) {
      //  If there is a dancer directly or diagonally in front, go there
      val d2 = ctx.dancerClosest(d) {
        it.data.active && d.angleToDancer(it).abs.isLessThan(PI/2.0) &&
            !d.angleFacing.isAround(it.angleFacing+PI)
      }
      return if (d2 != null) {
        val v = d.vectorToDancer(d2)
        if (d.angleFacing.isAround(d2.angleFacing))
          TamUtils.getMove("Extend Left").changebeats(3.0).scale(v.x,v.y)
        else if (d.angleFacing.isAround(d2.angleFacing+PI/2))
          TamUtils.getMove("Lead Left").changebeats(3.0).scale(v.x,v.y)
        else if (d.angleFacing.isAround(d2.angleFacing-PI/2))
          TamUtils.getMove("Lead Right").changebeats(3.0).scale(v.x,-v.y)
        else
          throw CallError("Unable to calculate Circulate path.")
      } else {   //  Otherwise look for a dancer to the side
        val d3 = ctx.dancerClosest(d) { it.data.active && it.isRightOf(d) }
          ?: ctx.dancerClosest(d) { it.data.active && it.isLeftOf(d) }
        if (d3 != null) {
          val v = d.vectorToDancer(d3)
          TamUtils.getMove("Run Left").scale(1.0, v.y / 2.0)
        } else
          throw CallError("Unable to calculate Circulate for dancer $d.")
      }
    }

    //  The harder case - 8 dancers in a t-bone
    else if (ctx.actives.count() == 8) {
      //  Column-like dancer
      if (ctx.dancersInFront(d).count() + ctx.dancersInBack(d).count() == 3) {
        return when {
          ctx.dancersInFront(d).count() > 0 ->
            if (ctx.dancerFacing(d) != null)
              TamUtils.getMove("Extend Left").scale(1.0,0.5).changebeats(2.0) +
              TamUtils.getMove("Extend Right").scale(1.0,0.5).changebeats(2.0)
            else
              TamUtils.getMove("Forward 2").changebeats(4.0)
          ctx.dancersToLeft(d).count() == 1 && ctx.isFacingSameDirection(d,ctx.dancerToLeft(d)!!) ->
            TamUtils.getMove("Flip Left").changebeats(4.0)
          ctx.dancersToLeft(d).count() == 1 ->
            TamUtils.getMove("Run Left").changebeats(4.0)
          ctx.dancersToRight(d).count() == 1 ->
            TamUtils.getMove("Run Right").changebeats(4.0)
          else ->
            throw CallError("Could not calculate Circulate for dancer $d")
        }
      }
      //  Line-like dancer
      else if (ctx.dancersToLeft(d).count() + ctx.dancersToRight(d).count() == 3) {
        if (ctx.dancersInFront(d).count() == 1) {
          return if (ctx.dancerFacing(d) != null)
            TamUtils.getMove("Extend Left").scale(1.0,0.5).changebeats(2.0) +
            TamUtils.getMove("Extend Right").scale(1.0,0.5).changebeats(2.0)
          else
            TamUtils.getMove("Forward 2").changebeats(4.0)
        }
        return when (ctx.dancersToLeft(d).count()) {
          0 -> {
            val d2 = ctx.dancersToRight(d).last()
            if (ctx.isFacingSameDirection(d,d2))
              TamUtils.getMove("Run Right").scale(3.0,3.0).changebeats(4.0)
            else
              TamUtils.getMove("Run Right").scale(2.0,3.0).changebeats(4.0)
          }
          1 -> {
            TamUtils.getMove("Run Right").changebeats(4.0)
          }
          2 -> {
            if (ctx.isFacingSameDirection(d,ctx.dancerToLeft(d)!!))
              TamUtils.getMove("Flip Left").changebeats(4.0)
            else
              TamUtils.getMove("Run Left").changebeats(4.0)
          }
          3 -> {
            TamUtils.getMove("Run Left").scale(2.0,3.0).changebeats(4.0)
          }
          else ->
            throw CallError("Could not calculate Circulate for dancer $d")
        }
      }
    }

    throw CallError("Cannot figure out how to Circulate.")
  }

}