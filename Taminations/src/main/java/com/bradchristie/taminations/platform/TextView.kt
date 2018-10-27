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

import android.graphics.Canvas
import android.graphics.Typeface
import android.support.v7.widget.AppCompatTextView
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.view.Gravity
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.common.f
import com.bradchristie.taminations.common.i

actual open class TextView actual constructor(private var t:String) : View() {

  actual enum class Align {
    LEFT,
    CENTER,
    RIGHT
  }

  actual var autoSize:Boolean = false
  private var needsLayout: Boolean = false

  override val div = object : AppCompatTextView(Taminations.context) {
    private val textPaint = TextPaint()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
      needsLayout = true
    }
    override fun onDraw(canvas: Canvas?) {
      if (autoSize && needsLayout) {
        val w = width.px2dip
        textSize = (30 downTo 6).find { textSize ->
          textPaint.textSize = textSize.f
          val staticLayout = StaticLayout(text, textPaint, w,
              Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true)
          staticLayout.lineCount <= 3 && staticLayout.height < 60
        }?.f ?: 6f
        needsLayout = false
      }
      super.onDraw(canvas)
    }
  }.apply { text = t }

  actual var textStyle:String
    get() { throw UnsupportedOperationException() }
    set(value) { if (value=="bold") div.typeface = Typeface.DEFAULT_BOLD }
  //  textSize is in sp units
  actual var textSize:Int
    get() { throw UnsupportedOperationException() }
    set(value) { div.textSize = value.f }
  actual fun nowrap() {
    div.setSingleLine()
  }
  override var textColor: Color
    get() = Color(div.currentTextColor)  // not used?
    set(c) { div.setTextColor(c.a) }
  //  Text shadow is only used for the title bar
  actual fun shadow() {
    div.setShadowLayer(1.5f,2f,2f,0xc0000000.i)
  }

  actual var text:String
    get() = t
    set(value) {
      t = value
      div.text = t
      needsLayout = true
    }

  //  Text alignment - more often done by alignment of this view in its parent
  actual var align:Align
    get() = when (div.gravity) {
      Gravity.CENTER -> Align.CENTER
      Gravity.RIGHT -> Align.RIGHT
      else -> Align.LEFT
    }
    set(v) {
      div.gravity = when (v) {
        Align.LEFT -> Gravity.LEFT
        Align.CENTER -> Gravity.CENTER
        Align.RIGHT -> Gravity.RIGHT
      }
    }


}