package com.bradchristie.taminations.common.calls.c1
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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.ri
import com.bradchristie.taminations.platform.System

class Start(norm: String, name: String) : Action(norm, name) {

  override val level = LevelObject("c1")
  override val requires = listOf("b1/pass_thru","b2/trade","c1/finish")

  override fun perform(ctx: CallContext, i: Int) {
    System.log("Start $name")
    val finishCall = name.replace("^start\\s+".ri,"")
    //  There has to be a subset of dancers selected to Start
    if (ctx.actives.count() >= ctx.dancers.count())
      throw CallError("Who is supposed to start?")
    //  If the actives are facing, assume that the first part is Pass Thru
    val startCall = if (ctx.actives.all {
          ctx.dancerFacing(it)?.data?.active == true
        })
      "Pass Thru"
    else
      //  Otherwise for now we will try a Trade
      "Trade"
    ctx.applyCalls(startCall)
    ctx.dancers.forEach { it.data.active = true }
    ctx.applyCalls("Finish $finishCall")
  }

}