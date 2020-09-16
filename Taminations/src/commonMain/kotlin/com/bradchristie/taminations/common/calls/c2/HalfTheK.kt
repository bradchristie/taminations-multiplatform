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

class HalfTheK : Action("Half the K") {

  override val level = LevelObject("c2")

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.dancers.filter { it.data.center }.count() == 4 &&
        ctx.dancers.filter { it.data.end}.count() == 4)
      ctx.applyCalls("Centers Hinge While Ends Quarter Out")
    else
      ctx.applyCalls("Center 4 Hinge while Outer 4 1/4 Out")
  }

}