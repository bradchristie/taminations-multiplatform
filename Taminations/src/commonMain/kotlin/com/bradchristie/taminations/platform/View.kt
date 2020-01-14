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

//  Base class for a generic container
//  with extra goodies


//  Constructor creates a View either from an existing div or a new one
expect open class View() {

  //  Create a view and its div
  //  constructor() : this(document.createHTMLElement("div") { })

  //  The display is written as for a screen with a height of 1000 pixels
  //  All dimensions are then scaled with this factor
  //  by using the .dip property (device-independent pixels)
  companion object {
    val scale:Double
    val Int.dip:Int
    val Int.dips:String
    val Int.pp:Int  // for text
  }

  var parentView: ViewGroup?

  //  Colors
  open var backgroundColor: Color
  open var textColor: Color
  //  Only gradients used are top to bottom
  fun linearGradient(top: Color, bottom: Color)
  var opacity:Double

  // Borders
  open inner class Border {
    var width: Int
    var color: Color
  }
  inner class AllBorders {
    val top : Border
    val right : Border
    val bottom : Border
    val left : Border

  }
  inner class Borders : Border
  val border : AllBorders
  val borders : Borders
  var borderRadius: Int

  //  Margin
  inner class AllMargins {
    var top: Int
    var right: Int
    var bottom: Int
    var left: Int
  }
  var margin : AllMargins
  var margins: Int

  //  Layout params
  var width : Int
  var height : Int
  open var weight : Int
  fun fillParent()
  fun fillHorizontal()
  fun fillVertical()
  //fun alignCenter()
  var isScrollable:Boolean

  //  Actions
  //  All actions and their code are declared here, although
  //  most are only applicable to inherited classes
  protected var clickCode:()->Unit
  enum class SwipeDirection { UP, DOWN, LEFT, RIGHT }
  open var displayCode:()->Unit

  fun clickAction(code:()->Unit)
  fun wheelAction(code:(Int)->Unit)
  fun touchDownAction(code:(Int,Int,Int)->Unit)
  fun touchMoveAction(code:(Int,Int,Int)->Unit)
  fun touchUpAction(code:(Int,Int,Int)->Unit)
  fun keyDownAction(code:((Int)->Unit)?)
  fun keyUpAction(code:(Int)->Unit)
  fun displayAction(code:()->Unit)

  //  Focus, Hide and Show
  open fun focus()
  open fun hide()
  open fun show()

  //  Scroll, Swipe
  open fun scrollToBottom()
  fun swipeAction(code:(SwipeDirection)->Unit)
}