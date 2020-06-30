package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.common.d
import com.bradchristie.taminations.common.i
import com.bradchristie.taminations.common.s
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.browser.window

//  Base class for div used as a generic container
//  with extra goodies
//  Extension on div to convert to a View
//fun HTMLElement.view(code: View.()->Unit = { }) = View(this, code)

val Color.css get() = "rgb($red,$green,$blue)"

//  Constructor creates a View either from an existing div or a new one
actual open class View(internal val div:HTMLElement) {

  //  Create a view and its div
  actual constructor() : this(document.createHTMLElement("div"))

  //  The display is written as for a screen with a height of 1000 pixels
  //  All dimensions are then scaled with this factor
  //  by using the .dip property (device-independent pixels)
  actual companion object {
    actual val scale:Double = window.innerHeight / 1000.0
    actual val Int.dip:Int get() = (this * scale).i
    actual val Int.dips:String get() = "${this.d * scale}px"
    actual val Int.pp:Int get() = (this * scale).i
  }

  val parentNode get() = div.parentNode
  actual var parentView: ViewGroup? = null
  //val firstView get() = View(div.firstChild as HTMLElement)
  val lastView get() = View(div.lastChild as HTMLElement)

  val style get() = div.style

  //  Colors
  protected val Color.css get() = "rgb($red,$green,$blue)"
  protected fun cssColor(cstr:String): Color = when {
  //  Parse a css color
    cstr.startsWith("rgb") -> {
      val nums = Regex("\\d+").findAll(cstr).map { it.value.i }.toList()
      Color(nums[0], nums[1], nums[2])
    }
    cstr.startsWith("#") ->
      Color(cstr.substring(1).toInt(16))
    else -> Color.BLACK
  }
  //  Convert to css string
  actual open var backgroundColor: Color
    get() = cssColor(div.style.backgroundColor)
    set(c) {
      div.style.backgroundColor = c.css
    }
  actual open var textColor: Color
    get() = cssColor(div.style.color)
    set(c) {
      div.style.color = "rgb(${c.red},${c.green},${c.blue})"
    }
  //  Only gradients used are top to bottom
  actual fun linearGradient(top: Color, bottom: Color) {
    style.background = "linear-gradient(${top.css},${bottom.css})"
  }
  actual var opacity:Double
    get() = div.style.opacity.d
    set(value) { div.style.opacity = value.s }

  // Borders
  private fun w2px(w:Int) = if (w==1) "1px" else w.dips
  actual open inner class Border(private val setWidth: (Int) -> Unit,
                                 private val setColor: (Color) -> Unit) {

    actual var width: Int
      get() { throw UnsupportedOperationException() }
      set(w) = setWidth(w)
    actual var color: Color
      get() { throw UnsupportedOperationException() }
      set(c) = setColor(c)
  }
  actual inner class AllBorders {
    actual val top = Border(
        { w -> div.style.borderTopWidth = w2px(w) },
        { c -> div.style.borderTopColor = c.css }
    )
    actual val right = Border(
        { w -> div.style.borderRightWidth = w2px(w) },
        { c -> div.style.borderRightColor = c.css }
    )
    actual val bottom = Border(
        { w -> div.style.borderBottomWidth = w2px(w) },
        { c -> div.style.borderBottomColor = c.css }
    )
    actual val left = Border(
        { w -> div.style.borderLeftWidth = w2px(w) },
        { c -> div.style.borderLeftColor = c.css }
    )
  }
  actual val border = AllBorders()
  actual inner class Borders(
      setWidth: (Int) -> Unit,
      setColor: (Color) -> Unit) : Border(setWidth,setColor)

  actual val borders = Borders(
    { w -> div.style.borderWidth = w2px(w) },
    { c -> div.style.borderColor = c.css }
  )
  actual var borderRadius: Int
    get() { throw UnsupportedOperationException() }
    set(r) { div.style.borderRadius = r.dips }

  //  Margin
  actual inner class AllMargins {
    actual var top: Int
      get() { throw UnsupportedOperationException() }
      set(value) { div.style.marginTop = value.dips }
    actual var right: Int
      get() { throw UnsupportedOperationException() }
      set(value) { div.style.marginRight = value.dips }
    actual var bottom: Int
      get() { throw UnsupportedOperationException() }
      set(value) { div.style.marginBottom = value.dips }
    actual var left: Int
      get() { throw UnsupportedOperationException() }
      set(value) { div.style.marginLeft = value.dips }
  }
  actual var margin = AllMargins()
  actual var margins: Int
    get() { throw UnsupportedOperationException() }
    set(value) { div.style.margin = value.dips }

  //  Layout params
  actual var width
    get() = div.clientWidth
    set(value) { div.style.width = value.dips }
  actual var height
    get() = div.clientHeight
    set(value) { div.style.height = value.dips }
  actual open var weight:Int
    get() = "0${style.flexGrow}".i
    set(value) {
      if (value == 0) {
        //  Force view to keep its size
        style.flexGrow = "0"
        style.flexShrink = "0"
        style.flexBasis = ""
      } else {
        style.flexBasis = "0"   // so all views grow equally
        style.flexGrow = "$value"
      }
    }
  actual fun fillParent() {  //  necessary??
    style.width = "100%"
    style.height = "100%"
  }
  actual fun fillHorizontal() { style.width = "100%" }
  actual fun fillVertical() { style.height = "100%" }
  actual var isScrollable:Boolean
    get() = div.style.overflowY == "auto"
    set(value) { div.style.overflowY = if (value) "auto" else "hidden" }

  init {
    //  Clip view by default.  Avoids strange layout problems.
    //  Not true any more ??
    //div.style.overflowX = "hidden"
    //div.style.overflowY = "hidden"
    //  Put borders and margins inside the box, since generally views
    //  are side-by-side
    div.style.boxSizing = "border-box"
    //  Generally text in these views is not to be selected
    //  Turning off selection at this time is not standardized
    System.writeProp(div.style, "-moz-user-select", "none")
    System.writeProp(div.style, "-webkit-user-select", "none")
    System.writeProp(div.style, "-ms-user-select", "none")
    System.writeProp(div.style, "user-select", "none")
    //  All borders are solid
    div.style.borderStyle = "solid"
    //  But turn off unless a specific border is set
    div.style.borderWidth = "0"
    //  Default border color is black
    div.style.borderColor = Color.BLACK.css
    //  We just use one font
    div.style.fontFamily = "Arial, sans-serif"
    div.onclick = { clickCode() }
    div.onwheel = { event -> wheelCode(event.deltaY.i) }
    div.onmousedown = { event ->
      touchDownCode(event.button.i,event.offsetX.i,event.offsetY.i) }
    div.onmouseup = { event ->
      touchUpCode(event.button.i,event.offsetX.i,event.offsetY.i) }
    div.onmousemove = { event ->
      touchMoveCode(0,event.offsetX.i,event.offsetY.i) }
    div.oncontextmenu = { false }
  }

  //  Actions
  //  All actions and their code are declared here, although
  //  most are only applicable to inherited classes
  protected actual var clickCode:()->Unit = { }
  private var touchDownCode:(Int, Int, Int)->Unit
      = { _: Int, _: Int, _: Int -> }
  private var touchUpCode:(Int,Int,Int)->Unit = { _: Int, _: Int, _: Int -> }
  private var touchMoveCode:(Int, Int, Int)->Unit
      = { _: Int, _: Int, _: Int -> }
  private var keyDownCode:(Int)->Unit = { }
  private var keyUpCode:(Int)->Unit = { }
  private var wheelCode:(Int)->Unit = { }
  actual enum class SwipeDirection { UP, DOWN, LEFT, RIGHT }
  actual open var displayCode:()->Unit = { }

  actual fun clickAction(code:()->Unit) {
    clickCode = code
    style.cursor = "pointer"
  }
  actual fun wheelAction(code:(Int)->Unit) { wheelCode = code }
  actual fun touchDownAction(code:(Int, Int, Int)->Unit) { touchDownCode = code }
  actual fun touchMoveAction(code:(Int, Int, Int)->Unit) { touchMoveCode = code }
  actual fun touchUpAction(code:(Int, Int, Int)->Unit) { touchUpCode = code }
  actual fun keyDownAction(code:((Int)->Unit)?) {
    when (code) {
     is (Int)->Unit -> { keyDownCode = code
          document.onkeydown = { event ->
            keyDownCode(event.keyCode)
            event.preventDefault()
          }
      }
      else -> document.onkeydown = null
    }
  }
  actual fun keyUpAction(code:(Int)->Unit) {
    keyUpCode = code
    document.onkeyup = { event ->
      keyUpCode(event.keyCode)
      event.preventDefault()
    }
  }
  actual fun longPressAction(code:(Int, Int)->Unit) {
    //  not used on this platform
  }
  actual fun displayAction(code:()->Unit) {
    displayCode = code
  }

  //  Hide and Show
  actual open fun focus() = div.focus()
  private var saveDisplay = ""
  actual open fun hide() {
    if (div.style.display != "none") {
      saveDisplay = div.style.display
      if (saveDisplay.isBlank())
        saveDisplay = "block"
      div.style.display = "none"
    }
  }
  actual open fun show() {
    if (div.style.display == "none" && saveDisplay.isNotBlank()) {
      div.style.display = saveDisplay
      saveDisplay = ""
    }
    displayCode()
  }

  //  Scroll
  actual open fun scrollToBottom() {
    div.scrollTop = div.scrollHeight.d - div.offsetHeight.d
  }

  //  No swipe on web
  actual fun swipeAction(code:(SwipeDirection)->Unit) { }
}