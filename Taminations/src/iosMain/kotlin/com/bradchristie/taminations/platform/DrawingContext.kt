package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
  Copyright (C) 2019 Brad Christie

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

import com.bradchristie.taminations.common.Matrix

actual enum class TextAlign {
  LEFT,
  CENTER,
  RIGHT,
}

actual class DrawingContext {
  actual val width: Int
    get() = TODO("not implemented")
  actual val height: Int
    get() = TODO("not implemented")

  actual fun save() {}
  actual fun restore() {}
  actual fun scale(x: Double, y: Double) {}
  actual fun rotate(r: Double) {}
  actual fun translate(x: Double, y: Double) {}
  actual fun drawCircle(x: Double, y: Double, radius: Double, p: DrawingStyle) {}
  actual fun fillCircle(x: Double, y: Double, radius: Double, p: DrawingStyle) {}
  actual fun drawRect(rect: Rect, p: DrawingStyle) {}
  actual fun fillRect(rect: Rect, p: DrawingStyle) {}
  actual fun drawRoundRect(rect: Rect, rad: Double, p: DrawingStyle) {}
  actual fun fillRoundRect(rect: Rect, rad: Double, p: DrawingStyle) {}
  actual fun transform(m: Matrix) {}
  actual fun fillText(text: String, x: Double, y: Double, p: DrawingStyle) {}
  actual fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double, p: DrawingStyle) {}
  actual fun drawPath(path: DrawingPath, p: DrawingStyle) {}
  actual fun fillPath(path: DrawingPath, p: DrawingStyle) {}

}

actual class DrawingPath actual constructor() {
  actual fun moveTo(x: Double, y: Double) {}
  actual fun lineTo(x: Double, y: Double) {}
  actual fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {}
  actual fun close() {}
}
