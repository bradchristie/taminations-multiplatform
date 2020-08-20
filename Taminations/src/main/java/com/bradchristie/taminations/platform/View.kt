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

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.bradchristie.taminations.Application
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.*

private const val SWIPE_DISTANCE_THRESHOLD = 100
private const val SWIPE_VELOCITY_THRESHOLD = 100

actual open class View actual constructor() {

  internal open val div = android.view.View(Taminations.context).addListeners()

  private var didLongPress = false
  private inner class DivGestureListener : GestureDetector.OnGestureListener {

    //  onShowPress is called on a long press before the touch up
    override fun onShowPress(e: MotionEvent?) { }

    //  This is called after a touch down + touch up
    //  that's not a long press and does not move
    override fun onSingleTapUp(e: MotionEvent?): Boolean {
      e?.apply {
        touchUpCode(getPointerId(actionIndex),
                      getX(actionIndex).i,
                      getY(actionIndex).i)
      }
      return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
      e?.apply {
        touchDownCode(getPointerId(actionIndex),
            getX(actionIndex).i,
            getY(actionIndex).i)
      }
      return true
    }

    //  onFling is called following any touch down followed by move
    //  followed by touch up
    override fun onFling(e1:MotionEvent, e2:MotionEvent, velX:Float, velY:Float):Boolean {
      val dx = e2.x - e1.x
      val dy = e2.y - e1.y

      //  First see if it's a real fling
      if (dx.abs > dy.abs && dx.abs > SWIPE_DISTANCE_THRESHOLD && velX.abs > SWIPE_VELOCITY_THRESHOLD) {
        if (dx > 0)
          swipeCode(SwipeDirection.RIGHT)
        else
          swipeCode(SwipeDirection.LEFT)
      } else if (dy.abs > dx.abs && dy.abs > SWIPE_DISTANCE_THRESHOLD && velY.abs > SWIPE_VELOCITY_THRESHOLD) {
        if (dy > 0)
          swipeCode(SwipeDirection.DOWN)
        else
          swipeCode(SwipeDirection.UP)

      } else {
        //  Not a fling so process the touch up
        e2.apply {
          touchUpCode(
              getPointerId(actionIndex),
              getX(actionIndex).i,
              getY(actionIndex).i
          )
        }
      }
      return true
    }

    //  onScroll is called for every move after a touch down
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
      e2?.apply {
        touchMoveCode(actionIndex,getX(actionIndex).i,getY(actionIndex).i)
      }
      return true
    }

    override fun onLongPress(e: MotionEvent?) {
      e?.apply {
        longPressCode(x.i, y.i)
        didLongPress = true
      }
    }

  }


  @SuppressLint("ClickableViewAccessibility")
  protected fun android.view.View.addListeners():android.view.View {
    layoutParams = android.widget.LinearLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT)
    setOnTouchListener { _: android.view.View, event: MotionEvent ->
        if (!gestureDetector.onTouchEvent(event)) {
          when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
              touchDownCode(
                  event.getPointerId(event.actionIndex),
                  event.getX(event.actionIndex).i,
                  event.getY(event.actionIndex).i
              )
            }
            MotionEvent.ACTION_MOVE -> {
              //  Multiple move events could be sent at once,
              //  so need to loop through
              for (i in 0 until event.pointerCount) {
                touchMoveCode(event.getPointerId(i),
                    event.getX(i).i,
                    event.getY(i).i)
              }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
              //  Long press doesn't handle the up event.
              //  But we don't want the up event after a long press
              //  to do anything.
              //  So special handling to ignore it.
              if (didLongPress)
                didLongPress = false
              else
                touchUpCode(event.getPointerId(event.actionIndex),
                    event.getX(event.actionIndex).i,
                    event.getY(event.actionIndex).i)
            }
          }
          false
        }
      else
        true
    }
    setOnKeyListener { _: android.view.View, keyCode:Int, event:KeyEvent ->
      when (event.action) {
        KeyEvent.ACTION_DOWN -> { keyDownCode(keyCode); true }
        KeyEvent.ACTION_UP -> { keyUpCode(keyCode); true }
        else -> false
      }
    }
    return this
  }

  actual companion object {
    //  Conversions from various scales to pixels
    //  Dimensions can be scaled to a percent of the display
    //  One pp is 0.1% of the screen height
    actual val Int.pp:Int get() = (this * Application.screenHeight) / 1000
    //  Conversion to Android's "dip", which should be about
    //  the same across different devices
    actual val Int.dip:Int get() = (this * Application.density).i
    actual val Int.dips:String get() = this.dip.s
    //  And Android "sp", like dip but includes
    //  user font size preference
    //  not used val Int.sp:Int get() = (this * Application.fontdensity).i
    //  Back conversions
    val Int.px2dip get() = (this / Application.density).i
    actual val scale: Double
      get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
  actual var parentView: ViewGroup? = null

  //  Colors
  inner class BackgroundWithBorders : ShapeDrawable() {
    @SuppressLint("CanvasSize")
    override fun draw(ctx: android.graphics.Canvas) {
      ctx.drawRect(Rect(0,0, ctx.width, ctx.height),backgroundPaint)
      if (border.top.w > 0) {
        ctx.drawLine(0.f,0.f, ctx.width.f,0.f,Paint().apply{color=border.top.c})
      }
      if (border.bottom.w > 0) {
        val y = (ctx.height - border.bottom.w).f
        ctx.drawLine(0.f,y, ctx.width.f,y,Paint().apply{color=border.bottom.c})
      }
      if (border.left.w > 0) {
        ctx.drawLine(0.f,0.f,0.f, ctx.height.f,Paint().apply{color=border.left.c})
      }
      if (border.right.w > 0) {
        val x = (ctx.width - border.right.w).f
        ctx.drawLine(x,0.f,x,ctx.height.f,Paint().apply{color=border.right.c})
      }
    }
  }
  var backgroundPaint = Paint()
  actual open var backgroundColor: Color
    get() = Color(backgroundPaint.color)
    set(c) {
      backgroundPaint.color = c.a
      backgroundPaint.style=Paint.Style.FILL
      div.background = BackgroundWithBorders()
    }
  actual open var textColor:Color = Color.BLACK  // overriden by TextView
  //  Only gradients used are top to bottom
  actual fun linearGradient(top:Color,bottom:Color) {
    div.background = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
        intArrayOf(top.a, bottom.a))
  }
  actual var opacity: Double
    get() = div.alpha.d
    set(value) { div.alpha = value.f }

  //  Borders
  private fun w2px(w:Int) = if (w==1) 1 else w.dip
  actual open inner class Border {
    var w : Int = 0
    var c : Int = Color.BLACK.a
    actual open var width: Int
      get() { throw UnsupportedOperationException() }
      set(value) { w = w2px(value) }
    actual open var color: Color
      get() { throw UnsupportedOperationException() }
      set(value) { c = value.a }
  }
  actual inner class AllBorders {
    actual val top = Border()
    actual val right = Border()
    actual val bottom = Border()
    actual val left = Border()
  }
  actual val border = AllBorders()
  actual inner class Borders : Border() {
    actual override var width: Int
      get() { throw UnsupportedOperationException() }
      set(value) {
        border.bottom.width = value
        border.top.width = value
        border.left.width = value
        border.right.width = value
      }
    actual override var color: Color
      get() { throw UnsupportedOperationException() }
      set(value) {
        border.bottom.color = value
        border.top.color = value
        border.left.color = value
        border.right.color = value
      }
  }
  actual val borders = Borders()
  actual var borderRadius: Int
    get() { throw UnsupportedOperationException() }
    set(_) { }

  //  Margins
  private val marginLayout
    get() = div.layoutParams as android.view.ViewGroup.MarginLayoutParams
  actual inner class AllMargins {
    actual var top: Int
      get() { throw UnsupportedOperationException() }
      set(value) { marginLayout.topMargin = value.dip }
    actual var right: Int
      get() { throw UnsupportedOperationException() }
      set(value) { marginLayout.rightMargin = value.dip }
    actual var bottom: Int
      get() { throw UnsupportedOperationException() }
      set(value) { marginLayout.bottomMargin = value.dip }
    actual var left: Int
      get() { throw UnsupportedOperationException() }
      set(value) { marginLayout.leftMargin = value.dip }
  }
  actual var margin = AllMargins()
  actual var margins: Int
    get() { throw UnsupportedOperationException() }
    set(value) { marginLayout.setMargins(value.dip,value.dip,value.dip,value.dip) }


  //  Layout params
  actual open var width:Int
    //  Android apparently expects these values in dip
    get() = div.width
    set(value) {  div.layoutParams.width = value.dip }
  actual open var height:Int
    get() = div.height
    set(value) { div.layoutParams.height = value.dip }

  val weightLayout
    get() = div.layoutParams as android.widget.LinearLayout.LayoutParams
  actual open var weight:Int
    get() = weightLayout.weight.i
    set(value) {
      val lay = weightLayout
      lay.weight = value.f
      if (value > 0) {
        if (lay.width == WRAP_CONTENT)
          lay.width = 0
        if (lay.height == WRAP_CONTENT)
          lay.height = 0
      }
      div.layoutParams = lay
    }

  actual open var isScrollable = false

  actual fun fillParent() {
    //  Create layoutparams if needed, but don't clobber existing one
    if (div.layoutParams == null)
      div.layoutParams = android.widget.LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    else {
      div.layoutParams.width = MATCH_PARENT
      div.layoutParams.height = MATCH_PARENT
    }

  }
  actual fun fillHorizontal() {
    div.layoutParams.width = MATCH_PARENT
  }
  actual fun fillVertical() {
    div.layoutParams.height = MATCH_PARENT
  }

  actual open fun focus() { div.requestFocus() }
  actual open fun hide() {
    div.visibility = android.view.View.GONE
  }

  actual open fun show() {
    div.visibility = android.view.View.VISIBLE
  }

  //  Actions
  //  All actions and their code are declared here, although
  //  most are only applicable to inherited classes
  protected actual var clickCode:()->Unit = { }
  protected var touchDownCode:(Int,Int,Int)->Unit = { _: Int, _: Int, _: Int -> }
  private var touchUpCode:(Int, Int, Int)->Unit = { _: Int, _: Int, _: Int -> }
  private var touchMoveCode:(Int, Int, Int)->Unit = { _: Int, _: Int, _: Int -> }
  protected var longPressCode:(Int,Int)->Unit = { _: Int, _: Int -> }
  protected var keyDownCode:(Int)->Unit = { }
  protected var keyUpCode:(Int)->Unit = { }
  //  No wheels on Android?
  private var wheelCode:(Int)->Unit = { }
  private var gestureDetector:GestureDetector =
      GestureDetector(Taminations.context,DivGestureListener())
  actual enum class SwipeDirection { UP, DOWN, LEFT, RIGHT }
  protected var swipeCode:(SwipeDirection)->Unit = { }
  private var consumeTouch = false

  actual fun clickAction(code:()->Unit) {
    clickCode = code
    div.setOnClickListener { clickCode() }
  }
  actual fun wheelAction(code:(Int)->Unit) { wheelCode = code }
  actual fun touchDownAction(code:(Int, Int, Int)->Unit) { touchDownCode = code }
  actual fun touchMoveAction(code:(Int, Int, Int)->Unit) {
    touchMoveCode = code
    consumeTouch = true
  }
  actual fun touchUpAction(code:(Int, Int, Int)->Unit) { touchUpCode = code }
  actual fun keyDownAction(code:((Int)->Unit)?) {
    when (code) {
      is (Int)->Unit -> keyDownCode = code
      else -> { }
    }
  }
  //  displayCode not used on this platform
  actual open var displayCode:()->Unit = { }
  actual fun displayAction(code:()->Unit) {
    displayCode = code
  }
  actual fun keyUpAction(code:(Int)->Unit) { keyUpCode = code }
  actual fun swipeAction(code:(SwipeDirection)->Unit) {
    swipeCode = code
  }
  actual fun longPressAction(code:(Int, Int)->Unit) {
    longPressCode = code
  }
  //  Scroll
  actual open fun scrollToBottom() {
    //  TODO
  }

}