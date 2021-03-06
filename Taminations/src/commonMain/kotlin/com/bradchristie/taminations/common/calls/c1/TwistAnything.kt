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

import com.bradchristie.taminations.common.CallContext
import com.bradchristie.taminations.common.CallError
import com.bradchristie.taminations.common.LevelObject
import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.r

class TwistAnything(norm:String, name:String) : Action(norm,name) {

  override val level = LevelObject("c1")

  override fun perform(ctx: CallContext, i: Int) {
    //  Get "Anything" call
    val anycall = if (norm == "twisttheline") "Star Thru" else
      //  Be careful to allow e.g. "Twist and Right and Left Thru"
      name.replaceFirst(".*? and ".r,"")
    //  Centers facing out or in?
    when {
      ctx.center(4).all { it.isFacingOut }
        //  This is for centers facing out
        -> try {
        //  First try original centers do the any call
        ctx.subContext {
          applyCalls(
              "Outer 4 Face In and Step while Center 4 Step Ahead",
              "Outer 4 Trade while Center 4 $anycall"
          )
        }
      } catch (e:CallError) {
        //  If that didn't work, try everybody do the any call
        ctx.applyCalls(
            "Outer 4 Face In and Step while Center 4 Step Ahead",
            "Outer 4 Trade"
        )
        ctx.matchStandardFormation()
        ctx.applyCalls(anycall)
      }
      ctx.center(4).all { it.isFacingIn }
        //  Centers facing in
        -> try {
        ctx.subContext {
          applyCalls(
              "Outer 4 Face In and Step while Center 4 Half Step Ahead",
              "Center 4 Trade while Outer 4 $anycall"
          )
        }
      } catch (e:CallError) {
        ctx.applyCalls(
            "Outer 4 Face In and Step while Center 4 Half Step Ahead",
            "Center 4 Trade"
        )
        ctx.matchStandardFormation()
        ctx.applyCalls(anycall)
      }
      else
        -> throw CallError("Centers must face the same direction")
    }
  }
}