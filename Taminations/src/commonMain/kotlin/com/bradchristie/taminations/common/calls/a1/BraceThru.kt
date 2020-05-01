package com.bradchristie.taminations.common.calls.a1
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
import com.bradchristie.taminations.common.calls.Action

class BraceThru : Action("Brace Thru") {

  override val level = LevelObject("a1")
  override val requires = listOf("b1/pass_thru","b1/courtesy_turn","b1/turn_back","b2/wheel_around")

  override fun perform(ctx: CallContext, i: Int) {
    ctx.subContext(ctx.actives) {
      applyCalls("Pass Thru")
      analyze()
      for (d in dancers) {
        val partner = d.data.partner
          ?: throw CallError("Dancer $d cannot Brace Thru")
        if (d.gender == partner.gender)
          throw CallError("Same-sex dancers cannot Brace Thru")
      }
      val normal = dancers.filter { it.data.beau xor (it.gender == Gender.GIRL) }
      val sashay = dancers.filter { it.data.beau xor (it.gender == Gender.BOY) }
      if (normal.count() > 0)
        subContext(normal) {
          applyCalls("Courtesy Turn")
        }
      if (sashay.count() > 0)
        subContext(sashay) {
          applyCalls("Turn Back")
        }
    }
  }

}