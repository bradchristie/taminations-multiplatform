package com.bradchristie.taminations.common.calls
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

class Adjust(norm:String,name:String) : Action(norm,name) {

  override fun perform(ctx: CallContext, i: Int) {
    val fname = name.replace("adjust to (a(n)?)?\\b".ri,"")
    val formation = when (norm) {
      in ".*line(s)?".r -> TamUtils.getFormation("Normal Lines")
      in ".*thar".r -> TamUtils.getFormation("Thar RH Boys")
      in ".*square(d)?set".r -> TamUtils.getFormation("Squared Set")
      in ".*boxes".r -> TamUtils.getFormation("Eight Chain Thru")
      in ".*14tag".r -> TamUtils.getFormation("Quarter Tag")
      in ".*diamonds".r -> TamUtils.getFormation("Diamonds RH Girl Points")
      in ".*tidal(wave|line)?" -> TamUtils.getFormation("Tidal Line RH")
      in ".*hourglass".r -> TamUtils.getFormation("Hourglass RH BP")
      in ".*galaxy".r -> TamUtils.getFormation("Galaxy RH GP")
      in ".*butterfly".r -> TamUtils.getFormation("Butterfly RH")
      in ".*o".r -> TamUtils.getFormation("O RH")
      else -> throw CallError("Sorry, don't know how to $name from here.")
    }
    val ctx2 = CallContext(formation)
    val mapping = ctx.matchFormations(ctx2,sexy=false,fuzzy=true,rotate=180,handholds=false, maxError = 3.0)
      ?: throw CallError("Unable to match formation to $fname")
    val matchResult = ctx.computeFormationOffsets(ctx2,mapping,0.3)
    ctx.adjustToFormationMatch(matchResult)
  }

}