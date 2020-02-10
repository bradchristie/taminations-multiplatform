package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.*

class BoxtheGnat : Action("Box the Gnat") {

  override val level = LevelObject("b2")

  private fun checkOtherDancer(d: Dancer, d2: Dancer?): Dancer? {
    val other = d2 ?: return null
    if (!other.data.active)
      return null
    if (other.gender == d.gender)
      return null
    return other
  }

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    if (ctx.isInWave(d)) {
      val d2 = checkOtherDancer(d,ctx.dancerToRight(d))
        ?: return ctx.dancerCannotPerform(d,name)
      val dist = d.distanceTo(d2)
      val offset = when {
        dist > 1.5 && d.data.end -> -dist
        dist > 1.5 && d.data.center -> 0.0
        else -> -dist/2.0
      }
      return TamUtils.getMove(if (d.gender==Gender.BOY) "U-Turn Right" else "U-Turn Left").skew(1.0,offset).changehands(Hands.GRIPRIGHT)
    } else {
      val d2 = checkOtherDancer(d,ctx.dancerFacing(d))
        ?: return ctx.dancerCannotPerform(d,name)
      val dist = d.distanceTo(d2)
      val cy1 = if (d.gender == Gender.BOY) 1.0 else 0.1
      val y4 = if (d.gender == Gender.BOY) -2.0 else 2.0
      val hands = if (d.gender == Gender.BOY) Hands.GRIPLEFT else Hands.GRIPRIGHT
      val m = Movement(
        4.0, hands,
        Bezier(0.0, 0.0, 1.0, cy1, dist / 2, cy1, dist / 2 + 1, 0.0),
        Bezier(0.0, 0.0, 1.3, 0.0, 1.3, y4, 0.0, y4)
      )
      return Path(m)
    }
  }
}