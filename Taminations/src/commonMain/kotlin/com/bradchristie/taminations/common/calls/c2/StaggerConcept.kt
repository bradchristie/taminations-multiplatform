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

import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.calls.ModifedFormationConcept
import kotlin.math.PI

class StaggerConcept(norm:String,name:String=norm) : ModifedFormationConcept(norm,name) {

  override val level = LevelObject.find("c2")
  override val conceptName = "Stagger"
  override val modifiedFormationName = "Double Pass Thru"

  private var startFormation = ""
  override val formationName get() = startFormation

  //  Starting formation could be blocks leaning left or right
  //  So check both and remember which one
  override fun checkFormation(ctx:CallContext) : Boolean {
    val ctx1 = CallContext(TamUtils.getFormation("Facing Blocks Right"))
    val ctx2 = CallContext(TamUtils.getFormation("Facing Blocks Left"))
    if (ctx.matchFormations(ctx1,sexy=false,fuzzy=true,rotate=180,handholds=false) != null) {
      startFormation = "Facing Blocks Right"
      return true
    }
    if (ctx.matchFormations(ctx2,sexy=false,fuzzy=true,rotate=180,handholds=false) != null) {
      startFormation = "Facing Blocks Left"
      return true
    }
    return false
  }

  override fun reformFormation(ctx: CallContext) : Boolean {
    //  If the dancers have rotated 90 degrees, then we need to switch to
    //  the other block to get the dancers back on the same footprints
    ctx.dancers[0].animate(0.0)
    val a1 = ctx.dancers[0].angleFacing
    ctx.dancers[0].animateToEnd()
    val a2 = ctx.dancers[0].angleFacing
    val finalFormation =
        if (a1.angleDiff(a2).abs.isAround(PI/2) xor (startFormation=="Facing Blocks Right"))
      "Facing Blocks Right"
    else
      "Facing Blocks Left"
    return ctx.adjustToFormation(finalFormation)
  }

}