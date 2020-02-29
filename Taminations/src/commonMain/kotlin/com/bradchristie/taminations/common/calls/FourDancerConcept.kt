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

//  This is a base class for concept calls that group or select
//  dancers in a way that they perform a 4-dancer call.
//  The concept maps the 4-dancer call to the real dancers.
//  Primary examples are As Couples Concept and Tandem Concept
abstract class FourDancerConcept(norm:String,name:String=norm) : Action(norm,name) {

  protected open val conceptName = ""

  //  Return list of groups of dancers
  //  List must have 4 sub-lists
  //  Each sub-list has 2 or more real (or phantom) dancers
  //  For example, the groups for As Couples are the 4 couples
  protected abstract fun dancerGroups(ctx:CallContext) : List<List<Dancer>>

  //  Return start position of concept dancer for one group
  protected abstract fun startPosition(group:List<Dancer>) : Vector

  //  Compute location for a real dancer at a specific beat
  //  given location of the concept dancer
  protected abstract fun computeLocation(d:Dancer, m: Movement, beat:Double, groupIndex:Int) : Vector

  protected open fun postAdjustment(ctx:CallContext,cd:Dancer, group:List<Dancer>) { }

  override fun perform(ctx: CallContext, i: Int) {
    //  Get dancer groups
    val groups = dancerGroups(ctx)
    //  Create a concept dancer for each group dancer
    val singles = groups.map { group ->
      //  Select the gender for the concept dancer
      val g = when {
        group.all { it.gender == Gender.BOY} -> Gender.BOY
        group.all { it.gender == Gender.GIRL} -> Gender.GIRL
        else -> Gender.NONE
      }
      //  Select the couple number for the concept dancer
      //  Needed for e.g. <concept> Heads Run
      val nc = when {
        group.all { it.number_couple.matches("[13]{2}".r)} -> "1"
        group.all { it.number_couple.matches("[24]{2}".r)} -> "2"
        else -> "0"
      }
      //  Create the concept dancer
      val dsingle = Dancer(group.first(), gender = g, number_couple = nc)
      //  Set the location for the concept dancer
      val newpos = startPosition(group)
      dsingle.setStartPosition(newpos.x,newpos.y)
      dsingle
    }

    //  Create context for concept dancers
    val conceptctx = CallContext(singles.toTypedArray())
    //  And apply the call
    conceptctx.applyCalls(name.replace("$conceptName ".ri,""))

    //  Get the paths and apply to the original dancers
    conceptctx.dancers.forEachIndexed { ci,cd ->
      val group = groups[ci]
      //  Compute movement for each real dancer for each movement
      //  based on the concept dancer
      var cdbeat = 0.0
      cd.path.movelist.forEachIndexed { i, m ->
        group.forEachIndexed { gi,d ->
          conceptctx.animate(cdbeat)
          //  Get the 4 points needed to compute Bezier curve
          val p1 = if (i==0) (d.location - cd.location).rotate(-cd.angleFacing)
                   else computeLocation(cd,m,0.0,gi)
          val p2 = computeLocation(cd,m,m.beats / 3.0,gi) - p1
          val p3 = computeLocation(cd,m,m.beats * 2.0 / 3.0,gi) - p1
          val p4 = computeLocation(cd,m,m.beats,gi) - p1
          //  Now we can compute the Bezier
          val cb = Bezier.fromPoints(Vector(), p2, p3, p4)
          //  And use it to build the Movement
          val cm = Movement(m.beats,m.hands,cb, m.brotate)
          //  And add the Movement to the Path
          d.path.add(cm)
        }

        cdbeat += m.beats
      }
    }
    //  Let inherited classes make any adjustments
    ctx.animateToEnd()
    conceptctx.dancers.forEachIndexed { ci,cd ->
      postAdjustment(ctx,cd,groups[ci])
    }
  }

}