package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations
  Copyright (C) 2018 Brad Christie

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

class BoxCounterRotate : Action("Box Counter Rotate") {

  override val level = LevelObject("a2")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val v = d.location
    val v2: Vector
    val cy4:Double
    val y4:Double
    val a1 = d.tx.angle
    val a2 = v.angle
    //  Determine if this is a rotate left or right
    val angdif = a2.angleDiff(a1)
    if (angdif < 0) {
      //  Left
      v2 = v.rotate(PI/2)
      cy4 = 0.45
      y4 = 1.0
    }
    else {
      //  Right
      v2 = v.rotate(-PI/2)
      cy4 = -0.45
      y4 = -1.0
    }
    //  Compute the model points
    val dv = (v2 - v).rotate(-a1)
    val cv1 = (v2*.5).rotate(-a1)
    val cv2 = (v*.5).rotate(-a1) + dv
    val m = Movement(
        2.0, Hands.NOHANDS,
        cv1.x, cv1.y, cv2.x, cv2.y, dv.x, dv.y,
        0.55, 1.0, cy4, 1.0, y4)
    return Path(m)

  }
}