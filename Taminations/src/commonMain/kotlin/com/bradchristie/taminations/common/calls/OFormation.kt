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
import kotlin.math.max

class OFormation(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {

    //  Find outer and inner dancers, and confirm we have on "O"
    val outer = ctx.dancers.filter { d ->
      (ctx.dancerInFront(d)?.distanceTo(d) ?: 0.0).isAbout(6.0) ||
      (ctx.dancerInBack(d)?.distanceTo(d) ?: 0.0).isAbout(6.0) }
    val inner = ctx.dancers.filter { d ->
      (ctx.dancerToLeft(d)?.distanceTo(d) ?: 0.0).isAbout(6.0) ||
      (ctx.dancerToRight(d)?.distanceTo(d) ?: 0.0).isAbout(6.0) }
    if (outer.count() != 4 || inner.count() != 4)
      throw CallError("Formation is not an O")

    //  Slide in the inner dancers
    outer.forEach { d -> d.data.active = false }
    ctx.applyCalls("Slide In")
    outer.forEach { d -> d.data.active = true }

    //  Do the call
    ctx.applyCalls(name.toLowerCase().replaceFirst("o",""))

    //  Merge the slide in adjustment into the start of the call
    inner.forEach { d ->
      if (d.path.movelist.count() > 1) {
        val dy = d.path.movelist.first().y2
        d.path.shift()
        d.path.skewFirst(0.0,dy)
      }
    }
    //  Outer 4 no longer need to stand for inner 4 to adjust
    outer.forEach { d -> d.path.shift() }

    //  Reform the O
    //  First find the major axis
    ctx.analyze()
    var (xmax,ymax) = listOf(0.0,0.0)
    ctx.dancers.forEach { d ->
      xmax = max(xmax,d.location.x)
      ymax = max(ymax,d.location.y)
    }
    //  Now move the centers in the other direction
    //  and adjust the outer dancers
    val newcenters = ctx.dancers.filter { it.data.center }
    if (newcenters.count() != 4)
      throw CallError("Unable to reform the O")
    ctx.dancers.forEach { d ->
      var (dx, dy) = listOf(0.0, 0.0)
      //  Centers are 6 units apart, ends 2 units apart
      val goal = if (d.data.center) 6.0 else 2.0
      if (d.angleFacing.abs.isAround(PI / 2) xor (ymax > xmax)) {
        //  Moving forward or back
        if (d.isFacingIn) {
          val d2 = ctx.dancerInFront(d)
            ?: throw CallError("Unable to reform the O")
          dx = -(goal - d.distanceTo(d2)) / 2.0
        }
        if (d.isFacingOut) {
          val d2 = ctx.dancerInBack(d)
            ?: throw CallError("Unable to reform the O")
          dx = (goal - d.distanceTo(d2)) / 2.0
        }
      } else {
        //  Moving left or right
        if (d.isCenterRight) {
          val d2 = ctx.dancerToRight(d)
            ?: throw CallError("Unable to reform the O")
          dy = (goal - d.distanceTo(d2)) / 2.0
        }
        if (d.isCenterLeft) {
          val d2 = ctx.dancerToLeft(d)
            ?: throw CallError("Unable to reform the O")
          dy = -(goal - d.distanceTo(d2)) / 2.0
        }
      }
      d.path.skewFromEnd(dx, dy)
    }

  }

}