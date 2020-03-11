package com.bradchristie.taminations.common.calls.a2
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
import com.bradchristie.taminations.common.ri

class TransferAnd(norm:String,name:String) : Action(norm,name) {

  override val level = LevelObject("a2")
  override val requires = listOf("a2/transfer_and_anything")

  override fun perform(ctx: CallContext, i: Int) {
    val othercall = name.replace("Transfer\\s+and\\s+".ri,"")
    ctx.applyCalls("Transfer and")
    ctx.contractPaths()
    ctx.applyCalls("Centers $othercall")
  }

}