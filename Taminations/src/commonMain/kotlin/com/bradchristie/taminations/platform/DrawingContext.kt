package com.bradchristie.taminations.platform
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

import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.common.Matrix

data class Rect(
    val x:Double,
    val y:Double,
    val w:Double,
    val h:Double
)

data class DrawingStyle(
    var color: Color = Color.BLACK,
    var alpha: Double = 1.0,
    var textSize: Double = 10.0,
    var textAlign: TextAlign = TextAlign.LEFT,
    var fontFamily: String = "sans-serif",
    var lineWidth: Double = 1.0
)

expect enum class TextAlign {
  LEFT,
  CENTER,
  RIGHT,
}

expect class DrawingContext {

  val width : Int
  val height : Int

  fun save()
  fun restore()
  fun scale(x:Double,y:Double)
  fun rotate(r:Double)
  fun translate(x: Double, y: Double)
  fun drawCircle(x:Double,y:Double,radius:Double,p:DrawingStyle)
  fun fillCircle(x:Double,y:Double,radius:Double,p:DrawingStyle)
  fun drawRect(rect: Rect, p:DrawingStyle)
  fun fillRect(rect: Rect, p:DrawingStyle)
  fun drawRoundRect(rect: Rect, rad:Double, p:DrawingStyle)
  fun fillRoundRect(rect: Rect, rad:Double, p:DrawingStyle)
  fun transform(m: Matrix)
  fun fillText(text:String,x:Double,y:Double,p:DrawingStyle)
  fun drawLine(x1:Double,y1:Double,x2:Double,y2:Double,p:DrawingStyle)
  fun drawPath(path:DrawingPath,p:DrawingStyle= DrawingStyle())
  fun fillPath(path:DrawingPath,p:DrawingStyle= DrawingStyle())
}

expect class DrawingPath() {
  fun moveTo(x:Double,y:Double)
  fun lineTo(x:Double,y:Double)
  fun arc(x:Double,y:Double,radius:Double,startAngle:Double,endAngle:Double)
  fun close()
}
