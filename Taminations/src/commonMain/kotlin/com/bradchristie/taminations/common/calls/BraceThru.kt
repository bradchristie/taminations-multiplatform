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

import com.bradchristie.taminations.common.*

class BraceThru : Action("Brace Thru") {

  override val level = LevelObject("a1")
  override val requires = listOf("b1/pass_thru","b1/courtesy_turn","b1/turn_back","b2/wheel_around")


  override fun perform(ctx: CallContext, i: Int) {
    ctx.applyCalls("Pass Thru")
    //  This is a rather complex check to handle not only
    //  Brace Thru for lines but also Centers Brace Thru
    val normal = mutableListOf<Dancer>()
    val sashay = mutableListOf<Dancer>()
    for (d in ctx.actives) {
      var isBeau = false
      var isBelle = false
      ctx.dancerToRight(d)?.let {
        if (d.data.beau && it.data.active)
          isBeau = true
      }
      ctx.dancerToLeft(d)?.let {
        if (d.data.belle && it.data.active)
          isBelle = true
      }
      if (!isBeau && !isBelle) {
        ctx.dancerToRight(d)?.let {
          if (it.data.active)
            isBeau = true
        }
        ctx.dancerToLeft(d)?.let {
          if (it.data.active)
            isBelle = true
        }
      }
      if (isBeau) {
        if (d.gender == Gender.BOY)
          normal += d
        else
          sashay += d
      } else if (isBelle) {
        if (d.gender == Gender.GIRL)
          normal += d
        else
          sashay += d
      } else
        throw CallError("Cannot figure out how to Brace Thru")
    }
    if (normal.count() > 0)
      CallContext(ctx,normal).applyCalls("Courtesy Turn").appendToSource()
    if (sashay.count() > 0)
      CallContext(ctx,sashay).applyCalls("Turn Back").appendToSource()
  }

}