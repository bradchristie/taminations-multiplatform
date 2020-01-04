package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations for Web Browsers
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

class PartnerTag : Action("Partner Tag") {

  override val level = LevelObject("a1")

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    //  Generally Partner Tag is with partner, but there can be exceptions
    val d2 = listOf(d.data.partner, ctx.dancerToRight(d), ctx.dancerToLeft(d)).firstOrNull {
      it != null && it.data.active
    } ?: throw CallError("Dancer $d cannnot Partner Tag")
    val dist = d.distanceTo(d2)
    return if (d2 isRightOf d)
      TamUtils.getMove("Lead Right").scale(0.5,dist/2) +
      TamUtils.getMove("Extend Right").scale(dist/2,0.5)
    else
      TamUtils.getMove("Quarter Left").skew(-0.5,dist/2) +
      TamUtils.getMove("Extend Right").scale(dist/2,0.5)
  }

}