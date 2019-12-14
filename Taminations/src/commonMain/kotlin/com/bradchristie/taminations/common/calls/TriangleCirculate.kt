package com.bradchristie.taminations.common.calls

/*

  Taminations Square Dance Animations
  Copyright (C) 2019 Brad Christie

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

class TriangleCirculate(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject("c1")

  //  Calculate circulate path to next triangle dancer
  private fun oneCirculatePath(d:Dancer, d2:Dancer) : Path {
    return when {
      d2.isInFrontOf(d) -> {
        //  Path is forward
        val dist = d.distanceTo(d2)
        TamUtils.getMove("Forward").scale(dist,1.0).changebeats(dist)
      }
      d2.angleFacing.isAround(d.angleFacing+PI) -> {
        //  Path is 180 degree turn to left or right
        val d2v = d.vectorToDancer(d2)
        TamUtils.getMove("Run Left")
            .scale(0.5,d2v.y/2)
            .skew(d2v.x,0.0)
      }
      else -> {
        //  Path is 90 degree turn to left or right
        val d2v = d.vectorToDancer(d2)
        TamUtils.getMove("Lead Left")
            .scale(d2v.x,d2v.y).changebeats(d2v.length)
      }
    }
  }

  override fun perform(ctx: CallContext, i: Int) {
    //  Find the 6 dancers to circulate
    val triangleType = norm.replace("trianglecirculate","")
    val points = ctx.points()
    when (triangleType) {
      "inside" -> ctx.outer(2).forEach { it.data.active = false }
      "outside" -> ctx.center(2).forEach { it.data.active = false }
      "inpoint" -> points.forEach {
        if (it.data.leader)
          it.data.active = false
      }
      "outpoint" -> points.forEach {
        if (it.data.trailer)
          it.data.active = false
      }
      "tandembased" -> ctx.dancers.forEach {
        //  Dancer must either be in a tandem ..
        if (!ctx.isInTandem(it)) {
          //  .. or two nearby dancers must form a tandem
          val others = ctx.dancersInOrder(it) { d2 -> ctx.isInTandem(d2) }
          if (!others[0].isInFrontOf(others[1]) && !others[1].isInFrontOf(others[0]))
            it.data.active = false
        }
      }
      //  If no type given, assume a wave-based (maybe sausage?)
      "wavebased", "" -> {
        if (points.count() > 0) {
          points.forEach {
            val others = ctx.dancersInOrder(it)
            if (!(others[0].isLeftOf(others[1]) || others[0].isRightOf(others[1])) ||
                !(others[1].isLeftOf(others[0]) || others[1].isRightOf(others[0]))
            )
              it.data.active = false
          }
        } else {
          //  No points, maybe a sausage
          val sausage = CallContext(TamUtils.getFormation("Sausage RH"))
          if (ctx.matchFormations(sausage,rotate = true) != null) {
            ctx.center(2).forEach { d -> d.data.active = false }
          }
        }
      }
    }
    if (ctx.actives.count() != 6)
      throw CallError("Unable to find dancers to circulate")
    //  Should be able to split the square to 2 3-dancer triangles
    val triangles = when {
      ctx.actives.none { it.location.x.isApprox(0.0) } ->
        ctx.actives.partition { d -> d.location.x < 0 }
      ctx.actives.none { it.location.y.isApprox(0.0) } ->
        ctx.actives.partition { d -> d.location.y < 0 }
      else ->
        throw CallError("Unable to find Triangles")
    }
    //  Figure out the circulates for each triangle
    triangles.toList().forEach { triangle ->
      triangle.forEach { d ->
        val d2 =
            //  Scan ever-widening angles for other dancer
            //  of this triangle to circulate to
            triangle.firstOrNull { d2 -> d2.isInFrontOf(d) } ?:
            triangle.firstOrNull { d2 -> d2 != d
              && d.angleToDancer(d2).abs < PI / 2.0
              && !d.angleToDancer(d2).abs.isAround(PI / 2) } ?:
            triangle.firstOrNull { d2 -> d2 != d
              && d.angleToDancer(d2).abs.isAround(PI / 2) } ?:
            triangle.firstOrNull { d2 -> d2 != d
                && !d.angleToDancer(d2).abs.isAround(PI) }
        if (d2 != null) {
          d.path += oneCirculatePath(d,d2)
        } else {
          throw CallError("Unable to calculate circulate path for $d")
        }
      }
    }

  }

}
