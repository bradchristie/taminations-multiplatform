package com.bradchristie.taminations.common.calls.c1
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

class TandemConcept(callnorm:String,callname:String) : FourDancerConcept(callnorm,callname) {

  override val level = LevelObject("c1")
  override val conceptName = "Tandem"

  //  Build list of (leader, trailer) tandems
  override fun dancerGroups(ctx: CallContext): List<List<Dancer>> =
    ctx.dancers.filter { d -> d.data.leader } .map { d ->
      val d2 = ctx.dancerInBack(d) ?:
          throw CallError("No tandem for dancer $d")
      if (!d2.data.trailer)
        throw CallError("Dancers $d and $d2 are not a Tandem")
      listOf(d,d2)
    }

  override fun startPosition(group: List<Dancer>): Vector {
    val (d, d2) = group
    return if (d.location.length isAbout d2.location.length)
    //  If tandem is straddling an axis, put single dancer on axis
      (d.location + d2.location).scale(0.5, 0.5)
    //  If tandem is on an axis (uncommon), probably tight column formation
    //  put single dancer in between
    else if (d.isOnAxis && d2.isOnAxis)
      (d.location + d2.location).scale(0.5, 0.5)
    //  Otherwise set to position of the two dancers nearest origin
    else if (d.location.length < d2.location.length)
      d.location
    else
      d2.location
  }

  override fun computeLocation(cd: Dancer,
                               m: Movement, beat: Double, groupIndex: Int): Vector {
    //  Position tandem dancers 0.5 units in front and behind concept dancer
    val offset = 0.5
    val isLeader = groupIndex == 0
    val pos = m.translate(beat).location
    val ang = m.rotate(beat).angle
    val v = Vector(offset,0.0).rotate(ang).rotate(if (isLeader) 0.0 else PI)
    return pos + v
  }

  override fun postAdjustment(ctx:CallContext,cd: Dancer, group: List<Dancer>) {
    //  If there is space, spread out the tandem a bit
    val (leader,trailer) = group
    if ((ctx.dancerInFront(leader)?.distanceTo(leader) ?: 2.0) > 1.0 &&
        (ctx.dancerInBack(trailer)?.distanceTo(trailer) ?: 2.0) > 1.0) {
      leader.path.skewFromEnd(0.5, 0.0)
      trailer.path.skewFromEnd(-0.5, 0.0)
    }
  }

}

