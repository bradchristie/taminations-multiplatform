package com.bradchristie.taminations.common.calls.ms
/*

  Taminations Square Dance Animations for Web Browsers
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
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.common.calls.Action

class Fold : Action("Fold") {

  override val level = LevelObject("ms")

  //  We need to work with all the dancers, not just actives
  //  because partners of the folders need to adjust
  //  so we get a standard formation that can be used for more calls
  override fun perform(ctx: CallContext, i: Int) {
    ctx.actives.forEach { d ->
      //  Find dancer to fold in front of
      //  Usually it's the partner
      val d2 = d.data.partner ?: throw CallError("Dancer ${d.number} has nobody to Fold in front")
      if (d2.data.active || d2.data.partner != d)
        throw CallError("Dancer ${d.number} has nobody to Fold in front")
      val m = if (d2 isRightOf d) "Fold Right" else "Fold Left"
      val dist = d.distanceTo(d2)
      val dxscale = 0.75
      val dyoffset = when {
        ctx.isTidal() -> 1.5
        d.data.end -> 2.0 - dist
        d.data.center -> 2.0
        else -> 1.0
      } * if (d2 isRightOf d) 1 else -1
      d.path += TamUtils.getMove(m).scale(dxscale, 1.0).skew(0.0, dyoffset)
      //  Also set path for partner
      val m2 = when {
        d isRightOf d2 -> "Dodge Right"
        d isLeftOf d2 -> "Dodge Left"
        else -> "Stand"  // should never happen
      }
      val myscale = when {
        ctx.isTidal() -> 0.25
        d2.data.end -> 0.5
        d2.data.center -> 0.0
        else -> 0.25
      }
      d2.path += TamUtils.getMove(m2).scale(1.0, dist * myscale)
    }

  }

}