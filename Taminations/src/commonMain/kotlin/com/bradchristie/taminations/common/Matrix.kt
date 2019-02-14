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

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/*
Table for matrix fields iOS and Java
iOS                         Java                                     Win Matrix3x2
a   c   tx        MSCALE_X(0)    MSKEW_X(1)    MTRANS_X(2)       M11    M21   M31
b   d   ty        MSKEW_Y(3)     MSCALE_Y(4)   MTRANS_Y(5)       M12    M22   M32
0   0   1         MPERSP_0(6)    MPERSP_1(7)   MPERSP_2(8)       0      0     1

*/

class Matrix(val m11:Double=1.0, val m21:Double=0.0, val m31:Double=0.0,
             val m12:Double=0.0, val m22:Double=1.0, val m32:Double=0.0) {

  companion object {
    fun getRotation(angle:Double): Matrix =
        Matrix(cos(angle), -sin(angle), 0.0, sin(angle), cos(angle), 0.0)
    fun getTranslation(x:Double,y:Double): Matrix = Matrix(m31 = x, m32 = y)
    //not used fun getScale(x:Double,y:Double):Matrix = Matrix(m11=x,m22=y)
  }

  //  Copy constructor
  constructor(m: Matrix) : this(m.m11,m.m21,m.m31,m.m12,m.m22,m.m32)

  override fun toString(): String {
    return "[$m11, $m12, $m21, $m22, $m31, $m32]"
  }

  //  Compute and return this * m
  private fun multiply(m: Matrix): Matrix = Matrix(
      m11 * m.m11 + m21 * m.m12,
      m11 * m.m21 + m21 * m.m22,
      m11 * m.m31 + m21 * m.m32 + m31,
      m12 * m.m11 + m22 * m.m12,
      m12 * m.m21 + m22 * m.m22,
      m12 * m.m31 + m22 * m.m32 + m32)
  operator fun times(m: Matrix) = multiply(m)

  //  Compute and return this * v
  private fun multiply(v: Vector): Vector = Vector(
      m11 * v.x + m21 * v.y + m31,
      m12 * v.x + m22 * v.y + m32)
  operator fun times(v: Vector) = multiply(v)

  //  Note that preConcatenate == "post" multiply
  //  and postConcatenate == "pre" multiply
  //  That's the way the math and geometry works out
  fun preConcatenate(m: Matrix): Matrix = this * m
  fun postConcatenate(m: Matrix): Matrix = m * this
  fun preRotate(angle:Double): Matrix = this * getRotation(angle)
  fun postRotate(angle:Double): Matrix = getRotation(angle) * this
  fun preTranslate(x:Double,y:Double): Matrix = this * getTranslation(x, y)
  fun postTranslate(x:Double,y:Double): Matrix = getTranslation(x, y) * this
  //not used fun preScale(x:Double,y:Double):Matrix = this * getScale(x,y)
  //not used fun postScale(x:Double,y:Double):Matrix = getScale(x,y) * this

  //  This is for rotation transforms only,
  //  or when using as a 2x2 matrix (as in SVD)
  fun transpose(): Matrix = Matrix(m11, m12, 0.0, m21, m22, 0.0)

  val location: Vector get() = Vector(m31, m32)
  val direction: Vector get() = Vector(m11, m21)
  val angle:Double get() = atan2(m12,m22)

  //  Compute and return the inverse matrix - only for affine transform matrix
  fun inverse(): Matrix {
    val det = m11*m22 - m21*m12
    return Matrix(
        m22 / det,
        -m21 / det,
        (m21 * m32 - m22 * m31) / det,
        -m12 / det,
        m11 / det,
        (m12 * m31 - m11 * m32) / det)
  }

  //  If a rotation matrix is close to a 90 degree angle,snap to it
  private val Double.snapDouble: Double get() = when {
    this.isApprox(0.0) -> 0.0
    this.isApprox(1.0) -> 1.0
    this.isApprox(-1.0) -> -1.0
    else -> this
  }
  fun snapTo90(): Matrix = Matrix(m11.snapDouble,m21.snapDouble,m31,
                                  m12.snapDouble,m22.snapDouble,m32)

  //  SVD simple and fast for 2x2 arrays
  //  for matching 2d formations
  fun svd22():Triple<Matrix,DoubleArray, Matrix> {
    val a = m11
    val b = m12
    val c = m21
    val d = m22
    //  Check for trivial case
    val epsilon = 0.0001
    if (b.abs < epsilon && c.abs < epsilon) {
      val v = Matrix(m11 = if (a < 0.0) -1.0 else 1.0,
          m22 = if (d < 0.0) -1.0 else 1.0)
      val sigma = doubleArrayOf(a.abs,d.abs)
      val u = Matrix()
      return Triple(u,sigma,v)
    } else {
      val j = a.sq + b.sq
      val k = c.sq + d.sq
      val vc = a*c + b*d
      //  Check to see if A^T*A is diagonal
      if (vc.abs < epsilon) {
        val s1 = j.sqrt
        val s2 = if ((j-k).abs < epsilon) s1 else k.sqrt
        val sigma = doubleArrayOf(s1,s2)
        val v = Matrix()
        val u = Matrix(a / s1, b / s1, 0.0,
            c / s2, d / s2, 0.0)
        return Triple(u,sigma,v)
      } else {   //  Otherwise, solve quadratic for eigenvalues
        val atanarg1 = 2 * a * c + 2 * b * d
        val atanarg2 = a * a + b * b - c * c - d * d
        val theta = 0.5 * atan2(atanarg1,atanarg2)
        val u = Matrix(theta.cos, -theta.sin, 0.0,
            theta.sin, theta.cos, 0.0)

        val phi = 0.5 * atan2(2 * a * b + 2 * c * d, a.sq - b.sq + c.sq - d.sq)
        val s11 = (a * theta.cos + c * theta.sin) * phi.cos +
            (b * theta.cos + d * theta.sin) * phi.sin
        val s22 = (a * theta.sin - c * theta.cos) * phi.sin +
            (-b * theta.sin + d * theta.cos) * phi.cos

        val s1 = a.sq + b.sq + c.sq + d.sq
        val s2 = ((a.sq + b.sq - c.sq - d.sq).sq + 4 * (a * c + b * d).sq).sqrt
        val sigma = doubleArrayOf((s1 + s2).sqrt / 2, (s1 - s2).sqrt / 2)

        val v = Matrix(s11.sign * phi.cos, -s22.sign * phi.sin, 0.0,
            s11.sign * phi.sin, s22.sign * phi.cos, 0.0)
        return Triple(u,sigma,v)
      }
    }
  }

}
