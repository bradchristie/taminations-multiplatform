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
import com.bradchristie.taminations.common.TamUtils.translatePath
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.platform.attr

class Finish(callnorm:String,callname:String) : Action(callnorm,callname) {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {
    val finishCall = name.replace("Finish\\s+".ri," ")
    val finishNorm = TamUtils.normalizeCall(finishCall)
    //  For now we just work with XML calls
    //  Find matching XML call
    val files = ctx.xmlFilesForCall(finishNorm)
    val found = files.any { link ->
      CallContext.loadedXML[link]?.evalXPath("/tamination/tam")?.asSequence()
          ?.filter { tam ->
            tam.attr("sequencer")!="no" &&
            TamUtils.normalizeCall(tam.attr("title")) == finishNorm
          }?.first { tam ->
            //  Should be divided into parts, will also accept fractions
            val parts = tam.attr("parts") + tam.attr("fractions")
            val allp = tam.children("path").map { Path(translatePath(it)) }
            parts.split(";").firstOrNull()?.d?.let { firstPart ->
              //  Load the call and animate past the first part
              val ctx2 = CallContext(tam,loadPaths = true)
              ctx2.animate(firstPart)
              ctx.matchFormations(ctx2)?.let { mapping ->
                val matchResult = ctx.computeFormationOffsets(ctx2, mapping)
                ctx.adjustToFormationMatch(matchResult)
                mapping.forEachIndexed { i,m ->
                  val p = Path(allp[m shr 1])
                  var firstBeats = 0.0
                  while (firstBeats.isLessThan(firstPart)) {
                    firstBeats += p.shift()?.beats ?: firstPart
                  }
                  ctx.dancers[i].path.add(p)
                }
                true
              } ?: false
            } ?: false
          } != null
    }
    if (!found)
      throw CallError("Could not figure out how to Finish $finishCall")
  }

}