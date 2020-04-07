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
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sign

class ConcentricConcept(callnorm:String,callname:String) : FourDancerConcept(callnorm,callname) {

  override val conceptName = "Concentric"
  override val level = LevelObject("c1")
  private val dancerLocations:HashMap<String,MutableList<Vector>> = hashMapOf()
  private val dancerShifts:HashMap<String,MutableList<Vector>> = hashMapOf()
  private var mindist = 10.0

  override fun dancerGroups(ctx: CallContext): List<List<Dancer>> {
    return ctx.actives.map { d -> listOf(d) }
  }

  //  Compute location of "normal" dancer from position of
  //  outer concentric dancer.
  //  This just shifts it in 2 units along the long axis
  override fun startPosition(group: List<Dancer>): Vector {
    val loc = group.first().location
    val shift = min(2.0,mindist-0.5)
    return if (loc.x.abs > loc.y.abs)
      Vector(loc.x - (shift*loc.x.sign), loc.y)
    else
      Vector(loc.x, loc.y - (shift*loc.y.sign))
  }

  override fun analyzeConceptResult(conceptctx: CallContext, realctx:CallContext) {
    //  Look at each movement to figure out
    //  the end point of the real dancer.
    conceptctx.dancers.zip(realctx.dancers).forEach { (cd,d) ->
      var cdbeat = 0.0
      val dloc = d.starttx
      //System.log("$d dloc: ${dloc.location}  cdloc: ${cd.location}")
      val dlocList:MutableList<Vector> = mutableListOf()
      val shiftList:MutableList<Vector> = mutableListOf()
      dancerLocations[cd.number] = dlocList
      dancerShifts[cd.number] = shiftList
      dlocList.add(d.location - cd.location)
      cd.path.movelist.forEachIndexed { i,m ->
        val isLast = i == cd.path.movelist.count()-1

        //  Does the movement cross an axis?
        //  If so, remember so we can shift the dancer away from that axis
        var (xshift,yshift) = listOf(0.0,0.0)
        cd.animate(cdbeat)
        val cdloc1 = cd.location
        cd.animate(cdbeat+m.beats)
        val cdloc2 = cd.location
        if (!cdloc1.x.isAbout(0.0) && !cdloc2.x.isAbout(0.0) &&
            cdloc1.x.sign != cdloc2.x.sign)
          //  Crosses Y axis
          yshift = 1.5
        if (!cdloc1.y.isAbout(0.0) && !cdloc2.y.isAbout(0.0) &&
            cdloc1.y.sign != cdloc2.y.sign)
          //  Crosses X axis
          xshift = 1.5
        if (xshift != 0.0 || yshift != 0.0) {
          cd.animate(cdbeat + m.beats / 2.0)
          //  If it's already some distance out on the axis don't need to shift
          if (cd.location.length.isLessThan(1.5)) {
            xshift *= cd.location.x.sign
            yshift *= cd.location.y.sign
          } else {
            xshift = 0.0
            yshift = 0.0
          }
          cd.animate(cdbeat + m.beats)
        }
        shiftList.add(Vector(xshift,yshift))

        //System.log("    cd location: ${cd.location}")

        //  If it ends an axis, then real dancer is on same axis further out
        var (dx,dy) = listOf(0.0,0.0)
        if (cd.isOnXAxis) {
          dx = 2.0
        } else if (cd.isOnYAxis) {
          dy = 2.0
        } else if (!isLast) {

          //  For 2x2 not at end of call, just shift out both directions
          dx = 2.0
          dy = 2.0
        } else {

          //System.log("    Trying rule 2")
          //  Not on an axis (ends in a 2x2) - should we move out X or Y?
          //  If started on an axis (i.e., not a 2x2),
          //  then shift out on the other axis
          if (dloc.location.x.isAbout(0.0))
            dx = 2.0
          else if (dloc.location.y.isAbout(0.0))
            dy = 2.0
          else {

            //System.log("    Trying rule 3")
            //  Starts and ends in a 2x2
            //  The rule is "Lines to Lines, Columns to Columns"
            //  Has the dancer's facing direction changed 90 degrees?
            val a1 = dloc.angle
            val a2 = cd.angleFacing
            val is90 = a1.angleDiff(a2).abs.isAround(PI/2.0)
            if ((dloc.location.x.abs > dloc.location.y.abs) xor is90)
              dx = 2.0
            else
              dy = 2.0
          }
        }
        dx *= cd.location.x.sign
        dy *= cd.location.y.sign
        //System.log("    shift: ${dx.s}  ${dy.s}")
        //  Remember the shift, will be used in computeLocation below
        dlocList.add(Vector(dx,dy))


        cdbeat += m.beats
      }
    }
  }

  override fun computeLocation(d: Dancer,
                               m: Movement, mi:Int,
                               beat: Double, groupIndex: Int): Vector {

    //  Get the offset vectors for the start and end of this movement
    //System.log("$d  $mi  ${beat.s}")
    val v1 = dancerLocations[d.number]!![mi]
    val v2 = dancerLocations[d.number]!![mi+1]
    //System.log("    v1: $v1  v2: $v2")
    //  Convert to dancer space
    val v1d = v1.rotate(-d.angleFacing)
    val v2d = v2.rotate(-d.angleFacing)
    //System.log("    v1d: $v1  v2d: $v2")
    //  Compute interpolation fraction
    val f = beat / m.beats
    //  Interpolate each offset to get dancer's current offset
    //  Hack for 1st movement, parent has adjusted according to startPosition
    val vnow = (v2d - v1d) * f + (if (mi==0) v1d else Vector())
    val pos = m.translate(beat).location
    val shift = if (f.isGreaterThan(0.0) && f.isLessThan(1.0))
      dancerShifts[d.number]!![mi].rotate(-d.angleFacing) else Vector()
    //System.log("    pos: $pos    vnow: $vnow   shift: $shift")
    //  And add it to the concept dancer location
    return pos + vnow + shift
  }

  override fun perform(ctx: CallContext, i: Int) {
    when {
      ctx.actives.count() == 8 -> ctx.applyCalls("Center 4 $realCall While Outer 4 $name")
      ctx.dancers.count() == 8 -> CallContext(ctx,ctx.actives).applyCalls(name).appendToSource()
      else -> {
        mindist = ctx.dancers.fold(10.0) { x,d -> min(x,d.location.length) }
        super.perform(ctx, i)
      }
    }
  }

}