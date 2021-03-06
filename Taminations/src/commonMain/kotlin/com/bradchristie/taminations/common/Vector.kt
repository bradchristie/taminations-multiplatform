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

import kotlin.math.*

fun angleAngleDiff(a1:Double,a2:Double): Double = ((a1-a2 + PI*3) % (PI*2)) - PI

data class Vector(val x:Double=0.0, val y:Double=0.0) {

  //  So we don't have to type decimal points all the time
  constructor(x:Int, y:Int) : this(x.d,y.d)
  //  Add another vector
  operator fun plus(v2: Vector): Vector = Vector(x + v2.x, y + v2.y)
  //  Subtract another vector
  operator fun minus(v2: Vector): Vector = Vector(x - v2.x, y - v2.y)
  operator fun unaryMinus(): Vector = Vector(-x, -y)
  //  Multiply by scale factors in one or more dimensions
  fun scale(sx:Double,sy:Double): Vector = Vector(x * sx, y * sy)
  operator fun times(s:Double) = scale(s,s)
  operator fun div(s:Double) = scale(1.0/s,1.0/s)
  //  Compute vector length
  override fun equals(other:Any?): Boolean =
      if (other is Vector) x.isAbout(other.x) && y.isAbout(other.y) else false
  val length:Double get() = sqrt(x*x + y*y)
  //  Angle off the X-axis
  val angle:Double get() = atan2(y,x)
  //  Rotate by a given angle
  fun rotate(angle2:Double): Vector {
    val d = length
    val a = angle + angle2
    return Vector(d * cos(a), d * sin(a))
  }
  //  Return difference angle between two vectors
  //  in the range of -pi to pi
  fun angleDiff(v: Vector):Double = angleAngleDiff(v.angle, angle)
  fun vectorTo(v: Vector): Vector = Vector(v.x - x, v.y - y)
  //  Return Z-coordinate of the cross product between two vectors
  fun crossZ(v: Vector) = x * v.y - y * v.x

  fun concatenate(tx: Matrix): Vector = tx * this
  fun preConcatenate(tx: Matrix): Vector = Matrix.getTranslation(x,y).preConcatenate(tx).location

  fun isApprox(v2:Vector, delta:Double=0.1) =
      x.isApprox(v2.x,delta) && y.isApprox(v2.y,delta)

  override fun toString(): String = "(${x.s},${y.s})"
  override fun hashCode(): Int {
    var result = x.hashCode()
    result = 31 * result + y.hashCode()
    return result
  }
}
