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

/*
    4-dancer calls that Concentric can use
    B-1
       Bend the Line
       Chain Down the Line
       Dosado (?)
       Ladies Chain
       Lead Right/Left
       Pass Thru
       Right and Left Thru
       Box Circulate
       Square Thru
       Star Thru
       Veer Left/Right

     B-2
       Extend
       Flutterwheel
       Pass the Ocean
       Sweep a Quarter
       Swing Thru
       Touch a Quarter
       Wheel and Deal
       Zoom

     Mainstream
       Cast Off 3/4
       Dixie Style to a Wave
       1/4 1/2 3/4 Full Tag the Line
       Recycle
       Scoot Back
       Slide Thru
       Spin the Top
       Turn Thru
       Walk and Dodge

     Plus
       Chase Right
       Crossfire
       Cut/Flip the Diamond
       Diamond Circulate
       Explode the Wave
       Fan the Top
       Follow Your Neighbor
       Linear Cycle
       Peel Off
       Peel the Top
       Single Circle to a Wave
       Trade the Wave


 */

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.FourDancerConcept

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

  override fun computeLocation(d: Dancer,
                               m: Movement, beat: Double, groupIndex: Int): Vector {
    //  Extend the dancer's current position by 2 units
    val loc = d.location
    val factor = (loc.length + 2.0) / loc.length
    val v =  d.location * factor
    //System.log("$sd ${beat.s} $loc $v ${v.ds(sd)}")
    return v
  }

}