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

class AsCouples(norm:String,name:String) : Action(norm,name)  {

  override val level = LevelObject("a1")

  //  If the 4-dancer formation is a compact wave or line there isn't
  //  enough room to fit in the corresponding 8-dancer formation
  //  In that case the dancers need to spread out more
  private fun compactWaveCorrection(ctx:CallContext, d:Dancer, isBeau:Boolean) : Double {
    if (d.isTidal && d.location.length isAbout 1.5) {
      val d2 = ctx.dancerToLeft(d) ?: ctx.dancerToRight(d)
      if (d2 != null && d2.location.length isAbout 0.5) {
        return if ((d.angleToOrigin > 0) xor isBeau) 1.5 else -1.5
      }
    }
    else if (d.isTidal && d.location.length isAbout 0.5) {
      if (ctx.dancerToLeft(d)?.location?.length?.isApprox(1.5) == true ||
          ctx.dancerToRight(d)?.location?.length?.isApprox(1.5) == true)
        return if ((d.angleToOrigin > 0) xor isBeau) 0.5 else -0.5
    }
    return 0.0
  }

  //  Compute location for a dancer of the couple at a specific beat
  //  given location of the single dancer
  private fun computeLocation(m:Movement, beat:Double, offset:Double,isBeau:Boolean) : Vector {
    val pos = m.translate(beat).location
    val ang = m.rotate(beat).angle
    val v = Vector(offset,0.0).rotate(ang).rotate(if (isBeau) PI/2.0 else -PI/2.0)
    return pos + v
  }

  //  Return offset of one of the original dancers of the couple
  //  given location of the single dancer
  //  Only used for the very start and very end of the call
  private fun coupleDancerOffset(d:Dancer,isBeau:Boolean) : Vector {

    //  If on axis then each dancer is offset equally from the single dancer
    if (d.isOnXAxis || d.isOnYAxis) {
      val offset = if (d.isTidal) 0.5 else 1.0
      return Vector(offset,0.0).rotate(d.angleFacing).rotate(if (isBeau) PI/2.0 else -PI/2.0)

      //  Not on axis - inside dancer is at same position as single dancer,
      //  outside dancer is 2 units away
    } else {
      val offset = 2.0
      val v = Vector(offset,0.0).rotate(d.angleFacing).rotate(if (isBeau) PI/2.0 else -PI/2.0)
      return if ((d.location + v).length > d.location.length+0.5) v else Vector()
    }
  }

  override fun perform(ctx: CallContext, i: Int) {
    //  Build a new context with one dancer from each couple
    //  Start with the beau of each couple
    val dancers = ctx.dancers.filter { d ->
      val d2 = d.data.partner ?:
        throw CallError("No partner for $d")
      if (!ctx.isInCouple(d,d2))
        throw CallError("$d and $d2 are not a Couple")
      d.data.beau
    }.map { d ->
      val d2 = d.data.partner!!
      //  Select the gender for the single dancer
      val g = when {
        d.gender == Gender.BOY && d2.gender == Gender.BOY -> Gender.BOY
        d.gender == Gender.GIRL && d2.gender == Gender.GIRL -> Gender.GIRL
        else -> Gender.NONE
      }
      //  Select the couple number for the single dancer
      //  Needed for e.g. As Couples Heads Run
      val nc = when {
        (d.number_couple + d2.number_couple).matches("[13]{2}".r) -> "1"
        (d.number_couple + d2.number_couple).matches("[24]{2}".r) -> "2"
        else -> "0"
      }
      //  Create the single dancer
      val dsingle = Dancer(d, gender = g, number_couple = nc)
      //  Set the location of the single dancer
      val newpos =
          //  If couple is straddling an axis, put single dancer on axis
          if (d.location.length isAbout d2.location.length)
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
      dsingle.setStartPosition(newpos.x, newpos.y)
      dsingle
    }
    val singlectx = CallContext(dancers.toTypedArray())

    //  Perform the As Couples call
    singlectx.applyCalls(name.replace("As Couples ",""))

    //  Get the paths and apply to the original dancers
    singlectx.dancers.forEach { sd ->
      val d1 = ctx.dancers.firstOrNull { it.number == sd.number && it.data.beau }
      val d2 = d1?.data?.partner
      if (d1 != null && d2 != null) {  // should always be true
        //  Compute movement for each couple dancer for each movement
        //  based on the single dancer
        var sdbeat = 0.0
        sd.path.movelist.forEachIndexed { i,m ->

          listOf(true,false).forEach { isBeau ->
            //  Get the start and end offsets for the couple dancer
            //  Start and end offsets for the very start and very end
            //  are returned by coupleDancerOffset()
            //  Other offsets are always 0.5 (dancers close together)
            //  with additional correction as needed for compact line/wave formation
            singlectx.animate(sdbeat)
            val start = if (i == 0) coupleDancerOffset(sd, isBeau).length
                        else 0.5 + compactWaveCorrection(singlectx, sd, isBeau)
            singlectx.animate(sdbeat + m.beats)
            val end = if (i == sd.path.movelist.count() - 1)
              coupleDancerOffset(sd, isBeau).length
                      else 0.5 + compactWaveCorrection(singlectx, sd, isBeau)
            //  Get the 4 points needed to compute Bezier curve
            val cp1 = computeLocation(m, 0.0, start, isBeau)
            val cp2 = computeLocation(m, m.beats / 3.0, start * 2.0 / 3.0 + end / 3.0, isBeau) - cp1
            val cp3 = computeLocation(m, m.beats * 2.0 / 3.0, start / 3.0 + end * 2.0 / 3.0, isBeau) - cp1
            val cp4 = computeLocation(m, m.beats, end, isBeau) - cp1
            //  Now we can compute the Bezier
            val cb = Bezier.fromPoints(Vector(), cp2, cp3, cp4)
            //  And use it to build the Movement
            val cm = Movement(m.beats,
                m.hands or ( if (isBeau) Hands.RIGHTHAND else Hands.LEFTHAND),
                cb, m.brotate)
            //  And add the Movement to the Path
            if (isBeau)
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