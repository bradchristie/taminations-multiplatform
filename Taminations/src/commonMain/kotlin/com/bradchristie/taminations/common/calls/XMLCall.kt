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
import com.bradchristie.taminations.common.Path
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.common.TamUtils.translatePath
import com.bradchristie.taminations.platform.TamElement
import com.bradchristie.taminations.platform.attr

class XMLCall(val xelem: TamElement,
              val xmlmap:IntArray,
              val ctx2: CallContext) : Call(xelem.attr("title")) {

  override fun performCall(ctx: CallContext, i:Int) {
    //  TODO handle case where xelem is a xref
    val allp = xelem.children("path").map { Path(translatePath(it)) }
    //  If moving just some of the dancers,
    //  see if we can keep them in the same shape
    if (ctx.actives.count() < ctx.dancers.count()) {
      //  No animations have been done on ctx2,
      //  so dancers are still at the start points
      val ctx3 = CallContext(ctx2)
      //  So ctx3 is a copy of the start point
      //  Now add the paths
      ctx3.dancers.forEachIndexed { ii,d ->
        d.path.add(allp[ii shr 1])
      }
      //  And move it to the end point
      ctx3.extendPaths()
      ctx3.analyze()
    }
    val matchResult = ctx.computeFormationOffsets(ctx2,xmlmap)
    xmlmap.forEachIndexed { i3, m ->
      val p = Path(allp[m shr 1])
      if (p.movelist.isEmpty())
        p.add(TamUtils.getMove("Stand"))
      //  Scale active dancers to fit the space they are in
      //  Compute difference between current formation and XML formation
      val vd = matchResult.offsets[i3].rotate(-ctx.actives[i3].tx.angle)
      //  Apply formation difference to first movement of XML path
      if (vd.length > 0.1)
        p.skewFirst(-vd.x,-vd.y)
      //  Add XML path to dancer
      ctx.actives[i3].path.add(p)
      //  Move dancer to end so any subsequent modifications (e.g. roll)
      //  use the new position
      ctx.actives[i3].animateToEnd()
    }

    //  Mark dancers that had no XML move as inactive
    //  Needed for post-call modifications e.g. spread
    var inactives = emptyArray<Dancer>()
    this.xmlmap.forEachIndexed { m,i4 ->
      if (allp[m shr 1].movelist.count() == 0)
      inactives += ctx.actives[i4]
    }
    inactives.forEach { d ->  d.data.active = false  }

    ctx.extendPaths()
    ctx.analyze()
  }

  override fun postProcess(ctx: CallContext, i: Int) {
    super.postProcess(ctx, i)
    //  If just this one call then assume it knows what
    //  the ending formation should be
    if (i > 0)
      ctx.matchStandardFormation()
  }

}