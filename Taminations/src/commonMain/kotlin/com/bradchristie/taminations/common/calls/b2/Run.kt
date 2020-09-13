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
import com.bradchristie.taminations.common.calls.Action

class Run(norm:String, name:String) : Action(norm,name) {

  override val level = LevelObject("b2")

  private fun runOne(d:Dancer, d2: Dancer, dir:String) {
    val dist = d.distanceTo(d2)
    d.path.add(TamUtils.getMove("Run $dir").scale(1.0,dist/2))
    val m2 = when {
      d isRightOf d2 -> "Dodge Right"
      d isLeftOf d2 -> "Dodge Left"
      d isInFrontOf d2 -> "Forward 2"
      d isInBackOf d2 -> "Back 2"   //  really ???
      else -> "Stand"  // should never happen
    }
    d2.path.add(TamUtils.getMove(m2).scale(dist/2.0,dist/2))
  }

  override fun perform(ctx:CallContext, i:Int) {
    val dancersToRun = ctx.dancers.filter { it.data.active }.toMutableSet()
    val dancersToWalk = ctx.dancers.filter { !it.data.active }.toMutableSet()
    var usePartner = false
    while (dancersToRun.isNotEmpty()) {
      var foundRunner = false
      dancersToRun.forEach { d ->
        val dleft = ctx.dancerToLeft(d)
        val dright = ctx.dancerToRight(d)
        val isLeft = dleft != null && dancersToWalk.contains(dleft) &&
            norm != "runright"
        val isRight = dright != null && dancersToWalk.contains(dright) &&
            norm != "runleft"
        if (!isLeft && !isRight)
          throw CallError("Dancer $d cannot Run")
        else if (!isLeft ||
            (usePartner && dright!=null && dright == d.data.partner)) {
          //  Run Right
          val d2 = dright ?: throw CallError("Dancer $d unable to Run")
          runOne(d,d2,"Right")
          dancersToRun.remove(d)
          dancersToWalk.remove(d2)
          foundRunner = true
          usePartner = false
        }
        else if (!isRight ||
          (usePartner && dleft!=null && dleft == d.data.partner)) {
          //  Run Left
          val d2 = dleft ?: throw CallError("Dancer $d unable to Run")
          runOne(d,d2,"Left")
          dancersToRun.remove(d)
          dancersToWalk.remove(d2)
          foundRunner = true
          usePartner = false
        }
      }
      if (!foundRunner) {
        if (!usePartner)
          usePartner = true
        else
          throw CallError("Unable to calculate $name for this formation.")
      }
    }
  }

}