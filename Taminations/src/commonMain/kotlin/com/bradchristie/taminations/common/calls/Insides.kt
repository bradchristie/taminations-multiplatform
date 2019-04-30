package com.bradchristie.taminations.common.calls

/*

  Taminations Square Dance Animations
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

class Insides(norm:String, name:String) : CodedCall(norm,name) {

  override fun performCall(ctx: CallContext, i: Int) {
    val num = when (norm) {
      in ".*2".r -> 2
      in ".*4".r -> 4
      in ".*6".r -> 6
      else -> 4
    }
    ctx.dancers.sortedBy{d -> d.location.length}.drop(num).forEach {
      it.data.active = false
    }
  }

}