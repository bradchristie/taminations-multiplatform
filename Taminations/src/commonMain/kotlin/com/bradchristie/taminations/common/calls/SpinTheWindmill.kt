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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.r
import com.bradchristie.taminations.platform.System

class SpinTheWindmill(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject.find("a2")
  override val requires = listOf("b2/trade","a2/slip","b1/face",
      "ms/cast_off_three_quarters","a2/spin_the_windmill")

  override fun perform(ctx: CallContext, i: Int) {
    var prefix = ""
    //  Get the center 4 dancers
    //  Note that if tidal it's not the same as the "centers"
    val centers = ctx.center(4)
    //  Step to a wave if facing couples
    val ctxCenters = CallContext(ctx, centers)
    val wave = if (norm.startsWith("left")) "Left-Hand Wave" else "Wave"
    if (ctxCenters.dancers.all { d ->
          ctxCenters.isInCouple(d)
        })
      prefix = "Step to a $wave"
    //  Then Swing, Slip, Cast
    val centerPart = "Center 4 $prefix Trade Slip Cast Off 3/4"

    val outerPart = "Outer 4 Windmillx "+norm.replace(".*windmill".r,"")

    ctx.applyCalls("$outerPart while $centerPart")
  }

}

class Windmillx(norm:String, name:String) : Action(norm,name) {

  override fun perform(ctx: CallContext, i: Int) {
    //  Get the direction
    val dir = norm.replace("windmillx","")
    //  Face that way and do two circulates
    ctx.applyCalls("Face $dir","Circulate","Circulate")
  }

}