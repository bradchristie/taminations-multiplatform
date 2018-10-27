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

import com.bradchristie.taminations.common.Matrix
import com.bradchristie.taminations.common.f
import org.w3c.dom.*
import kotlin.math.PI

actual enum class TextAlign(val align:CanvasTextAlign) : CanvasTextAlign {
  LEFT(CanvasTextAlign.LEFT),
  CENTER(CanvasTextAlign.CENTER),
  RIGHT(CanvasTextAlign.RIGHT)
}


//actual typealias DrawingPath = Path2D
actual class DrawingPath actual constructor() {
  val p = Path2D()
  actual fun moveTo(x: Double, y: Double) {
    p.moveTo(x,y)
  }
  actual fun lineTo(x: Double, y: Double) {
    p.lineTo(x,y)
  }
  actual fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {
    p.arc(x,y,radius, startAngle, endAngle)
  }
  actual fun close() { p.closePath() }
}

actual class DrawingContext(val ctx:CanvasRenderingContext2D) {

  actual val width get() = ctx.canvas.clientWidth
  actual val height get() = ctx.canvas.clientHeight

  actual fun save() {
    ctx.save()
  }
  actual fun restore() {
    ctx.restore()
  }

  actual fun scale(x:Double, y:Double) {
    ctx.scale(x,y)
  }

  actual fun rotate(r:Double) {
    ctx.rotate(r)
  }
  actual fun translate(x: Double, y: Double) {
    ctx.translate(x, y)
  }

  actual fun drawCircle(x: Double, y: Double, radius: Double, p: DrawingStyle) {
    ctx.strokeStyle = p.color.css
    ctx.lineWidth = p.lineWidth
    ctx.beginPath()
    ctx.ellipse(x, y, radius, radius, 0.0, 0.0, 2 * PI)
    ctx.stroke()
  }

  actual fun fillCircle(x: Double, y: Double, radius: Double, p: DrawingStyle) {
    ctx.fillStyle = p.color.css
    ctx.beginPath()
    ctx.ellipse(x, y, radius, radius, 0.0, 0.0, 2 * PI)
    ctx.fill()
  }

  actual fun drawRect(rect: Rect, p: DrawingStyle) {
    ctx.strokeStyle = p.color.css
    ctx.lineWidth = p.lineWidth
    ctx.strokeRect(rect.x, rect.y, rect.w, rect.h)
  }

  actual fun fillRect(rect: Rect, p: DrawingStyle) {
    ctx.fillStyle = p.color.css
    ctx.fillRect(rect.x, rect.y, rect.w, rect.h)
  }

  //  Canvas doesn't have a rounded rect intrinsic
//  so we have to make it step by step
  private fun roundRect(rect: Rect, rad: Double, p: DrawingStyle) {
    ctx.beginPath()
    ctx.lineWidth = p.lineWidth
    ctx.moveTo(rect.x + rad, rect.y)
    ctx.lineTo(rect.x + rect.w - rad, rect.y)
    ctx.arc(rect.x + rect.w - rad, rect.y + rad, rad, PI * 3 / 2, 0.0)
    ctx.lineTo(rect.x + rect.w, rect.y + rect.h - rad)
    ctx.arc(rect.x + rect.w - rad, rect.y + rect.h - rad, rad, 0.0, PI / 2.0)
    ctx.lineTo(rect.x + rad, rect.y + rect.h)
    ctx.arc(rect.x + rad, rect.y + rect.h - rad, rad, PI / 2.0, PI)
    ctx.lineTo(rect.x, rect.y + rad)
    ctx.arc(rect.x + rad, rect.y + rad, rad, PI, PI * 3 / 2)
    ctx.closePath()
  }

  actual fun drawRoundRect(rect: Rect, rad: Double, p: DrawingStyle) {
    ctx.strokeStyle = p.color.css
    roundRect(rect, rad, p)
    ctx.stroke()
  }

  actual fun fillRoundRect(rect: Rect, rad: Double, p: DrawingStyle) {
    ctx.fillStyle = p.color.css
    roundRect(rect, rad, p)
    ctx.fill()
  }

  actual fun transform(m: Matrix) =
    ctx.transform(m.m11, m.m12, m.m21, m.m22, m.m31, m.m32)

  actual fun fillText(text: String, x: Double, y: Double, p: DrawingStyle) {
    ctx.font = "${p.textSize}px ${p.fontFamily}"
    ctx.textAlign = p.textAlign
    ctx.fillStyle = p.color.css
    ctx.fillText(text, x, y)
  }

  actual fun drawLine(x1: Double, y1: Double, x2: Double, y2: Double, p: DrawingStyle) {
    ctx.beginPath()
    ctx.strokeStyle = p.color.css
    ctx.lineWidth = p.lineWidth
    ctx.moveTo(x1, y1)
    ctx.lineTo(x2, y2)
    ctx.stroke()
  }

  actual fun drawPath(path: DrawingPath, p: DrawingStyle) {
    ctx.strokeStyle = p.color.css
    ctx.lineWidth = p.lineWidth
    ctx.stroke(path.p)
  }

  actual fun fillPath(path: DrawingPath, p: DrawingStyle) {
    ctx.fillStyle = p.color.css
    ctx.fill(path.p)
  }

}