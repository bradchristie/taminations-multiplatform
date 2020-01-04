package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.platform.Setting
import kotlin.math.atan2

class InteractiveDancer(number: String, number_couple: String,
                        gender: Int, fillcolor: Color,
                        mat: Matrix, geom: Geometry,
                        moves: List<Movement>)
  : Dancer(number, number_couple, gender, fillcolor, mat, geom, moves, null) {

  companion object {
    const val ANGLESENSITIVITY = 0.5
    const val MOVESENSITIVITY = 1.0
    const val DIRECTIONALPHA = 0.9
    const val DIRECTIONTHRESHOLD = 0.002
    val NOPOINT = Vector()
    val NODIRECTION = Vector()
  }

  var onTrack = true

  //  For moving dancer with mouse and keys
  private var shiftDown = false
  private var ctlDown = false
  private var primaryDirection = NODIRECTION
  private var mouseIsDown = false

  //  For moving dancer with fingers
  private var primaryid = -1
  private var secondaryid = -1
  private var primaryTouch = NOPOINT
  private var primaryMove = NOPOINT
  private var secondaryTouch = NOPOINT
  private var secondaryMove = NOPOINT
  private val primaryIsLeft = Setting("PrimaryControl").s == "Left"

  //  Need a val for original fill color, as we change it
  private val onTrackColor = fillcolor
  override val drawColor = onTrackColor.darker()

  fun computeMatrix(beat:Double): Matrix {
    val savetx = Matrix(tx)
    super.animate(beat)
    val computetx = Matrix(tx)
    tx = savetx
    return computetx
  }

  override fun animate(beat: Double) {
    fillcolor = if (beat <= 0.0 || onTrack)
      onTrackColor.veryBright()
    else
      Color.GRAY
    if (beat <= -1.0) {
      tx = Matrix(starttx)
      primaryTouch = Vector()
      primaryMove = Vector()
    } else { //   if (mouseIsDown == (Setting("PracticeMousePressed").b != false)) {

      if (primaryMove != NOPOINT) {
        val d = (primaryMove - primaryTouch) * MOVESENSITIVITY
        if (!ctlDown) {
          tx = tx.postTranslate(d.x, d.y)
          if (!shiftDown && secondaryMove == NOPOINT) {
            //  Rotation follow movement
            if (primaryDirection == Vector())
              primaryDirection = d
            else {
              val dd = Vector(
                  //  this smooths the rotation
                  DIRECTIONALPHA * primaryDirection.x + (1 - DIRECTIONALPHA) * d.x,
                  DIRECTIONALPHA * primaryDirection.y + (1 - DIRECTIONALPHA) * d.y)
              if (dd.length >= DIRECTIONTHRESHOLD) {
                val a1 = tx.angle
                val a2 = atan2(dd.y, dd.x)
                tx = tx.preRotate(a2 - a1)
                primaryDirection = dd
              }
            }
          }
        } else {
          //  Control key pressed - mouse controls rotation only
          val a = primaryMove - tx.location
          val z = a.crossZ(d)
          tx = tx.preRotate(z* ANGLESENSITIVITY / a.length)
        }
        primaryTouch = primaryMove
      }

      if (secondaryMove != NOPOINT) {
        //  Rotation follow right finger
        //  Get the vector of the user's finger
        val dx = -(secondaryMove.x - secondaryTouch.x) * ANGLESENSITIVITY
        val dy = (secondaryMove.y - secondaryTouch.y) * ANGLESENSITIVITY
        val vf = Vector(dx,dy)
        //  Get the vector the dancer is facing
        val vu = Matrix(tx).direction
        //  Amount of rotation is z of the cross product of the two
        val da = vu.crossZ(vf)
        tx = tx.preRotate(da)
        secondaryTouch = secondaryMove
      }

    }
  }

  fun touchDown(id:Int, x:Double, y:Double) {
    if (Setting("PracticeMousePressed").b != false || Application.isTouch)
      touchDownAction(id,x,y)
    else
      touchUpAction(id)
  }
  private fun touchDownAction(id:Int, x:Double, y:Double) {
    //  Figure out if touching left or right side, and remember the point
    //  Also need to remember the "id" to correlate future move events
    //  Point has already been transformed to dancer coords
    if ((y < 0) xor primaryIsLeft || !Application.isTouch) {
      primaryTouch = Vector(x,y)
      primaryMove = primaryTouch
      primaryid = id
      mouseIsDown = true
    } else {
      secondaryTouch = Vector(x,y)
      secondaryMove = secondaryTouch
      secondaryid = id
    }
  }

  fun touchUp(id:Int,x:Double,y:Double) {
    if (Setting("PracticeMousePressed").b != false || Application.isTouch)
      touchUpAction(id)
    else
      touchDownAction(id,x,y)
  }
  private fun touchUpAction(id:Int) {
    if (id == primaryid) {
      primaryTouch = NOPOINT
      primaryMove = NOPOINT
      primaryid = -1
    } else if (id == secondaryid) {
      secondaryTouch = NOPOINT
      secondaryMove = NOPOINT
      secondaryid = -1
    }
    mouseIsDown = false
  }

  fun touchMove(id:Int, x:Double, y:Double) {
    if (id == primaryid)
      primaryMove = Vector(x, y)
    else if (id == secondaryid)
      secondaryMove = Vector(x, y)
  }

  fun keyDown(key:Int) {
    when (key) {
      16 -> shiftDown = true
      17 -> ctlDown = true
    }
  }

  fun keyUp(key:Int) {
    when (key) {
      16 -> shiftDown = false
      17 -> ctlDown = false
    }
  }



}