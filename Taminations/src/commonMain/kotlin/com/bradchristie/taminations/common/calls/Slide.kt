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

//  This class is for manually moving dancers left or right,
//  not the A-2 call Slide
class Slide(norm:String, name:String) : Action(norm,name) {

  override fun performOne(d: Dancer, ctx: CallContext): Path {
    val dir = when (norm) {
      "slideleft" -> "Left"
      "slideright" -> "Right"
      "slidein" -> if (d.isCenterLeft) "Left" else "Right"
      "slideout" -> if (d.isCenterLeft) "Right" else "Left"
      else -> throw CallError("Slide how?")
    }
    return TamUtils.getMove("Dodge $dir")
  }


}