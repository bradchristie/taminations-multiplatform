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

import kotlin.math.PI
import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.TamUtils.getMove

class SquareTheSet : Action("Square the Set") {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val xhome = when (ctx.dancers.count()) {
      4 -> if (d.number_couple == "1") -2.0 else 2.0
      else -> when (d.number.i) {
        1, 2 -> -3.0
        3, 8 -> -1.0
        4, 7 -> 1.0
        5, 6 -> 3.0
        else -> 0.0  // never
      }
    }
    val yhome = when (ctx.dancers.count()) {
      4 -> if ((d.number_couple=="1") xor (d.gender==Gender.GIRL)) 1.0 else -1.0
      else -> {
        when (d.number.i) {
          1,6 -> 1.0
          2,5 -> -1.0
          3,4 -> -3.0
          7,8 -> 3.0
          else -> 0.0  // never
        }
      }
    }
    val ahome = when (ctx.dancers.count()) {
      4 -> if (d.number_couple=="1") 0.0 else PI
      else -> when (d.number.i) {
        1,2 -> 0.0
        3,4 -> PI/2
        5,6 -> PI
        7,8 -> PI*3/2
        else -> 0.0  // never
      }
    }
    var tohome = (Vector(xhome,yhome) - d.location)
    val angle = d.tx.angle
    tohome = tohome.rotate(-angle)
    val adiff = ahome.angleDiff(angle)
    val turn = when {
      adiff.angleEquals(0.0) -> "Stand"
      adiff.angleEquals(PI/4) -> "Eighth Left"
      adiff.angleEquals(PI/2) -> "Quarter Left"
      adiff.angleEquals(3*PI/4) -> "3/8 Left"
      adiff.angleEquals(PI) -> "U-Turn Right"
      adiff.angleEquals(-3*PI/4) -> "3/8 Right"
      adiff.angleEquals(-PI/2) -> "Quarter Right"
      adiff.angleEquals(-PI/4) -> "Eighth Right"
      else -> "Stand"
    }
    return getMove(turn).changebeats(2.0).skew(tohome.x,tohome.y)
  }

}
