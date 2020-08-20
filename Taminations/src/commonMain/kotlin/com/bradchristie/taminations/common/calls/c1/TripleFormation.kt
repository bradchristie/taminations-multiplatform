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
import com.bradchristie.taminations.common.calls.Action

abstract class TripleFormation(norm: String, name: String) : Action(norm, name) {

  companion object {

    val tripleBoxFormations = mapOf(
        "Triple Boxes" to 1.0,
        "Triple Boxes 2" to 1.0,
        "Triple Lines" to 1.0,
        "Triple Columns" to 1.0
    )
  }

  override val level = LevelObject("c1")

  open var isXaxis = false
  abstract fun isXaxis(ctx:CallContext):Boolean
  protected val Vector.major:Double get() =
    if (isXaxis) this.x else this.y
  protected val Vector.minor:Double get() =
    if (isXaxis) this.y else this.x

  abstract fun majorValues(ctx:CallContext) : List<Double>
  abstract fun minorValues(ctx:CallContext) : List<Double>
  abstract fun tripleFormations(ctx:CallContext) : List<CallContext>
  private val subcall = name.replaceFirst("Triple (Box|Lines?|Waves?|Columns?) ".ri,"")

  override fun perform(ctx: CallContext, i: Int) {
    isXaxis = isXaxis(ctx)
    //  Add phantoms in spots not occupied by dancers
    val phantoms = mutableListOf<Dancer>()
    majorValues(ctx).forEach { c1 ->
      minorValues(ctx).forEach { c2 ->
        val v = if (isXaxis)
          Vector(c1, c2)
        else
          Vector(c2, c1)
        if (ctx.dancerAt(v) == null)
          phantoms += Dancer(
              ctx.dancers.first(),
              gender = Gender.PHANTOM,
              number = "P${phantoms.count() + 1}"
          ).setStartPosition(v)
      }
    }
    //  Make the three boxes
    val tripleBoxCtx = CallContext(ctx, ctx.dancers + phantoms)
    val tripleContexts = tripleFormations(tripleBoxCtx)
    //  Apply call to each box
    tripleContexts.forEach { box ->
      if (box.dancers.count() != 4)
        throw CallError("Error splitting into groups - group has ${box.dancers.count()} dancers.")
      box.analyze()
      box.rotatePhantoms(subcall, rotate = 90, asym = true)?.also { rotbox ->
        rotbox.applyCalls(subcall)
        //  If it ends in a bax, make it a compact box in major direction
        //  so it will fit with others to make a triple box
        if (rotbox.isBox() && rotbox.dancers.any {
              it.location.major.abs.isGreaterThan(1.0)
            })
          rotbox.adjustToFormation("Facing Couples Close",rotate = 90)
        //  Now apply the result to the 12-dancer triple box context
        rotbox.appendToSource()
        box.appendToSource()
      }
        ?: throw CallError("Unable to do $subcall with these Triple Boxes")
    }
    tripleBoxCtx.animateToEnd()
    tripleBoxCtx.matchFormationList(tripleBoxFormations)
    tripleBoxCtx.dancers.filter { it.gender != Gender.PHANTOM }.forEach { bd ->
      ctx.dancers.find { it == bd }!!.path.add(bd.path)
    }
    ctx.noSnap()
  }

}


//  This class is for Triple Box only,
//  which is 12 spots arranged in a 2 x 6
class TripleBoxConcept(norm: String, name: String)
  : TripleFormation(norm, name) {

  override fun majorValues(ctx:CallContext) = listOf(-5.0, -3.0, -1.0, 1.0, 3.0, 5.0)
  override fun minorValues(ctx:CallContext) =
      listOf(-ctx.dancers[0].location.minor,ctx.dancers[0].location.minor)

  override fun tripleFormations(ctx: CallContext): List<CallContext> =
      listOf(
          CallContext(ctx,
              ctx.dancers.filter {
                it.location.major.isGreaterThan(1.0)
              }),
          CallContext(ctx,
              ctx.dancers.filter {
                it.location.major.abs.isLessThan(3.0)
              }),
          CallContext(ctx,
              ctx.dancers.filter {
                it.location.major.isLessThan(-1.0)
              })
      )

  override fun isXaxis(ctx: CallContext) : Boolean {
    val maxX = ctx.dancers.map { it.location.x }.maxOrNull()!!
    val maxY = ctx.dancers.map { it.location.y }.maxOrNull()!!
    return maxX > maxY
  }

}

class TripleLineConcept(norm: String, name: String)
  : TripleFormation(norm, name) {

  override fun majorValues(ctx: CallContext): List<Double> = listOf(
      ctx.dancers.map { it.location.major }.minOrNull()!!,
      0.0,
      ctx.dancers.map { it.location.major }.maxOrNull()!!
  )

  override fun minorValues(ctx: CallContext): List<Double> =
      listOf(-3.0,-1.0,1.0,3.0)

  override fun tripleFormations(ctx: CallContext): List<CallContext> {
    val xVal = ctx.dancers.map { it.location.major }.maxOrNull()!!
    return listOf(
        CallContext(ctx,ctx.dancers.filter { it.location.major.isAbout(-xVal) }),
        CallContext(ctx,ctx.dancers.filter { it.location.major.isAbout(0.0) }),
        CallContext(ctx,ctx.dancers.filter { it.location.major.isAbout(xVal) })
    )
  }

  override fun isXaxis(ctx: CallContext):Boolean {
    //  Triple line/wave/column - will have 4 different
    //  dancer coordinates only in axis parallel to lines
    //  Be careful about coords at 2.5, which are used often
    //  and could round up or down.
    val xGroups = ctx.dancers.groupBy { d -> (d.location.x + 0.1).round.i }
    val yGroups = ctx.dancers.groupBy { d -> (d.location.y + 0.1).round.i }
    //  isXaxis is true if there are 3 lines at x == 0 and x +/- 2 or 3
    return if (xGroups.count() == 4 && yGroups.count() < 4)
      false
    else if (xGroups.count() < 4 && yGroups.count() == 4)
      true
    else
      throw CallError("Unable to find Triple Lines/Waves/Columns")
  }


}