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

class SiameseConcept(callnorm:String,callname:String) : FourDancerConcept(callnorm,callname) {

  override val level = LevelObject("c1")
  override val conceptName = "Siamese"

  lateinit var couples: List<List<Dancer>>
  lateinit var tandems: List<List<Dancer>>

  override fun dancerGroups(ctx: CallContext): List<List<Dancer>> {
    //  First find couples
    couples = ctx.dancers.filter { d ->
      d.data.beau && (d.data.partner?.data?.belle ?: false) }
        .map { d -> listOf(d, d.data.partner!!) }
    //  Remaining dancers are tandems
    tandems = ctx.dancers.filter { d ->
      val d2 = ctx.dancerInBack(d)
      d2!=null && couples.flatten().none { it in listOf(d,d2) } }
        .map { d -> listOf(d,ctx.dancerInBack(d)!!) }
    //  Better be all the dancers
    if ((couples+tandems).flatten().count() == ctx.dancers.count() )
      return couples + tandems
    else
      throw CallError("Unable to find all Siamese pairs")
  }

  override fun startPosition(group: List<Dancer>): Vector {
    val (d,d2) = group
    return (d.location + d2.location).scale(0.5, 0.5)
  }

  override fun computeLocation(d: Dancer, m: Movement, beat: Double, groupIndex: Int): Vector {
    val pos = m.translate(beat).location
    val offset = 0.5
    val isFirst = groupIndex == 0
    val isCouple = d in couples.flatten()
    val ang = m.rotate(beat).angle
    val ang2 = when {
      isCouple && isFirst -> PI/2.0
      isCouple -> -PI/2.0
      isFirst -> 0.0
      else -> PI
    }
    val v = Vector(offset,0.0).rotate(ang).rotate(ang2)
    return pos + v
  }

  override fun postAdjustment(ctx: CallContext, cd: Dancer, group: List<Dancer>) {
    if (group in tandems) {
      //  If there is space, spread out the tandem a bit
      val (leader,trailer) = group
      if ((ctx.dancerInFront(leader)?.distanceTo(leader) ?: 2.0) > 1.0 &&
          (ctx.dancerInBack(trailer)?.distanceTo(trailer) ?: 2.0) > 1.0) {
        leader.path.skewFromEnd(0.5, 0.0)
        trailer.path.skewFromEnd(-0.5, 0.0)
      }
    } else {  //  Couples
      group.zip(listOf(Hands.GRIPRIGHT,Hands.GRIPLEFT)).forEach { (d,h) ->
        d.path.addhands(h)
      }
    }
  }

}