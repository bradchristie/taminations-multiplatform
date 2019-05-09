package com.bradchristie.taminations.common.calls

import com.bradchristie.taminations.common.*

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

class CrossOverCirculate : Action("Cross Over Circulate") {

  override val level = LevelObject("a1")

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() != 4)
      throw CallError("No animation for Cross Over Circulate from this formation")
    super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    when {
      d.data.leader -> {
        //  Find another active dancer in this line
        val d2 = ctx.actives.firstOrNull { dd ->
          dd.isRightOf(d) || dd.isLeftOf(d)
        } ?: throw CallError("Unable to calculate Cross Over Circulate for dancer $d")
        val move = if (d2.isRightOf(d)) "Run Right" else "Run Left"
        //  Pass right shoulders if necessary
        val xScale = if (d2.isRightOf(d) && d2.data.leader) 2.0 else 1.0
        val yScale = d.distanceTo(d2) / 2.0
        return TamUtils.getMove(move).scale(xScale,yScale)
      }
      d.data.trailer -> {
        //  Find the dancer in the other line to move to
        val d2 = ctx.actives.firstOrNull { dd ->
          dd != d && !dd.isOpposite(d) && !dd.isLeftOf(d) && !dd.isRightOf(d)
        } ?: throw CallError("Unable to calculate Cross Over Circulate for dancer $d")
        val v = d.vectorToDancer(d2)
        //  Pass right shoulders if necessary
        return when {
          d2.data.trailer && v.y > 0 ->
            TamUtils.getMove("Extend Left").changebeats(v.x-1).scale(v.x-1,v.y) +
            TamUtils.getMove("Forward")
          d2.data.trailer && v.y < 0 ->
            TamUtils.getMove("Forward") +
            TamUtils.getMove("Extend Right").scale(v.x-1,-v.y).changebeats(v.x-1)
          v.y > 0 ->
            TamUtils.getMove("Extend Left").changebeats(v.x).scale(v.x,v.y)
          else ->
            TamUtils.getMove("Extend Right").changebeats(v.x).scale(v.x,-v.y)
        }
      }
      else ->
        throw CallError("Unable to calculate Cross Over Circulate for dancer $d")
    }
  }

}