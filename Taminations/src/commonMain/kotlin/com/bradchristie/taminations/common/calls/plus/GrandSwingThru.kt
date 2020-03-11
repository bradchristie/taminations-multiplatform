package com.bradchristie.taminations.common.calls.plus

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action

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


//  Tidal waves of 8 dancers are covered by xml animations.
//  This class handles formations of 6 dancers, with 2 others inactive.
class GrandSwingThru(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject.find("plus")

  override fun perform(ctx: CallContext, i: Int) {
    //  Check that we have 6 dancers in a tidal wave
    var count = 0
    ctx.actives.forEach { d ->
      if (ctx.dancersToLeft(d).count() + ctx.dancersToRight(d).count() == 5) {
        count += 1
        listOfNotNull(ctx.dancerToLeft(d), ctx.dancerToRight(d)).forEach { d2 ->
              if (!ctx.isInWave(d, d2))
                throw CallError("Dancers are not in a tidal wave.")
            }
      }
      else
        d.data.active = false
    }

    //  Ok, do each part
    if (norm.contains("left"))
      ctx.applyCalls("_Grand Swing Left","_Grand Swing Right")
    else
      ctx.applyCalls("_Grand Swing Right","_Grand Swing Left")
  }

}

class GrandSwingX(norm:String,name:String) : Action(norm,name) {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val (d2q,dir) = if (norm.endsWith("right"))
      Pair(ctx.dancerToRight(d),"Right")
    else
      Pair(ctx.dancerToLeft(d),"Left")
    d2q?.let { d2 ->
      //  Distance is likely 1.0 (shoulder to shoulder)
      val dist = d.distanceTo(d2)
      return TamUtils.getMove("Swing $dir").scale(dist/2.0,dist/2.0)
    }
    //  d2 is null - must be dancer at end not moving for this part
    return TamUtils.getMove("Stand")
  }

}