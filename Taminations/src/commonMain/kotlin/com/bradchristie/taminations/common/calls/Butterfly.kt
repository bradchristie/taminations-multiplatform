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

class Butterfly(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {

    //  Find the outside dancers, and confirm we have a butterfly
    val outer = ctx.dancers.filter { d ->
      d.location.x.abs.isAbout(3.0) && d.location.y.abs.isAbout(3.0)
    }
    val inner = ctx.dancers.filter { d ->
      d.location.x.abs.isAbout(1.0) && d.location.y.abs.isAbout(1.0)
    }
    if (outer.count() != 4 || inner.count() != 4)
      throw CallError("Not a Butterfly")

    //  Slide in the outer 4 dancers
    ctx.applyCalls("Outer 4 Slide In")
    //  Do the call
    ctx.applyCalls(name.toLowerCase().replace("butterfly",""))
    //  Merge the slide in adjustment into the start of the call
    outer.forEach { d ->
      if (d.path.movelist.count() > 1) {
        val dy = d.path.movelist.first().y2
        d.path.shift()
        d.path.skewFirst(0.0,dy)
      }
    }
    //  Center 4 no longer need to stand for outer 4 to adjust
    inner.forEach { d -> d.path.shift() }

    //  Reform the butterfly
    ctx.analyze()
    ctx.dancers.forEach { d ->
      var (dx,dy) = listOf(0.0,0.0)

      //  Only adjustment for centers is maybe move in a little closer
      if (d.data.center) {
        if (d.isFacingIn) {
          val d2 = ctx.dancerInFront(d)
            ?: throw CallError("Unable to reform the Butterfly")
          //  Both dancers will be adjusted, so only need to move halfway
          dx = (d.distanceTo(d2)-2.0)/2.0
        } else if (d.isFacingOut) {
          val d2 = ctx.dancerInBack(d)
            ?: throw CallError("Unable to reform the Butterfly")
          dx = -(d.distanceTo(d2)-2.0)/2.0
        }

      } else {
        //  Compute offset for the outer 4
        if (d.isFacingIn) {
          //  Could be line-like formation or column-like formation
          //  Either way, we want to find distance to dancer farthest away
          val d2 = ctx.dancersInFront(d).lastOrNull()
            ?: throw CallError("Unable to reform the Butterfly")
          dx += -(6.0-d.distanceTo(d2))/2.0
        }
        if (d.isFacingOut) {
          val d2 = ctx.dancersInBack(d).lastOrNull()
            ?: throw CallError("Unable to reform the Butterfly")
          dx += (6.0-d.distanceTo(d2))/2.0
        }
        if (d.isCenterRight) {
          val d2 = ctx.dancersToRight(d).lastOrNull()
            ?: throw CallError("Unable to reform the Butterfly")
          dy += (6.0-d.distanceTo(d2))/2.0
        }
        if (d.isCenterLeft) {
          val d2 = ctx.dancersToLeft(d).lastOrNull()
            ?: throw CallError("Unable to reform the Butterfly")
          dy += -(6.0-d.distanceTo(d2))/2.0
        }
      }
      //  Rotate by the angle of the last movement
      d.path.skewFromEnd(dx,dy)
    }

  }

}