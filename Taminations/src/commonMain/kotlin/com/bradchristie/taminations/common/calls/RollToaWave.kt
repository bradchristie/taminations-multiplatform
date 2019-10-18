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

//  Class for leaders part of Roll to a Wave
class RollTo(norm: String, name: String) : Action(norm, name) {

  //  Turn tightly in the direction while moving a little back
  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val move = if (norm.startsWith("left")) "Flip Left" else "Flip Right"
    return TamUtils.getMove(move).scale(1.0,0.25).skew(-0.5,0.0)
  }

}

//  Left or Right Roll to a Wave
class RollToaWave(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject.find("a1")
  override val requires = listOf("b2/ocean_wave")

  override fun perform(ctx: CallContext, i: Int) {
    val dir = if (norm.startsWith("left")) "Left" else "Right"
    val wave = if (norm.startsWith("left")) "Left-Hand" else ""
    //  Have the leaders (if any) turn back the indicated direction
    //  Then everybody Step to a Wave
    val roller = "Leaders $dir RollTo"
    if (ctx.actives.any { it.data.leader })
      ctx.applyCalls(roller,"Step to a $wave Wave")
    else
      ctx.applyCalls("Step to a $wave Wave")

    //  Post-process - take out the filler for the trailers while
    //  leaders were turning back, then set all to 4 beats
    ctx.dancers.forEach { d ->
      //  a bit of a hack
      if (d.path.movelist.count() == 2 && !d.path.movelist.first().fromCall) {
        val m = d.path.pop()
        d.path.clear()
        d.path.add(m)
      }
      d.path.changebeats(4.0)
    }

  }


}