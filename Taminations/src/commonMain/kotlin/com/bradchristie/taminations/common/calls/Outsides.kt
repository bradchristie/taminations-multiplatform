package com.bradchristie.taminations.common.calls
/*

  Taminations Square Dance Animations for Web Browsers
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
import com.bradchristie.taminations.common.r

class Outsides(name:String) : CodedCall(name) {

  override fun performCall(ctx: CallContext, i: Int) {
    val num = when (name) {
      in ".* 2|two".r -> 2
      in ".* 4|four".r -> 4
      in ".* 6|six".r -> 6
      in "points".r -> 4
      else -> 4
    }
    ctx.dancers.sortedBy{d -> -d.location.length}.drop(num).forEach {
      it.data.active = false
    }
  }

}