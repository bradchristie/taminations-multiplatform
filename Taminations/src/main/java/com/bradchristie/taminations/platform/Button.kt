package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import android.graphics.*
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.RoundRectShape
import com.bradchristie.taminations.Application
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.*
import com.bradchristie.taminations.common.Color

actual class ImageButton actual constructor(name:String, c: Shape) : Button(name,c) {

  actual fun setImage(shape:Shape) {
    super.setImage(shape.drawable)
  }

}

actual open class Button actual constructor(t:String) : View() {

  actual var gradientColor = Color.LIGHTGRAY
  private val rectBorder = RoundRectShape(FloatArray(8) {8.dip.f},
      RectF(2.dip.f,2.dip.f,2.dip.f,2.dip.f), FloatArray(8) {6.dip.f})
  private val stateList = StateListDrawable()
  actual var text:String
    get() = div.text.toString()
    set(value) { div.text = value }
  actual var id:String = ""

  override val div = android.widget.Button(Taminations.context).apply {
    typeface = Typeface.DEFAULT_BOLD
    textSize = 30.pp.f
    maxLines = 1
    text = t
    setPadding(16,8,16,8)
    val rectShape = RoundRectShape(FloatArray(8) {8.dip.f},null,null)
    val borderShape = ShapeDrawable(rectBorder)
    val normalShape = ShapeDrawable(rectShape)
    val pressedShape = ShapeDrawable(rectShape)
    borderShape.shaderFactory = object : ShapeDrawable.ShaderFactory() {
      override fun resize(width: Int, height: Int): Shader =
          LinearGradient(0f,0f,0f,height.toFloat(),Color.WHITE.a,Color.GRAY.a,Shader.TileMode.CLAMP)
    }
    normalShape.shaderFactory = object : ShapeDrawable.ShaderFactory() {
      override fun resize(width: Int, height: Int): Shader =
          LinearGradient(0f,0f,0f,height.f,gradientColor.brighter().brighter().a,gradientColor.darker(0.9).a,Shader.TileMode.CLAMP)
    }
    pressedShape.shaderFactory = object : ShapeDrawable.ShaderFactory() {
      override fun resize(width: Int, height: Int): Shader =
          LinearGradient(0f,0f,0f,height.toFloat(),gradientColor.darker().a,gradientColor.brighter().a,Shader.TileMode.CLAMP)
    }
    stateList.addState(IntArray(1) {android.R.attr.state_pressed}, LayerDrawable(arrayOf(pressedShape,borderShape)))
    stateList.addState(IntArray(1) {0},LayerDrawable(arrayOf(normalShape,borderShape)))
    background = stateList
    setOnClickListener {
      clickCode()
      Application.sendMessage(Request.Action.BUTTON_PRESS,
          "button" to text.toString(),
          "id" to this@Button.id)
    }
  }

  constructor(text:String,image:Shape) : this(text) {
    setImage(image.drawable)
  }

  fun setImage(image: Drawable) {
    div.background = LayerDrawable(arrayOf(stateList,image))
    div.text = ""
  }

}