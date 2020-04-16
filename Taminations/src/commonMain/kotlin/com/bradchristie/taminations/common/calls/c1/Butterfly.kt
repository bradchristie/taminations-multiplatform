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
import com.bradchristie.taminations.common.calls.ModifedFormationConcept

class Butterfly(norm:String,name:String) : ModifedFormationConcept(norm,name) {

  override val level = LevelObject("c1")
  override val conceptName = "Butterfly"
  override val modifiedFormationName = "Double Pass Thru"
  override val formationName = "Butterfly RH"

  override fun reformFormation(ctx: CallContext): Boolean {
    //  First try the usual way
    if (!super.reformFormation(ctx)) {
      //  That didn't work, we are too far off from a butterfly
      //  So first just concentrate on the centers
      val centers = CallContext(ctx,ctx.center(4).inOrder())
      if (centers.adjustToFormation("Facing Couples Close",rotate = 180)) {
        //  And now use the base method to fix the outer 4
        centers.appendToSource()
        return super.reformFormation(ctx)
      }
      return false
    }
    return true
  }

}