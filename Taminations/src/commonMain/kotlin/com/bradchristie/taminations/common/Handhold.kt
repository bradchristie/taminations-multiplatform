package com.bradchristie.taminations.common
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

import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

data class Handhold(val dancer1: Dancer, val dancer2: Dancer,
                    val hold1:Int, val hold2:Int,
                    val angle1:Double, val angle2:Double,
                    val distance:Double, val score:Double) {

  val inCenter: Boolean get() = dancer1.inCenter && dancer2.inCenter

  companion object {
    operator fun invoke(d1: Dancer, d2: Dancer, geometry: Int): Handhold? {
      var retval: Handhold? = null
      if (!d1.hidden && !d2.hidden) {

        //  Turn off grips if not specified in current movement
        if ((d1.hands and Hands.GRIPRIGHT) != Hands.GRIPRIGHT)
          d1.rightgrip = null
        if ((d1.hands and Hands.GRIPLEFT) != Hands.GRIPLEFT)
          d1.leftgrip = null
        if ((d2.hands and Hands.GRIPRIGHT) != Hands.GRIPRIGHT)
          d2.rightgrip = null
        if ((d2.hands and Hands.GRIPLEFT) != Hands.GRIPLEFT)
          d2.leftgrip = null

        //  Check distance
        val x1 = d1.tx.m31
        val y1 = d1.tx.m32
        val x2 = d2.tx.m31
        val y2 = d2.tx.m32
        val dx = x2 - x1
        val dy = y2 - y1
        val dfactor1 = 0.1  // for distance up to 2.0
        val dfactor2 = 2.0  // for distance past 2.0
        val cutover = when (geometry) {
          Geometry.HEXAGON -> 2.5
          Geometry.BIGON -> 3.7
          else -> 2.0
        }
        val d = sqrt(dx * dx + dy * dy)
        val dfactor0 = if (geometry == Geometry.HEXAGON) 1.15 else 1.0
        val d0 = d * dfactor0
        var score1 = if (d0 > cutover) (d0 - cutover) * dfactor2 + 2 * dfactor1 else d0 * dfactor1
        var score2 = score1
        //  Angle between dancers
        val a0 = atan2(dy, dx)
        //  Angle each dancer is facing
        val a1 = atan2(d1.tx.m12, d1.tx.m22)
        val a2 = atan2(d2.tx.m12, d2.tx.m22)
        //  For each dancer, try left and right hands
        var h1 = 0
        var h2 = 0
        var ah1 = 0.0
        var ah2 = 0.0
        val afactor1 = 0.2
        val afactor2 = if (geometry == Geometry.BIGON) 0.6 else 1.0

        //  Dancer 1
        var a = (a1 - a0 + PI * 3.0 / 2.0).abs.IEEErem(PI * 2.0).abs
        var ascore = if (a > PI / 6.0) (a - PI / 6.0) * afactor2 + PI / 6.0 * afactor1 else a * afactor1
        if (score1 + ascore < 1.0 && (d1.hands and Hands.RIGHTHAND) != 0 &&
            d1.rightgrip == null || d1.rightgrip == d2) {
          score1 = if (d1.rightgrip == d2) 0.0 else score1 + ascore
          h1 = Hands.RIGHTHAND
          ah1 = a1 - a0 + PI * 3.0 / 2.0
        } else {
          a = (a1 - a0 + PI / 2.0).abs.IEEErem(PI * 2.0).abs
          ascore = if (a > PI / 6.0) (a - PI / 6.0) * afactor2 + PI / 6.0 * afactor1 else a * afactor1
          if (score1 + ascore < 1.0 && (d1.hands and Hands.LEFTHAND) != 0 &&
              d1.leftgrip == null || d1.leftgrip == d2) {
            score1 = if (d1.leftgrip == d2) 0.0 else score1 + ascore
            h1 = Hands.LEFTHAND
            ah1 = a1 - a0 + PI / 2.0
          } else
            score1 = 10.0
        }

        //  Dancer 2
        a = (a2 - a0 + PI / 2.0).abs.IEEErem(PI * 2.0).abs
        ascore = if (a > PI / 6.0) (a - PI / 6.0) * afactor2 + PI / 6.0 * afactor1 else a * afactor1
        if (score2 + ascore < 1.0 && (d2.hands and Hands.RIGHTHAND) != 0 &&
            d2.rightgrip == null || d2.rightgrip == d1) {
          score2 = if (d2.rightgrip == d1) 0.0 else score2 + ascore
          h2 = Hands.RIGHTHAND
          ah2 = a2 - a0 + PI / 2.0
        } else {
          a = (a2 - a0 + PI * 3.0 / 2.0).abs.IEEErem(PI * 2.0).abs
          ascore = if (a > PI / 6.0) (a - PI / 6.0) * afactor2 + PI / 6.0 * afactor1 else a * afactor1
          if (score2 + ascore < 1.0 && (d2.hands and Hands.LEFTHAND) != 0 &&
              d2.leftgrip == null || d2.leftgrip == d1) {
            score2 = if (d2.leftgrip == d1) 0.0 else score2 + ascore
            h2 = Hands.LEFTHAND
            ah2 = a2 - a0 + PI * 3.0 / 2.0
          } else
            score2 = 10.0
        }

        //  Generate return value
        if (d1.rightgrip == d2 && d2.rightgrip == d1)
          retval = Handhold(d1, d2, Hands.RIGHTHAND, Hands.RIGHTHAND, ah1, ah2, d, 0.0)
        else if (d1.rightgrip == d2 && d2.leftgrip == d1)
          retval = Handhold(d1, d2, Hands.RIGHTHAND, Hands.LEFTHAND, ah1, ah2, d, 0.0)
        else if (d1.leftgrip == d2 && d2.rightgrip == d1)
          retval = Handhold(d1, d2, Hands.LEFTHAND, Hands.RIGHTHAND, ah1, ah2, d, 0.0)
        else if (d1.leftgrip == d2 && d2.leftgrip == d1)
          retval = Handhold(d1, d2, Hands.LEFTHAND, Hands.LEFTHAND, ah1, ah2, d, 0.0)
        else if (score1 <= 1.0 && score2 <= 1.0 && score1 + score2 <= 1.2)
          retval = Handhold(d1, d2, h1, h2, ah1, ah2, d, score1 + score2)
        //  otherwise dancers are too far apart

      }
      return retval
    }

  }

}