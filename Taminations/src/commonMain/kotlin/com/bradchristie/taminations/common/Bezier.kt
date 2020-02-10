package com.bradchristie.taminations.common
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

import kotlin.math.PI
import kotlin.math.atan2

class Bezier(private val x1:Double, private val y1:Double,
             private val ctrlx1:Double, private val ctrly1:Double,
             private val ctrlx2:Double, private val ctrly2:Double,
             private val x2:Double, private val y2:Double) {

  companion object {

    //  Constructor from 4 points along the curve
    //  at times 0, 1/3, 2/3, 1
    //  Reference:  https://web.archive.org/web/20131225210855/http://people.sc.fsu.edu/~jburkardt/html/bezier_interpolation.html
    fun fromPoints(p0:Vector, p1:Vector, p2:Vector,p3:Vector): Bezier {
      val pc1 = Vector(
          (-5.0*p0.x + 18.0*p1.x - 9.0*p2.x + 2.0*p3.x)/6.0,
          (-5.0*p0.y + 18.0*p1.y - 9.0*p2.y + 2.0*p3.y)/6.0
      )
      val pc2 = Vector(
          (2.0*p0.x - 9.0*p1.x + 18.0*p2.x - 5.0*p3.x)/6.0,
          (2.0*p0.y - 9.0*p1.y + 18.0*p2.y - 5.0*p3.y)/6.0
      )
      return Bezier(p0.x,p0.y,pc1.x,pc1.y,pc2.x,pc2.y,p3.x,p3.y)
    }

  }

  private val cx = 3.0*(ctrlx1-x1)
  private val bx = 3.0*(ctrlx2-ctrlx1) - cx
  private val ax = x2 - x1 - cx - bx

  private val cy = 3.0*(ctrly1-y1)
  private val by = 3.0*(ctrly2-ctrly1) - cy
  private val ay = y2 - y1 - cy - by

  val endPoint:Vector = Vector(x2,y2)

  //  Compute X, Y values for a specific t value
  private fun xt(t:Double):Double = x1 + t*(cx + t*(bx + t*ax))
  private fun yt(t:Double):Double = y1 + t*(cy + t*(by + t*ay))
  //  Compute dx, dy values for a specific t value
  private fun dxt(t:Double):Double = cx + t*(2.0*bx + t*3.0*ax)
  private fun dyt(t:Double):Double = cy + t*(2.0*by + t*3.0*ay)
  private fun angle(t:Double):Double = atan2(dyt(t),dxt(t))

  override fun toString(): String =
      "$($x1 $y1) ($ctrlx1 $ctrly1) ($ctrlx2 $ctrly2) ($x2 $y2)"

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

  //  Well, there could be control points way out but this is ok for our use
  fun isIdentity() : Boolean = x2 isAbout 0.0 && y2 isAbout 0.0

  ////  Functions to compute a new Bezier
  fun scale(x:Double, y:Double) : Bezier =
      Bezier(x1*x,y1*y,ctrlx1*x,ctrly1*y,ctrlx2*x,ctrly2*y,x2*x,y2*y)

  fun skew(x:Double, y:Double) : Bezier =
      Bezier(x1,y1,ctrlx1,ctrly1,ctrlx2+x,ctrly2+y,x2+x,y2+y)

}