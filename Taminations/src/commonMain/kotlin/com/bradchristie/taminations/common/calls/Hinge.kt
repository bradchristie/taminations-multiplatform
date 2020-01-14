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

class Hinge(norm:String, name:String) : Action(norm,name) {

  private var myLevel = LevelObject("ms")
  override val level get() = myLevel

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Find the dancer to hinge with
    val leftCount = ctx.dancersToLeft(d).filter { it.data.active }.count()
    val rightCount = ctx.dancersToRight(d).filter { it.data.active }.count()
    val d2 = when {
      leftCount.isOdd && rightCount.isEven -> ctx.dancerToLeft(d)!!
      leftCount.isEven && rightCount.isOdd -> ctx.dancerToRight(d)!!
      else -> throw CallError("Dancer $d has no one to hinge with.")
    }
    if (!d2.data.active)
     throw CallError("Dancer $d has no one to hinge with.")
    if (!ctx.isInWave(d,d2))
      myLevel = LevelObject("a1")
    val dist = d.distanceTo(d2)
    return when {
      //  Hinge from mini-wave, left or right handed
      ctx.isInWave(d,d2) ->
        TamUtils.getMove(if (d2 isRightOf d) "Hinge Right" else "Hinge Left").scale(1.0,dist/2)
      //  Left Partner Hinge
      ctx.isInCouple(d,d2) && d2 isRightOf d && name.contains("Left") ->
        TamUtils.getMove("Quarter Right").skew(-1.0,-1.0*(dist/2))
      ctx.isInCouple(d,d2) && d2 isLeftOf d && name.contains("Left") ->
        TamUtils.getMove("Lead Left").scale(1.0,dist/2)
      //  Partner Hinge
      ctx.isInCouple(d,d2) && d2 isRightOf d ->
        TamUtils.getMove("Lead Right").scale(1.0,dist/2)
      ctx.isInCouple(d,d2) && d2 isLeftOf d ->
        TamUtils.getMove("Quarter Left").skew(-1.0,dist/2)
      else ->
        ctx.dancerCannotPerform(d,name)
    }
  }

}