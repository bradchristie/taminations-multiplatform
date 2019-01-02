package com.bradchristie.taminations.common
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

import kotlin.math.PI
import kotlin.math.atan2

class Bezier(private val x1:Double, private val y1:Double,
             ctrlx1:Double, ctrly1:Double,
             ctrlx2:Double, ctrly2:Double,
             x2:Double, y2:Double) {

  private val cx = 3.0*(ctrlx1-x1)
  private val bx = 3.0*(ctrlx2-ctrlx1) - cx
  private val ax = x2 - x1 - cx - bx

  private val cy = 3.0*(ctrly1-y1)
  private val by = 3.0*(ctrly2-ctrly1) - cy
  private val ay = y2 - y1 - cy - by

  //  Compute X, Y values for a specific t value
  private fun xt(t:Double):Double = x1 + t*(cx + t*(bx + t*ax))
  private fun yt(t:Double):Double = y1 + t*(cy + t*(by + t*ay))
  //  Compute dx, dy values for a specific t value
  private fun dxt(t:Double):Double = cx + t*(2.0*bx + t*3.0*ax)
  private fun dyt(t:Double):Double = cy + t*(2.0*by + t*3.0*ay)
  private fun angle(t:Double):Double = atan2(dyt(t),dxt(t))

  //  Return the movement along the curve given "t" between 0 and 1
  fun translate(t:Double): Matrix {
    val x = xt(t)
    val y = yt(t)
    return Matrix().postTranslate(x,y)
  }

  fun rotate(t:Double): Matrix {
    val theta = angle(t)
    return Matrix().postRotate(theta)
  }

  //  Return turn direction at end of curve
  fun rolling():Double {
    //  Check angle at end
    var theta = angle(1.0)
    //  If it's 180 then use angle at halfway point
    if (theta.angleEquals(PI))
      theta = this.angle(0.5)
    //  If angle is 0 then no turn
    return if (theta.angleEquals(0.0))
      0.0
    else
      theta
  }

}