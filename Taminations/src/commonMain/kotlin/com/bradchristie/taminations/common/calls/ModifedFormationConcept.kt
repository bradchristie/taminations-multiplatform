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

//  Base class for calls that are modified columns
//  Butterfly, O, Staggered
abstract class ModifedFormationConcept(norm:String, name:String=norm) : Action(norm,name) {

  protected open val conceptName = ""
  protected val realCall get() = name.replace("$conceptName ".ri,"")
  protected open val formationName = ""
  protected open val modifiedFormationName = ""

  protected open fun checkFormation(ctx:CallContext) : Boolean {
    val ctx2 = CallContext(TamUtils.getFormation(formationName))
    return ctx.matchFormations(ctx2,sexy=false,fuzzy=true,rotate=90,handholds=false) != null
  }

  protected open fun reformFormation(ctx:CallContext) : Boolean =
      ctx.adjustToFormation(formationName,rotate=180) ||
      ctx.adjustToFormation(formationName,rotate=90)


  override fun perform(ctx: CallContext, i: Int) {
    //  Check that the formation matches
    if (!checkFormation(ctx))
      throw CallError("Not $conceptName formation")
    //  Shift dancers into modified formation
    ctx.adjustToFormation(modifiedFormationName)
    val adjusted = ctx.dancers.filter { d -> d.path.movelist.isNotEmpty() }

    //  Perform the call
    val callName = name.replaceFirst(conceptName.ri,"")
    ctx.applyCalls(callName)
    //  Merge the slide in adjustment into the start of the call
    ctx.dancers.forEach { d ->
      if (d in adjusted && d.path.movelist.count() > 1) {
        val dy = d.path.movelist.first().btranslate.endPoint.y
        d.path.shift()
        d.path.skewFirst(0.0,dy)
      }
    }

    //  Reform the formation
    if (!reformFormation(ctx))
      throw CallError("Unable to reform $formationName formation")
  }

}