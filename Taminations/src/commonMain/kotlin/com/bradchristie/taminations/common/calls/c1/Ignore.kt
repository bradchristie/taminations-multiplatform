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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.Action

class Ignore(callnorm:String,callname:String) : Action(callnorm,callname) {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {
    //  Who should we ignore?
    "ignore (?:the )?((?:$specifier )+)(?:and )?(?:for a )?(.+)".ri.matchEntire(name)?.groupValues?.let { match ->
      val who = match[1]
      val call = match[2]
      //  Remember the dancers that we will ignore
      ctx.subContext(ctx.dancers) {
        interpretCall(who,noAction = true)
        performCall()
        val ignoreDancers = actives
        //  Do the call
        dancers.forEach { it.data.active = true }
        applyCalls(call)
        //  Now erase the action of the ignored dancers
        dancers.filter { it in ignoreDancers }.forEach {
          it.path = Path()
        }
      }
    }

  }

}