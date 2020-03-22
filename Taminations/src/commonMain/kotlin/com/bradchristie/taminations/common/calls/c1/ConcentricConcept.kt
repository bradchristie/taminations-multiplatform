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
import com.bradchristie.taminations.platform.System

class ConcentricConcept(callnorm:String,callname:String) : FourDancerConcept(callnorm,callname) {

  override val conceptName = "Concentric"

  var minX = 0.0
  var minY = 0.0

  override fun dancerGroups(ctx: CallContext): List<List<Dancer>> {
    minX = ctx.actives.map { d -> d.location.x.abs }.min()!!
    minY = ctx.actives.map { d -> d.location.y.abs }.min()!!
    return ctx.actives.map { d -> listOf(d) }
  }

  override fun startPosition(group: List<Dancer>): Vector {
    val loc = group.first().location
    return if (minX > minY)
      Vector(loc.x - 2.0*loc.x.sign,loc.y)
    else
      Vector(loc.x, loc.y-2.0*loc.y.sign)
  }

  override fun computeLocation(cd: Dancer,
                               m: Movement, beat: Double, groupIndex: Int): Vector {
    //  Extend the dancer's current position by 2 units
    val loc = cd.location
    val factor = (loc.length + 2.0) / loc.length
    val v =  cd.location * factor
    //System.log("$sd ${beat.s} $loc $v ${v.ds(sd)}")
    return v
  }

}