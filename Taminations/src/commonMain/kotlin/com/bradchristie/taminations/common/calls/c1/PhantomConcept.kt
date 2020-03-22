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
import com.bradchristie.taminations.common.calls.Action
import kotlin.math.PI

class PhantomConcept(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject("c1")
  private val subcall = name.replace("Phantom ","",ignoreCase = true)

  private fun addPhantoms(ctx:CallContext) : CallContext {
    //  Add all the phantoms
    //  This assumes lines, will not work
    //  for phantom column formations
    val ang = ctx.dancers.first().angleFacing
    val positions = if (ang isAround 0.0 || ang isAround PI) {
      //  Lines are parallel to Y-axis
      //  Vectors must be pairs of diagonal opposites
      //  for XML mapping to work
      listOf(Vector(2,-3),Vector(-2,3),Vector(2,-1),Vector(-2,1),
          Vector(-2,-3),Vector(2,3),Vector(-2,-1),Vector(2,1))
    } else {
      //  Lines are parallel to X-axis
      listOf(Vector(-3,2),Vector(3,-2),Vector(-1,2),Vector(1,-2),
          Vector(-3,-2),Vector(3,2),Vector(-1,-2),Vector(1,2))
    }
    //  Positions for phantoms are the ones not
    //  occupied by real dancers
    val phantomPositions = positions.filter { v ->
      ctx.dancers.none { d -> d.location.isApprox(v) }
    }
    //  Put phantoms in those positions
    val phantoms = phantomPositions.mapIndexed { i,v ->
      Dancer(ctx.dancers.first(),
              gender = Gender.PHANTOM,
              number = "P${i+1}")
          .setStartPosition(v).rotateStartAngle((i % 2) * 180.0)
    }
    //  And merge with real dancers in a new context
    val phantomctx = CallContext(ctx,ctx.dancers+phantoms)
    //  Find good rotation
    phantomctx.analyze()
    if (!phantomctx.rotatePhantoms(subcall))
      throw CallError("Unable to find phantom formation for $subcall")
    return phantomctx
  }

  override fun perform(ctx: CallContext, i: Int) {
    //  Split the dancers into two groups
    //  by the axis the dancers are facing
    ctx.dancers.partition {
      it.angleFacing isAround 0.0 || it.angleFacing isAround PI
    }.toList().forEach { group ->
      //  Make a call context for this group
      val groupctx = CallContext(ctx,group)
      //  Add phantoms to make 8 dancers
      val phantomctx = addPhantoms(groupctx)
      //  Perform 8-dancer call
      phantomctx.applyCalls(subcall)
      //  Append the results
      group.zip(phantomctx.dancers.filter
          { it.gender != Gender.PHANTOM } ).forEach { (d1,d2) ->
        d1.path + d2.path
      }
    }

  }

}