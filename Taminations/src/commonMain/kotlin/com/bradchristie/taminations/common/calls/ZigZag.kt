package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations
  Copyright (C) 2018 Brad Christie

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
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.LevelObject

class ZigZag(norm:String, name:String) : QuarterTurns(norm,name) {

  override val level = LevelObject("a2")

  override fun select(ctx: CallContext, d: Dancer): String = when {
    d.data.leader && norm.matches(Regex("zigz[ai]g")) -> "Quarter Right"
    d.data.leader && norm.matches(Regex("zagz[ai]g")) -> "Quarter Left"
    d.data.trailer && norm.matches(Regex("z[ai]gzig")) -> "Quarter Right"
    d.data.trailer && norm.matches(Regex("z[ai]gzag")) -> "Quarter Left"
    else -> "Stand"
  }

}