package com.bradchristie.taminations.common.calls.c2

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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.r

//  Replace the first circulate of Motivate, Coordinate, Percolate or Perk Up
//  with another call
class AnythingConcept(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("c2")
  override val requires = listOf("a2/split_counter_rotate","c1/counter_rotate",
      "b1/circulate","a1/cross_over_circulate",
      "a2/in_roll_circulate","a2/out_roll_circulate",
      "a2/trade_circulate",
      "c2/split_trade_circulate",
      "c3a/scatter_circulate")

  override fun perform(ctx: CallContext, i: Int) {
    var firstCall = norm.replace("(.*)(motivate|coordinate|percolate|perkup)".r,"$1")
    val secondCall = norm.replace("(.*)(motivate|coordinate|percolate|perkup)".r,"$2")
    //  If the first call is Counter Rotate or Split Counter Rotate
    //  the word Rotate is generally omitted
    if (firstCall.matches("(split)?counter".r))
      firstCall += "rotate"
    //  If the first call is any type of Circulate
    //  the word Circulate is generally omitted
    else if (firstCall.matches("split|trade|splittrade|inroll|outroll|crossover|scatter".r))
      firstCall += "circulate"
    ctx.applyCalls(firstCall,"Finish $secondCall")
  }

}