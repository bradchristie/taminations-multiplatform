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

import com.bradchristie.taminations.platform.*
import kotlin.math.PI
import kotlin.math.atan2

object Gender {
  const val BOY = 1
  const val GIRL = 2
  const val PHANTOM = 3
  const val NONE = 4   // for concepts with abstract dancers
}

//  Additional data for each dancer for use by sequencer
data class DancerData(
    var active:Boolean = true,
    var beau:Boolean = false,
    var belle:Boolean = false,
    var leader:Boolean = false,
    var trailer:Boolean = false,
    var center:Boolean = false,
    var verycenter:Boolean = false,
    var end:Boolean = false,
    var partner: Dancer? = null,
    var actionBeats:Double = 0.0  // needed for moves that analyze previous action, like Roll
)

/**
 *     Constructor for a new dancer
 * @param number    Number to show when Number display is on
 * @param number_couple  Number to show when Couples Number display is on
 * @param gender    Gender - boy, girl, phantom
 * @param fillcolor    Base color
 * @param mat  Transform for dancer's start position
 * @param geom  Square, Bigon, Hexagon
 * @param moves   List of Movements for dancer's path
 */

open class Dancer(val number:String, val number_couple:String, val gender:Int,
                  var fillcolor: Color, mat: Matrix, private val geom: Geometry, val moves:List<Movement>,
                  val clonedFrom: Dancer? = null)
  : Comparable<Dancer> {

  companion object {
    const val NUMBERS_OFF = 0
    const val NUMBERS_DANCERS = 1
    const val NUMBERS_COUPLES = 2
    const val NUMBER_NAMES = 3    //  sequencer only
    val rect = Rect(-0.5,-0.5,1.0,1.0)
  }

  open val drawColor get() = fillcolor.darker()
  var showNumber = NUMBERS_OFF
  var showColor = true
  var showShape = true
  var hidden = false
  var starttx = geom.startMatrix(mat)  // only changed by sequencer
  var path = Path(moves)  // only changed by sequencer
  var showPath = false
  var hands = Hands.NOHANDS
  // Compute points of path for drawing path
  var tx = Matrix()
  private val pathpath = DrawingPath()  //android.graphics.Path()
  val beats:Double get() = path.beats
  //  Other vars for computing handholds
  var leftdancer: Dancer? = null
  var rightdancer: Dancer? = null
  var rightgrip: Dancer? = null
  var leftgrip: Dancer? = null
  var rightHandVisibility = false
  var leftHandVisibility = false
  var rightHandNewVisibility = false
  var leftHandNewVisibility = false
  val data = DancerData()  // for sequencer
  var name = ""  // for sequencer

  constructor(from: Dancer,
              number:String=from.number,
              number_couple:String=from.number_couple,
              gender:Int=from.gender)
      : this(number,number_couple,gender,from.fillcolor,from.tx,
      //  Already geometrically rotated so don't do it again
      Geometry(from.geom.geometry, 0),listOf<Movement>(),from) {
    //  For the sequencer, copy dancer data
    data.active = from.data.active
  }


  init {
    // Compute points of path for drawing path
    animateComputed(0.0)
    var loc = location
    pathpath.moveTo(loc.x,loc.y)
    for (beat10 in 1..beats.i*10) {
      animateComputed(beat10.d/10.0)
      loc = location
      pathpath.lineTo(loc.x,loc.y)
    }
    //  Restore dancer to start position
    animateComputed(-2.0)
  }

  override fun equals(other: Any?): Boolean {
    if (other is Dancer) {
      return other.number == number
    }
    return false
  }

  override fun compareTo(other: Dancer): Int = number.compareTo(other.number)

  override fun toString() = number

  val isPhantom:Boolean get() = gender == Gender.PHANTOM

  val location: Vector get() = tx.location

  //  distance to another dancer
  fun distanceTo(d2:Dancer):Double = (location - d2.location).length

  //  angle the dancer is facing relative to the positive x-axis
  val angleFacing:Double get() = tx.angle

  //  angle of the dancer's position relative to the positive x-axis
  val anglePosition:Double get() = tx.location.angle

  //  angle the dancer turns to look at the origin
  val angleToOrigin:Double get() = Vector().preConcatenate(tx.inverse()).angle

  fun vectorToDancer(d2:Dancer):Vector =
      d2.location.concatenate(tx.inverse())
  //  Angle of d2 as viewed from this dancer
  //  If angle is 0 then d2 is in front
  //  Angle returned is in the range -pi to pi
  fun angleToDancer(d2: Dancer):Double =
      vectorToDancer(d2).angle

  //  Other geometric interrogatives
  val isFacingIn : Boolean get() {
    val a: Double = angleToOrigin.abs
    return !a.isApprox(PI / 2) && a < PI / 2
  }

  val isFacingOut: Boolean get() {
    val a: Double = angleToOrigin.abs
    return !a.isApprox(PI / 2) && a > PI / 2
  }

  val isCenterLeft : Boolean get() {
    return angleToOrigin > 0
  }
  val isCenterRight : Boolean get() {
    return angleToOrigin < 0
  }

  val isOnXAxis : Boolean get() {
    return location.y isAbout 0.0
  }

  val isOnYAxis : Boolean get() {
    return location.x isAbout 0.0
  }

  val isOnAxis : Boolean get() {
    return isOnXAxis || isOnYAxis
  }

  val isTidal : Boolean get() =
    (isOnXAxis || isOnYAxis) && (isCenterLeft || isCenterRight)

  infix fun isInFrontOf(d2:Dancer) : Boolean =
    this != d2 && d2.angleToDancer(this).angleEquals(0.0)

  infix fun isInBackOf(d2:Dancer) : Boolean =
    this != d2 && d2.angleToDancer(this).angleEquals(PI)

  infix fun isRightOf(d2:Dancer) : Boolean =
    this != d2 && d2.angleToDancer(this).angleEquals(PI*3/2)

  infix fun isLeftOf(d2:Dancer) : Boolean =
    this != d2 && d2.angleToDancer(this).angleEquals(PI/2)

  infix fun isOpposite(d2:Dancer) : Boolean =
    this != d2 && (location + d2.location).length.isApprox(0.0)


  /**
   *   Used for hexagon handholds
   * @return  True if dancer is close enough to center to make a center star
   */
  val inCenter:Boolean get() = location.length < 1.1

  /**
   *   Move dancer to location along path
   * @param beat where to place dancer
   */
  private fun animateComputed(beat:Double) {
    hands = path.hands(beat)
    tx = starttx * path.animate(beat)
    tx = geom.pathMatrix(starttx,tx,beat) * tx
  }
  fun animateToEnd() = animate(beats)

  open fun animate(beat:Double) = animateComputed(beat)

  fun setStartPosition(x:Double,y:Double) {
    val a = angleFacing
    starttx = Matrix.getTranslation(x,y) * Matrix.getRotation(a)
    tx = Matrix(starttx)
  }

  fun rotateStartAngle(angle:Double) {
    starttx = starttx.preRotate(angle.toRadians)
    tx = Matrix(starttx)
  }

  /**
   *   Draw the entire dancer's path as a translucent colored line
   * @param c  Canvas to draw to
   */
  fun drawPath(c: DrawingContext) {
    //  The path color is a partly transparent version of the draw color
    c.drawPath(pathpath,DrawingStyle(drawColor,alpha=0.5,lineWidth=0.1))
  }

  //  Draw the dancer at its current position
  fun draw(c:DrawingContext) {
    val dc = if (showColor) drawColor else Color.GRAY
    val fc = if (showColor) fillcolor else Color.LIGHTGRAY
    //  Draw the head
    val p = DrawingStyle(color=dc)
    c.fillCircle(0.5,0.0,0.33,p)
    //  Draw the body
    p.color = if (showNumber == NUMBERS_OFF || gender == Gender.PHANTOM)
      fc else fc.veryBright()
    when (if (showShape) gender else Gender.PHANTOM) {
      Gender.BOY -> c.fillRect(rect,p)
      Gender.GIRL -> c.fillCircle(0.0,0.0,0.5,p)
      else -> c.fillRoundRect(rect,0.3, p)
    }
    //  Draw the body outline
    p.lineWidth = 0.1
    p.color = dc
    when (if (showShape) gender else Gender.PHANTOM) {
      Gender.BOY -> c.drawRect(rect,p)
      Gender.GIRL -> c.drawCircle(0.0,0.0,.5,p)
      else -> c.drawRoundRect(rect,0.3,p)
    }
    //  Draw number if on
    if (showNumber != NUMBERS_OFF) {
      //  The dancer is rotated relative to the display, but of course
      //  the dancer number should not be rotated.
      //  So the number needs to be transformed back
      val angle = atan2(tx.m12,tx.m22)
      val txtext = Matrix().postRotate(-angle+PI/2)
      c.transform(txtext)
      c.scale(-0.1,0.1)
      val textSize = 7.0
      c.fillText(when (showNumber) {
        NUMBERS_DANCERS -> number
        NUMBERS_COUPLES -> number_couple
        NUMBER_NAMES -> name
        else -> ""  // should not happen
      }, -textSize*0.25, textSize*0.4,DrawingStyle(textSize=textSize))
    }

  }


}