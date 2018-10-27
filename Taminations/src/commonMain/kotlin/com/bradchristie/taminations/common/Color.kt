package com.bradchristie.taminations.common
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

class Color(val color:Int = 0) {

  companion object {
    val WHITE = Color(0xffffff)
    val BLACK = Color(0x000000)
    val RED = Color(0xff0000)
    val GREEN = Color(0x00ff00)
    val BLUE = Color(0x0000ff)
    val YELLOW = Color(0xffff00)
    val MAGENTA = Color(0xff00ff)
    val CYAN = Color(0x00ffff)
    val ORANGE = Color(0xffc800)
    val GRAY = Color(0x808080)
    val LIGHTGRAY = Color(0xc0c0c0)

    // Other colors specific to Taminations
    val BMS = Color(0xc0c0ff)
    val B1 = Color(0xe0e0ff)
    val B2 = Color(0xe0e0ff)
    val MS = Color(0xe0e0ff)
    val PLUS = Color(0xc0ffc0)
    val ADV = Color(0xffe080)
    val A1 = Color(0xfff0c0)
    val A2 = Color(0xfff0c0)
    val CHALLENGE = Color(0xffc0c0)
    val C1 = Color(0xffe0e0)
    val C2 = Color(0xffe0e0)
    val C3A = Color(0xffe0e0)
    val C3B = Color(0xffe0e0)
    val COMMON = Color(0xc0ffc0)
    val HARDER = Color(0xffffc0)
    val EXPERT = Color(0xffc0c0)
    val FLOOR = Color(0xfff0e0)
    val TICS = Color(0x008000)
  }

  //  Return color components
  val red:Int get() = (color and 0xff0000) shr 16
  val green:Int get() = (color and 0x00ff00) shr 8
  val blue:Int get() = (color and 0x0000ff)

  //  Construct from color components
  constructor(red:Int, green:Int, blue:Int) :
    this((red shl 16) or (green shl 8) or blue)

  //  Construct from a name, used for dancer colors
  constructor(name:String) : this( when (name.toLowerCase()) {
    "black" -> Color.BLACK.color
    "blue" -> Color.BLUE.color
    "cyan" -> Color.CYAN.color
    "gray" -> Color.GRAY.color
    "green" -> Color.GREEN.color
    "magenta" -> Color.MAGENTA.color
    "orange" -> Color.ORANGE.color
    "red" -> Color.RED.color
    "white" -> Color.WHITE.color
    "yellow" -> Color.YELLOW.color
    else -> Color.WHITE.color
  })

  override operator fun equals(other:Any?):Boolean =
    when (other) {
      is Color -> other.color == color
      else -> false
    }

  override fun hashCode() = color
  private fun invert() : Color = Color(255 - red, 255 - green, 255 - blue)
  fun darker(f:Double = 0.7) : Color =
      Color((red * f).i, (green * f).i, (blue * f).i)
  fun brighter(f:Double = 0.7) : Color = invert().darker(f).invert()
  fun veryBright() : Color = brighter().brighter().brighter().brighter()

}