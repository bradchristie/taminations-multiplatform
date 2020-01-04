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

import com.bradchristie.taminations.platform.*
import kotlin.math.*

abstract class Geometry(val rotnum:Int) /* : Cloneable */ {

  companion object {
    const val BIGON = 1
    const val SQUARE = 2
    const val HEXAGON = 3

    fun DrawingContext.gridPaint():DrawingStyle = DrawingStyle(
      color = Color.LIGHTGRAY,
      // anti-alias is on by default
      // stroke vs fill is determined by call
      // Canvas does not have a 1-pixel width setting
      //  so we need to calculate it
      lineWidth = 1.0/((height min width) / 13.0))


    operator fun invoke(g:Int, r:Int=0): Geometry = when (g) {
      BIGON -> BigonGeometry(r)
      HEXAGON -> HexagonGeometry(r)
      else -> SquareGeometry(r)
    }

    operator fun invoke(gstr:String): Geometry = when (gstr.toLowerCase()) {
      "bi-gon" -> BigonGeometry(0)
      "hexagon" -> HexagonGeometry(0)
      else -> SquareGeometry(0)
    }

    /**
     * Factory method to get a geometry object
     * @param sym  One of BIGON, SQUARE, HEXAGON
     * @return  Geometry object to compute symmetric dancer locations
     */
    fun getGeometry(sym:Int):List<Geometry> = when (sym) {
      BIGON -> listOf(BigonGeometry(0))
      HEXAGON -> listOf(HexagonGeometry(0), HexagonGeometry(1), HexagonGeometry(2))
      else -> listOf(SquareGeometry(0), SquareGeometry(1))
    }

  }  //  end of companion object

  open val geometry = 0
  /**
   * used for computing dancer path
   */
  var prevangle = 0.0

  /**
   * Generate a transform to apply to a dancer's start position
   */
  abstract fun startMatrix(mat: Matrix): Matrix

  /**
   * Convert transform for a dancer's current position
   */
  abstract fun pathMatrix(starttx: Matrix, tx: Matrix, beat:Double): Matrix

  /**
   * Draw a dancer-sized grid of the specific geometry
   * @param ctx  Canvas to draw grid on
   */
  abstract fun drawGrid(ctx: DrawingContext)

  abstract fun clone(): Geometry

}

/////  Specific Geometry classes  //////
class BigonGeometry(rotnum:Int) : Geometry(rotnum) {

  override val geometry = BIGON
  override fun toString() = "Bigon"
  override fun clone(): Geometry = BigonGeometry(rotnum)

  /**
   * Generate a transform to apply to a dancer's start position
   */
  override fun startMatrix(mat: Matrix): Matrix {
    val x = mat.m31
    val y = mat.m32
    val r = sqrt(x * x + y * y)
    val startangle = atan2(mat.m12, mat.m22)
    val angle = atan2(y, x) + PI
    val bigangle = angle * 2 - PI
    val x2 = r * cos(bigangle)
    val y2 = r * sin(bigangle)
    return Matrix().postRotate(startangle + angle).postTranslate(x2, y2)
  }

  /**
   * Convert transform for a dancer's current position
   */
  override fun pathMatrix(starttx: Matrix, tx: Matrix, beat: Double): Matrix {
    //  Get dancer's start angle and current angle
    val x = starttx.m31
    val y = starttx.m32
    val a0 = atan2(y, x)
    val x2 = tx.m31
    val y2 = tx.m32
    val a1 = atan2(y2, x2)
    if (beat <= 0.0)
      prevangle = a1
    val wrap = round((a1 - prevangle) / (PI * 2))
    val a2 = a1 - wrap * PI * 2
    val a3 = a2 - a0
    prevangle = a2
    return Matrix().postRotate(a3)
  }

  override fun drawGrid(ctx: DrawingContext) {
    val p = ctx.gridPaint()
    for (xs in -1..1 step 2) {
      ctx.save()
      ctx.scale(xs.d,1.0)
      for (xi in -75..75 step 10) {
        val x1 = xi / 10.0
        val path = DrawingPath()
        path.moveTo(abs(x1), 0.0)
        for (yi in 2..75 step 2) {
          val y1 = yi / 10.0f
          val a = 2.0 * atan2(y1.d, x1)
          val r = sqrt(x1 * x1 + y1 * y1)
          val x = r * cos(a)
          val y = r * sin(a)
          path.lineTo(x, y)
        }
        ctx.drawPath(path,p)
      }
      ctx.restore()
    }
  }
}

///////////////////////////////////////////////////////////////////////////
class SquareGeometry(rotnum: Int) : Geometry(rotnum) {
  override val geometry = SQUARE
  override fun toString() = "Square"
  override fun clone() = SquareGeometry(rotnum)
  /**
   * Generate a transform to apply to a dancer's start position
   */
  override fun startMatrix(mat: Matrix): Matrix =
      Matrix(mat).postRotate(PI * rotnum)

  /**
   * Convert transform for a dancer's current position
   */
  //  No additional transform needed for squares
  override fun pathMatrix(starttx: Matrix, tx: Matrix, beat: Double): Matrix = Matrix()

  override fun drawGrid(ctx: DrawingContext) {
    val p = ctx.gridPaint()
    for (x in -75..75 step 10) {
      val path = DrawingPath()
      path.moveTo(x / 10.0, -7.5)
      path.lineTo(x / 10.0, 7.5)
      ctx.drawPath(path,p)
    }
    for (y in -75..75 step 10) {
      val path = DrawingPath()
      path.moveTo(-7.5, y / 10.0)
      path.lineTo(7.5, y / 10.0)
      ctx.drawPath(path,p)
    }
  }

}

///////////////////////////////////////////////////////////////////////////
class HexagonGeometry(rotnum:Int) : Geometry(rotnum) {
  override val geometry = HEXAGON
  override fun toString() = "Hexagon"
  override fun clone(): Geometry = HexagonGeometry(rotnum)
  /**
   * Generate a transform to apply to a dancer's start position
   */
  override fun startMatrix(mat: Matrix): Matrix {
    val a = (PI * 2 / 3) * rotnum
    val x = mat.m31
    val y = mat.m32
    val r = sqrt(x*x + y*y)
    val startangle = atan2(mat.m12,mat.m22)
    val angle = atan2(y,x)
    val dangle = if (angle < 0.0) -(PI+angle)/3 else (PI-angle)/3
    val x2 = r * cos(angle+dangle+a)
    val y2 = r * sin(angle+dangle+a)
    val startangle2 = startangle + a + dangle
    return Matrix().postRotate(startangle2).postTranslate(x2,y2)
  }

  /**
   * Convert transform for a dancer's current position
   */
  private var pathMatrix = Matrix()
  override fun pathMatrix(starttx: Matrix, tx: Matrix, beat: Double): Matrix {
    //  Get dancer's start angle and current angle
    val x = starttx.m31
    val y = starttx.m32
    val a0 = atan2(y,x)
    val x2 = tx.m31
    val y2 = tx.m32
    val a1 = atan2(y2,x2)
    //  Correct for wrapping around +/- pi
    if (beat <= 0)
      prevangle = a1
    val wrap = round((a1-prevangle)/(PI*2))
    val a2 = a1 - wrap*PI*2
    val a3 = -(a2-a0)/3
    prevangle = a2
    pathMatrix = Matrix()
    pathMatrix = pathMatrix.postRotate(a3)
    return pathMatrix
  }

  override fun drawGrid(ctx:DrawingContext) {
    val p = ctx.gridPaint()
    for (yscale in -1 .. 1 step 2) {
      for (a in 0..6) {
        ctx.save()
        ctx.rotate(PI/6 + a*PI/3)
        ctx.scale(1.0, yscale.d)
        for (xi in 5..85 step 10) {
          val x0 = xi / 10.0
          val path = DrawingPath()
          path.moveTo(0.0, x0)
          for (yi in 5..85 step 5) {
            val y0 = yi / 10.0
            val aa = atan2(y0, x0) * 2 / 3
            val r = sqrt(x0 * x0 + y0 * y0)
            val x = r * sin(aa)
            val y = r * cos(aa)
            path.lineTo(x, y)
          }
          ctx.drawPath(path,p)
        }
        ctx.restore()
      }
    }
  }

}