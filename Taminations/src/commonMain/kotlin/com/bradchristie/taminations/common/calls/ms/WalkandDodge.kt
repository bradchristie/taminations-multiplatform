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
import com.bradchristie.taminations.common.TamUtils.getMove
import com.bradchristie.taminations.common.calls.Action

//  This handles both generic Walk and Dodge
//  and directed (somebody) Walk (somebody else) Dodge

class WalkandDodge(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("ms")
  private lateinit var walkctx: CallContext
  private lateinit var dodgectx: CallContext
  private val Dancer.isWalker get() = this.number in walkctx.actives.map { it.number }
  private val Dancer.isDodger get() = this.number in dodgectx.actives.map { it.number }

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() < ctx.dancers.count()) {
      ctx.subContext(ctx.actives) {
        perform(this,i)
      }
      return
    }
    //  Figure out who is a walker and who is a dodger.
    //  Save the results in call contexts
    walkctx = CallContext(ctx)
    walkctx.analyze()
    dodgectx = CallContext(ctx)
    dodgectx.analyze()
    val (walkers, dodgers) = if (norm == "walkanddodge")
      listOf("trailers", "leaders")
    else
      Regex("(.+) walk(?: and)? (.+) dodge").find(name.toLowerCase())?.groupValues!!.drop(1)
    walkers.split("\\s".r).forEach {
      getCodedCall(it)?.performCall(walkctx)
    }
    dodgers.split("\\s".r).forEach {
      getCodedCall(it)?.performCall(dodgectx)
    }
    //  If dancer is not in either set then it is inactive
    ctx.dancers.forEach { d ->
      d.data.active = d.isWalker || d.isDodger
    }
    super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    when {
      d.isDodger -> {
        //  A Dodger.  Figure out which way to dodge.
        val dir = when {
          d.data.beau && ctx.dancerToRight(d)?.isWalker ?: false -> "Right"
          d.data.belle && ctx.dancerToLeft(d)?.isWalker ?: false -> "Left"
          ctx.dancerToRight(d)?.isWalker ?: false -> "Right"
          ctx.dancerToLeft(d)?.isWalker ?: false -> "Left"
          d.data.beau -> "Right"
          d.data.belle -> "Left"
          else -> throw CallError("Dancer $d does not know which way to Dodge")
        }
        if (ctx.isInCouple(d) && d.data.partner!!.isDodger)
          throw CallError("Dodgers would cross each other")
        (if (dir == "Right")
          ctx.dancerToRight(d)
        else
          ctx.dancerToLeft(d))?.let { d2 ->
          val dist = d.distanceTo(d2)
          return getMove("Dodge $dir").scale(1.0, dist / 2.0)
        }
        throw CallError("Unable to calculate Walk and Dodge for dancer $d")
      }
      d.isWalker -> {
        //  A Walker.  Check formation and distance.
        val d2 = ctx.dancerInFront(d)
        if (d2 == null || (ctx.dancerFacing(d) == d2 && d2.isWalker))
          throw CallError("Walkers cannot face each other")
        else {
          val dist = d.distanceTo(d2)
          return getMove("Forward").scale(dist,1.0).changebeats(3.0)
        }
      }
      else -> throw CallError("Dancer $d cannot Walk or Dodge")
    }
  }

}