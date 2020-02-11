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

import com.bradchristie.taminations.platform.TamElement
import kotlin.math.max
import kotlin.math.min

object Hands {
  const val NOHANDS = 0
  const val LEFTHAND = 1
  const val RIGHTHAND = 2
  const val BOTHHANDS = 3
  const val GRIPLEFT = 5
  const val GRIPRIGHT = 6
}

fun getHands(h:String):Int =
    hashMapOf("none" to 0, "nohands" to 0, "left" to 1, "right" to 2, "both" to 3, "anygrip" to 4,
        "gripleft" to 5, "gripright" to 6, "gripboth" to 7)[h]!!

/**  Constructor for a movement where the dancer does not face the direction
 *   of travel.  Two Bezier curves are used, one for travel and one for
 *   facing direction.
 *
 * @param fullbeats  Timing
 * @param hands  One of the const ints above
 *     Next set of parameters are for direction of travel
 *     X and Y values for start of curve are always 0,0
 *     Next set of parameters are for facing direction
 *     X and Y values for start of curve, as well as Y value for 1st model
 *     point, are all 0
 * @param btranslate  Bezier curve for movement
 * @param brotate  Bezier curve for facing direction, can be same as btranslate
 * @param beats  Where to stop for a clipped movement
 */
class Movement(private val fullbeats:Double, val hands:Int,
               val btranslate: Bezier,
               val brotate: Bezier,
               val beats:Double = fullbeats) {

  //  for sequencer
  var fromCall = true

  /**
   * Construct a Movement from the attributes of an XML movement
   * @param elem from xml
   */
  constructor(elem: TamElement) :
      this(elem.getAttribute("beats")!!.toDouble(),
          getHands(elem.getAttribute("hands") ?: ""),
          Bezier(0.0,0.0,
              elem.getAttribute("cx1")!!.toDouble(),
              elem.getAttribute("cy1")!!.toDouble(),
              elem.getAttribute("cx2")!!.toDouble(),
              elem.getAttribute("cy2")!!.toDouble(),
              elem.getAttribute("x2")!!.toDouble(),
              elem.getAttribute("y2")!!.toDouble()),
          Bezier(0.0,0.0,
              elem.getAttribute(if (elem.hasAttribute("cx3")) "cx3" else "cx1")!!.toDouble(),
              0.0,
              elem.getAttribute(if (elem.hasAttribute("cx4")) "cx4" else "cx2")!!.toDouble(),
              elem.getAttribute(if (elem.hasAttribute("cy4")) "cy4" else "cy2")!!.toDouble(),
              elem.getAttribute(if (elem.hasAttribute("x4")) "x4" else "x2")!!
                   .toDouble(),
              elem.getAttribute(if (elem.hasAttribute("y4" )) "y4" else "y2")!!.toDouble()),
          elem.getAttribute("beats")!!.toDouble())

  /**
   * Return a matrix for the translation part of this movement at time t
   * @param t  Time in beats
   * @return   Matrix for using with canvas
   */
  fun translate(t:Double = beats): Matrix {
    val tt = min(max(0.0,t),fullbeats)
    return btranslate.translate(tt/fullbeats)
  }

  /**
   * Return a matrix for the rotation part of this movement at time t
   * @param t  Time in beats
   * @return   Matrix for using with canvas
   */
  fun rotate(t:Double = beats): Matrix {
    val tt = min(max(0.0,t),fullbeats)
    return brotate.rotate(tt / fullbeats)
  }

  /**
   * Return a new movement by changing the beats
   */
  fun time(b:Double): Movement = Movement(b, hands, btranslate, brotate, b)

  /**
   * Return a new movement by changing the hands
   */
  fun useHands(h:Int): Movement = Movement(fullbeats, h, btranslate, brotate, beats)

  /**
   * Return a new Movement scaled by x and y factors.
   * If y is negative hands are also switched.
   */
  fun scale(x: Double, y: Double): Movement =
      Movement(fullbeats,
          if (y < 0 && hands == Hands.RIGHTHAND) Hands.LEFTHAND
          else if (y < 0 && hands == Hands.LEFTHAND) Hands.RIGHTHAND
          else hands, // what about GRIPLEFT, GRIPRIGHT?
          btranslate.scale(x,y), brotate.scale(x,y), beats)

  /**
   * Return a new Movement with the end point shifted by x and y
   */
  fun skew(x: Double, y: Double): Movement =
      if (beats < fullbeats) skewClip(x,y) else skewFull(x,y)

  private fun skewFull(x: Double, y: Double): Movement =
      Movement(fullbeats, hands, btranslate.skew(x,y), brotate, beats)
  private fun skewClip(x: Double, y: Double): Movement {
    var vdelta = Vector(x, y)
    val vfinal = this.translate().location + vdelta
    var m = this
    var maxiter = 100
    do {
      // Shift the end point by the current difference
      m = m.skewFull(vdelta.x, vdelta.y)
      // See how that affects the clip point
      val loc = m.translate().location
      vdelta = vfinal - loc
      maxiter -= 1
    } while (vdelta.length > 0.001 && maxiter > 0)
    //  If timed out, return original rather than something that
    //  might put the dancers in outer space
    return if (maxiter > 0) m else this
  }

  /**
   * Skew a movement based on an  adjustment to the final position
   */
  fun skewFromEnd(x: Double, y: Double): Movement {
    val a = rotate().angle
    val v = Vector(x,y).rotate(a)
    return skew(v.x,v.y)
  }


  fun reflect(): Movement = scale(1.0, -1.0)

  fun clip(b:Double): Movement = Movement(fullbeats, hands,
      btranslate, brotate, b)

  fun isStand(): Boolean = btranslate.isIdentity() && brotate.isIdentity()

}
