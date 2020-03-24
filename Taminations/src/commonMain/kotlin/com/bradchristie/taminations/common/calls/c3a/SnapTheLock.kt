package com.bradchristie.taminations.common.calls.c3a
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

class SnapTheLock : Action("Snap the Lock") {

  override val level = LevelObject("c3a")

  override val requires = listOf("a1/partner_tag","a1/lock_it","b1/step_thru")

  override fun perform(ctx: CallContext, i: Int) {
    try {
      ctx.applyCalls("Partner Tag",
        "Outsides Partner Tag While Centers Step to a Wave Lockit Step Thru")
    } catch (_: CallError) {
      throw CallError("Cannot Snap the Lock from this formation.")
    }
  }

}