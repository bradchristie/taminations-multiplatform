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

class CastBack(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {
    //  Either the leaders Cast Back or the caller has
    //  to specify the dancers
    val leaders = ctx.dancers.filter { it.data.leader }
    val casters = if (ctx.actives.count() < ctx.dancers.count())
      ctx.actives
    else if (leaders.count() > 0 && leaders.count() < ctx.dancers.count())
      leaders
    else
      throw CallError("Who is going to Cast Back?")
    ctx.dancers.forEach { it.data.active = it in casters }
    super.perform(ctx, i)
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val dir = if (d.isCenterRight xor (norm == "crosscastback")) "Left" else "Right"
    val move = if (d.isCenterRight && (norm == "crosscastback")) "Flip" else "Run"
    val scale = if (norm == "crosscastback") 2.0 else 1.0
    return TamUtils.getMove("$move $dir").scale(1.0,scale).skew(-2.0,0.0)
  }

}