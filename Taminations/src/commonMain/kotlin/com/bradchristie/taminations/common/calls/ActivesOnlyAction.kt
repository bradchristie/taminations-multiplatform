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

import com.bradchristie.taminations.common.CallContext

//  For most calls where only some dancers are selected, the other dancers
//  can be ignored.  Removing them from the context, and analyzing what is left,
//  often makes it easier to figure out how to perform the call.
abstract class ActivesOnlyAction(norm:String,name:String=norm) : Action(norm,name) {

  override fun perform(ctx: CallContext, i: Int) {
    if (ctx.actives.count() < ctx.dancers.count()) {
      ctx.subContext(ctx.actives) {
        analyze();
        perform(this,i);
      }
    } else
      super.perform(ctx, i)
  }

}
