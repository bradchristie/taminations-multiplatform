package com.bradchristie.taminations.common.calls.c2
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
import com.bradchristie.taminations.platform.System
import kotlin.math.sign

class DisconnectedConcept(norm: String, name: String) : FourDancerConcept(norm, name) {

  override val level = LevelObject("c2")
  override val conceptName = "Disconnected"

  var xIsMajor = true
  val Vector.major:Double get() = if (xIsMajor) this.x else this.y
  val Vector.minor:Double get() = if (xIsMajor) this.y else this.x
  var closer = 0.0
  var further = 0.0

  override fun dancerGroups(ctx: CallContext): List<List<Dancer>> =
      ctx.actives.map { d -> listOf(d) }

  override fun startPosition(group: List<Dancer>): Vector {
    val d = group.firstOrNull() ?: throw CallError("Group error")
    val pos = if (d.location.major.abs.isAbout(closer)) 1.0 else 3.0
    val s = d.location.major.sign
    System.log("${d.location} -> ${Vector(pos*s,0.0)}")
    return if (xIsMajor) Vector(pos*s,0.0) else Vector(0.0,pos*s)
  }

  //  Not working - this computes an absolute position
  //  based on the current absolute position
  //  but needs to compute a relative position
  //  based on the current movement + beat
  override fun computeLocation(d: Dancer, m: Movement, mi: Int, beat: Double, groupIndex: Int): Vector {
    //  Map positions on the major axis from 0 to 1 -> 0 to closer
    //  Map positions on the major axis from 1 to 3 -> closer to further
    val pos = d.location.major
    val pos2 = if (pos.abs < 1.0)
      pos * closer
    else
      (pos - pos.sign) * 0.5 * (further - closer) + closer*pos.sign
    return if (xIsMajor) Vector(pos2,d.location.y) else Vector(d.location.x,pos2)
  }

  override fun perform(ctx: CallContext, i: Int) {
    if (!ctx.isTidal())
      throw CallError("Disconnected Concept only implemented for tidal formations")
    if (ctx.actives.count() != 4)
      throw CallError("Disconnected must be done by 4 dancers.")

    xIsMajor = ctx.dancers[0].location.y.isAbout(0.0)
    System.log("xIsMajor: $xIsMajor")
    closer = ctx.actives.minOf { it.location.major.abs }
    further = ctx.actives.maxOf { it.location.major.abs }
    super.perform(ctx, i)
  }

}