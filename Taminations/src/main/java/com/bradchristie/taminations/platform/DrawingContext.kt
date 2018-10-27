package com.bradchristie.taminations.platform
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

import android.graphics.Paint
import com.bradchristie.taminations.common.Matrix
import com.bradchristie.taminations.common.f
import com.bradchristie.taminations.common.i
import kotlin.math.PI

private val Rect.rectF:android.graphics.RectF get() =
      android.graphics.RectF(x.f,y.f,(x+w).f,(y+h).f)

actual enum class TextAlign(val align:Paint.Align) {
  LEFT(Paint.Align.LEFT),
  CENTER(Paint.Align.CENTER),
  RIGHT(Paint.Align.RIGHT)
}

  private val DrawingStyle.paint:Paint get() = Paint().also {
    it.color = color.a
    it.alpha = (alpha * 255.0).i
    it.textAlign = textAlign.align
    it.textSize = textSize.f
    it.strokeWidth = lineWidth.f
    it.isAntiAlias = true
  }
  val DrawingStyle.draw:Paint get() = paint.apply { style=Paint.Style.STROKE }
  val DrawingStyle.fill:Paint get() = paint.apply { style=Paint.Style.FILL }

actual class DrawingPath actual constructor() {
  val p = android.graphics.Path()
  actual fun moveTo(x: Double, y: Double) {
    p.moveTo(x.f,y.f)
  }
  actual fun lineTo(x: Double, y: Double) {
    p.lineTo(x.f,y.f)
  }
  actual fun arc(x: Double, y: Double, radius: Double, startAngle: Double, endAngle: Double) {
    p.addArc(Rect(x-radius,y-radius,radius*2,radius*2).rectF,
      (startAngle*180.0/PI).f,((endAngle-startAngle)*180.0/PI).f)
  }
  actual fun close() {
    p.close()
  }
}

actual class DrawingContext(val canvas:android.graphics.Canvas) {

  actual val height:Int = canvas.height
  actual val width:Int = canvas.width

  actual fun save() {
    canvas.save()
  }
  actual fun restore() {
    canvas.restore()
  }

  actual fun scale(x:Double, y:Double) {
    canvas.scale(x.f,y.f)
  }
  actual fun rotate(r:Double) {
    //  Android Canvas rotate is in degrees (!)
    canvas.rotate((r*180.0/PI).f)
  }
  actual fun translate(x: Double, y: Double) {
    canvas.translate(x.f, y.f)
  }
  actual fun transform(m:Matrix) {
    val am = android.graphics.Matrix()
    am.setValues(floatArrayOf(m.m11.f,m.m21.f,m.m31.f,m.m12.f,m.m22.f,m.m32.f,0f,0f,1f))
    canvas.concat(am)
  }

  actual fun drawRect(rect: Rect, p:DrawingStyle) {
    canvas.drawRect(rect.rectF,p.draw)
  }
  actual fun fillRect(rect: Rect, p:DrawingStyle) {
    canvas.drawRect(rect.rectF,p.fill)
  }

  actual fun drawRoundRect(rect: Rect, rad:Double, p:DrawingStyle) {
    canvas.drawRoundRect(rect.rectF,rad.f,rad.f,p.draw)
  }
  actual fun fillRoundRect(rect: Rect, rad:Double, p:DrawingStyle) {
    canvas.drawRoundRect(rect.rectF,rad.f,rad.f,p.fill)
  }


  actual fun drawCircle(x:Double, y:Double, radius:Double, p:DrawingStyle) {
    canvas.drawCircle(x.f,y.f,radius.f,p.draw)
  }

  actual fun fillCircle(x:Double, y:Double, radius:Double, p:DrawingStyle) {
    canvas.drawCircle(x.f,y.f,radius.f,p.fill)
  }

  actual fun fillText(text:String, x:Double, y:Double, p:DrawingStyle) {
    canvas.drawText(text,x.f,y.f,p.fill)
  }

  actual fun drawLine(x1:Double, y1:Double, x2:Double, y2:Double, p:DrawingStyle) {
    canvas.drawLine(x1.f,y1.f,x2.f,y2.f,p.draw)
  }

  actual fun drawPath(path:DrawingPath, p:DrawingStyle) {
    canvas.drawPath(path.p,p.draw)
  }
  actual fun fillPath(path:DrawingPath, p:DrawingStyle) {
    canvas.drawPath(path.p,p.fill)
  }

}