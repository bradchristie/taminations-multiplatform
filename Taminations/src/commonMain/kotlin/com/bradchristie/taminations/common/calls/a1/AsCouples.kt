package com.bradchristie.taminations.common.calls.a1

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
import com.bradchristie.taminations.common.calls.FourDancerConcept
import kotlin.math.PI

class AsCouplesConcept(callnorm:String,callname:String) : FourDancerConcept(callnorm,callname) {

  override val level = LevelObject("a1")
  override val conceptName = "As Couples"

  //  Build list of (beau, belle) couples
  override fun dancerGroups(ctx: CallContext): List<List<Dancer>> =
      ctx.dancers.filter { d -> d.data.beau } .map { d ->
        val d2 = d.data.partner ?:
          throw CallError("No partner for $d")
        if (!ctx.isInCouple(d,d2))
          throw CallError("$d and $d2 are not a Couple")
        listOf(d,d2)
      }

  override fun startPosition(group: List<Dancer>): Vector {
    val (d,d2) = group
    return if (d.location.length isAbout d2.location.length)
      (d.location + d2.location).scale(0.5, 0.5)
      //  If couple is on axis, probably tidal formation
      //  put single dancer in between
    else if (d.isTidal && d2.isTidal)
      (d.location + d2.location).scale(0.5, 0.5)
      //  Otherwise set to position of the two dancers nearest origin
    else if (d.location.length < d2.location.length)
      d.location
    else
      d2.location
  }

  override fun computeLocation(d:Dancer,
                               m: Movement, mi:Int, beat: Double, groupIndex: Int): Vector {
    //  Position tandem dancers 0.5 units left and right of the concept dancer
    val pos = m.translate(beat).location
    val offset = 0.5
    val isBeau = groupIndex == 0
    val ang = m.rotate(beat).angle
    val v = Vector(offset,0.0).rotate(ang).rotate(if (isBeau) PI/2.0 else -PI/2.0)
    return pos + v
  }

  //  Add handholds
  override fun postAdjustment(ctx:CallContext,cd: Dancer, group: List<Dancer>) {
    group.zip(listOf(Hands.GRIPRIGHT,Hands.GRIPLEFT)).forEach { (d,h) ->
      d.path.addhands(h)
    }
  }

}
