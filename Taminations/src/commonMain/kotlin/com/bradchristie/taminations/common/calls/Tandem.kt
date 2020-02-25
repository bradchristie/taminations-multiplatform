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
import kotlin.math.PI

class Tandem(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")

  //  Compute location for a dancer of the couple at a specific beat
  //  given location of the single dancer
  private fun computeLocation(m: Movement, beat:Double, offset:Double, isLeader:Boolean) : Vector {
    val pos = m.translate(beat).location
    val ang = m.rotate(beat).angle
    val v = Vector(offset,0.0).rotate(ang).rotate(if (isLeader) 0.0 else PI)
    return pos + v
  }

  //  Return offset of one of the original dancers of the tandem
  //  given location of the single dancer
  //  Only used for the very start and very end of the call
  private fun tandemDancerOffset(d: Dancer, isLeader:Boolean, dist:Double) : Vector {
    //  If on axis then each dancer is offset equally from the single dancer
    if (d.isOnXAxis || d.isOnYAxis) {
      val offset = dist
      return Vector(offset,0.0).rotate(d.angleFacing).rotate(if (isLeader) 0.0 else PI)
    } else {
      //  Not on axis - inside dancer is at same position as single dancer,
      //  outside dancer is 2 units away
      val offset = 2.0
      val v = Vector(offset,0.0).rotate(d.angleFacing).rotate(if (isLeader) 0.0 else PI)
      return if ((d.location + v).length > d.location.length+0.5) v else Vector()
    }
  }

  override fun perform(ctx: CallContext, i: Int) {
    //  Build a new context with one dancer from each couple
    //  Start with the leader of each tandem
    val dancers = ctx.dancers.filter { d -> d.data.leader }.map { d ->
      val d2 = ctx.dancerInBack(d)!!
      //  Select the gender for the single dancer
      val g = when {
        d.gender == Gender.BOY && d2.gender == Gender.BOY -> Gender.BOY
        d.gender == Gender.GIRL && d2.gender == Gender.GIRL -> Gender.GIRL
        else -> Gender.NONE
      }
      //  Select the couple number for the single dancer
      //  Needed for e.g. Tandem Heads Run
      val nc = when {
        (d.number_couple + d2.number_couple).matches("[13]{2}".r) -> "1"
        (d.number_couple + d2.number_couple).matches("[24]{2}".r) -> "2"
        else -> "0"
      }
      //  Create the single dancer
      val dsingle = Dancer(d, gender = g, number_couple = nc)
      //  Set the location of the single dancer
      val newpos =
          //  If tandem is straddling an axis, put single dancer on axis
          if (d.location.length isAbout d2.location.length)
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
      dsingle.setStartPosition(newpos.x,newpos.y)
      dsingle
    }
    if (dancers.count() != ctx.dancers.count()/2)
      throw CallError("Unable to group all dancers in Tandems")
    val singlectx = CallContext(dancers.toTypedArray())

    //  Perform the Tandem call
    singlectx.applyCalls(name.replace("Tandem ",""))

    //  Get the paths and apply to the original dancers
    singlectx.dancers.forEach { sd ->
      val d1 = ctx.dancers.firstOrNull { it.number == sd.number && it.data.leader }
      val d2 = d1?.let { ctx.dancerInBack(it) }
      if (d1 != null && d2 != null) {  // should always be true
        //  Compute movement for each tandem dancer for each movement
        //  based on the single dancer
        var sdbeat = 0.0
        sd.path.movelist.forEachIndexed { i,m ->

          listOf(true,false).forEach { isLeader ->
            //  Get the start and end offsets for the tandem dancer
            //  Start and end offsets for the very start and very end
            //  are returned by tandemDancerOffset()
            //  Other offsets are always 0.5 (dancers close together)
            singlectx.animate(sdbeat)
            val dist = d1.distanceTo(d2)/2.0
            val start = if (i == 0)
                            tandemDancerOffset(sd, isLeader, dist).length
                        else 0.5
            singlectx.animate(sdbeat + m.beats)
            val end = if (i == sd.path.movelist.count() - 1)
                          tandemDancerOffset(sd, isLeader, dist).length
                      else 0.5
            //  Get the 4 points needed to compute Bezier curve
            val cp1 = computeLocation(m, 0.0, start, isLeader)
            val cp2 = computeLocation(m, m.beats / 3.0, start * 2.0 / 3.0 + end / 3.0, isLeader) - cp1
            val cp3 = computeLocation(m, m.beats * 2.0 / 3.0, start / 3.0 + end * 2.0 / 3.0, isLeader) - cp1
            val cp4 = computeLocation(m, m.beats, end, isLeader) - cp1
            //  Now we can compute the Bezier
            val cb = Bezier.fromPoints(Vector(), cp2, cp3, cp4)
            //  And use it to build the Movement
            val cm = Movement(m.beats,m.hands,cb, m.brotate)
            //  And add the Movement to the Path
            if (isLeader)
              d1.path.add(cm)
            else
              d2.path.add(cm)
          }

          sdbeat += m.beats
        }
      }
    }

  }

}