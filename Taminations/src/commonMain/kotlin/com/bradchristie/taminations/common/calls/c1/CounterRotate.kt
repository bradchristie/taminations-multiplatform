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
import kotlin.math.ceil

class CounterRotate : Action("Counter Rotate") {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {
    super.perform(ctx, i)
    //  Looks much better if dancers all take the same time
    val maxBeats = ctx.dancers.maxOf { it.path.beats }
    ctx.dancers.forEach {
      it.path.changebeats(maxBeats)
    }
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val da = d.angleToOrigin
    //  Counter Rotate not possible if dancer is looking
    //  directly at the center of the square
    if (da.isAround(0.0))
      throw CallError("Dancer $d cannot Counter Rotate")
    //  Compute points for Bezier
    val anginc = PI/6.0 * da.sign
    val p1 = d.location.ds(d)
    val p2 = d.location.rotate(anginc).ds(d)
    val p3 = d.location.rotate(anginc*2.0).ds(d)
    val p4 = d.location.rotate(anginc*3.0).ds(d)
    val bz = Bezier.fromPoints(p1,p2,p3,p4)
    //  Get turn, which is 1/4 right or left
    val turn = if (da < 0) "Right" else "Left"
    val brot = TamUtils.getMove("Quarter $turn").movelist[0].brotate
    val beats = ceil(d.location.length)
    val move = Movement(beats,Hands.NOHANDS,bz,brot)
    return Path(move)
  }

}