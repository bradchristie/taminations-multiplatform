package com.bradchristie.taminations.common.calls.b2
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
import com.bradchristie.taminations.common.calls.ActivesOnlyAction

class CrossRun(norm:String,name:String) : ActivesOnlyAction(norm,name) {

  override val level = LevelObject("b2")

  /*
  New algorithm -
  Accept specifier as part of call so active dancers include dodgers
  Parse specifier and apply to context to get runners
  For each runner
    Find active dancer 2 dancers away it can run to
  For each dodger
      Find a direction they can move to a runner's spot
        I don't think there can be more than one
        in a symmetric formation
      Dodge or move forward/back to that spot
   */

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() < ctx.dancers.count()) {
      super.perform(ctx, i)
      return
    }
    //  Get runners and dodgers
    val spec = name.replace("cross\\s*run".ri,"")
    val specCtx = CallContext(ctx)
    specCtx.applySpecifier(spec)
    val runners = ctx.actives.filter { specCtx.actives.contains(it) }
    val dodgers = ctx.actives.filter { !runners.contains(it) }
    //  Loop through runners and figure out where they are going
    runners.forEach { d ->
      //  Find active dancer 2 dancers away it can run to
      val dright = ctx.dancersToRight(d).getOrNull(1)
      val dleft = ctx.dancersToLeft(d).getOrNull(1)
      val dir = when {
        dright?.data?.active != true -> "Left"
        dleft?.data?.active != true -> "Right"
        //  If 2 dancers away both left and right are active,
        //  choose dancer furthest from the center,
        //    as it must be a tidal formation and runners should not cross center
        dright.location.length > dleft.location.length -> "Right"
        else -> "Left"
      }
      val d2 = (if (dir == "Right") dright else dleft) ?:
          throw CallError("Dancer $d cannot Cross Run")
      val dist = d.distanceTo(d2)
      d.path.add(TamUtils.getMove("Run $dir").scale(1.5,dist/2.0))
    }
    //  Loop through each dodger and figure out which way they are moving
    dodgers.forEach { d ->
      //  Find a direction they can move to a runner's spot
      //  I don't think there can be more than one
      //  in a symmetric formation
      val dright = ctx.dancerToRight(d)
      val dleft = ctx.dancerToLeft(d)
      val dfront = ctx.dancerInFront(d)
      val dback = ctx.dancerInBack(d)
      //  Dodge or move forward/back to that spot
      when {
        dright != null && runners.contains(dright) ->
          d.path.add(TamUtils.getMove("Dodge Right")).scale(1.0,d.distanceTo(dright)/2.0)
        dleft != null && runners.contains(dleft) ->
          d.path.add(TamUtils.getMove("Dodge Left")).scale(1.0,d.distanceTo(dleft)/2.0)
        dfront != null && runners.contains(dfront) ->
          d.path.add(TamUtils.getMove("Forward"))
            .changebeats(3.0).scale(d.distanceTo(dfront),1.0)
        dback != null && runners.contains(dback) ->
          d.path.add(TamUtils.getMove("Forward"))
            .changebeats(3.0).scale(d.distanceTo(dback),1.0)
        else ->
          throw CallError("Unable to calculate Cross Run action for dancer $d")
      }
    }
  }

}