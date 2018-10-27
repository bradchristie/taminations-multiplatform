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

//  Constructor creates a View either from an existing div or a new one
actual open class View actual constructor() {

  //  Create a view and its div
  //  constructor() : this(document.createHTMLElement("div") { })
  //  The display is written as for a screen with a height of 1000 pixels
  //  All dimensions are then scaled with this factor
  //  by using the .dip property (device-independent pixels)
  actual companion object {
    actual val scale: Double
      get() = TODO("not implemented")
    actual val Int.dip: Int
      get() = TODO("not implemented")
    actual val Int.dips: String
      get() = TODO("not implemented")
    actual val Int.pp: Int  // for text
      get() = TODO("not implemented")
  }

  actual var parentView: ViewGroup?
    get() = TODO("not implemented")
    set(value) {}
  //  Colors
  actual open var backgroundColor: Color
    get() = TODO("not implemented")
    set(value) {}
  actual open var textColor: Color
    get() = TODO("not implemented")
    set(value) {}

  //  Only gradients used are top to bottom
  actual fun linearGradient(top: Color, bottom: Color) {}

  actual var opacity: Double
    get() = TODO("not implemented")
    set(value) {}

  // Borders
  actual open inner class Border {
    actual var width: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var color: Color
      get() = TODO("not implemented")
      set(value) {}
  }

  actual inner class AllBorders {
    actual val top: Border
      get() = TODO("not implemented")
    actual val right: Border
      get() = TODO("not implemented")
    actual val bottom: Border
      get() = TODO("not implemented")
    actual val left: Border
      get() = TODO("not implemented")

  }

  actual inner class Borders : Border()

  actual val border: AllBorders
    get() = TODO("not implemented")
  actual val borders: Borders
    get() = TODO("not implemented")
  actual var borderRadius: Int
    get() = TODO("not implemented")
    set(value) {}

  //  Padding
  actual inner class AllPaddings {
    actual var top: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var right: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var bottom: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var left: Int
      get() = TODO("not implemented")
      set(value) {}
  }

  actual var padding: AllPaddings
    get() = TODO("not implemented")
    set(value) {}
  actual var paddings: Int
    get() = TODO("not implemented")
    set(value) {}

  //  Margin
  actual inner class AllMargins {
    actual var top: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var right: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var bottom: Int
      get() = TODO("not implemented")
      set(value) {}
    actual var left: Int
      get() = TODO("not implemented")
      set(value) {}
  }

  actual var margin: AllMargins
    get() = TODO("not implemented")
    set(value) {}
  actual var margins: Int
    get() = TODO("not implemented")
    set(value) {}
  //  Layout params
  actual var width: Int
    get() = TODO("not implemented")
    set(value) {}
  actual var height: Int
    get() = TODO("not implemented")
    set(value) {}
  actual open var weight: Int
    get() = TODO("not implemented")
    set(value) {}

  actual fun fillParent() {}
  actual fun fillHorizontal() {}
  actual fun fillVertical() {}
  //fun alignCenter()
  actual var isScrollable: Boolean
    get() = TODO("not implemented")
    set(value) {}
  //  Actions
  //  All actions and their code are declared here, although
  //  most are only applicable to inherited classes
  protected actual var clickCode: () -> Unit
    get() = TODO("not implemented")
    set(value) {}

  actual enum class SwipeDirection { UP, DOWN, LEFT, RIGHT }

  actual open var displayCode: () -> Unit
    get() = TODO("not implemented")
    set(value) {}

  actual fun clickAction(code: () -> Unit) {}
  actual fun wheelAction(code: (Int) -> Unit) {}
  actual fun touchDownAction(code: (Int, Int, Int) -> Unit) {}
  actual fun touchMoveAction(code: (Int, Int, Int) -> Unit) {}
  actual fun touchUpAction(code: (Int, Int, Int) -> Unit) {}
  actual fun keyDownAction(code: ((Int) -> Unit)?) {}
  actual fun keyUpAction(code: (Int) -> Unit) {}
  actual fun displayAction(code: () -> Unit) {}
  //  Focus, Hide and Show
  actual open fun focus() {}

  actual open fun hide() {}
  actual open fun show() {}
  //  Scroll, Swipe
  actual fun scrollToBottom() {}

  actual fun swipeAction(code: (SwipeDirection) -> Unit) {}

}