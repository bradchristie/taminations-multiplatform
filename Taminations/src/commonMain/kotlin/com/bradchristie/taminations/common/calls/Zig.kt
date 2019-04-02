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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.Dancer
import com.bradchristie.taminations.common.LevelObject

//  This is for the one-word calls Zig and Zag
//  Zig-Zag etc are handled in another class
class Zig(norm:String,name:String) : QuarterTurns(norm,name) {

  override val level = LevelObject("a2")

  override fun select(ctx: CallContext, d: Dancer): String = when {
    d.data.leader && name.matches(Regex("Zig")) -> "Quarter Right"
    d.data.leader && name.matches(Regex("Zag")) -> "Quarter Left"
    else -> "Stand"
  }

}