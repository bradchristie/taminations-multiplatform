package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.platform.*
import com.bradchristie.taminations.platform.View

fun ViewGroup.sliderTicView(code: SliderTicView.()->Unit = { }) : SliderTicView =
    appendView(SliderTicView(code))

class SliderTicView(code: SliderTicView.()->Unit = { }) : Canvas() {

  private var beats = 0.0
  private var parts = doubleArrayOf()
  private var isParts = false
  private var isCalls = false
  private var p = DrawingStyle()

  init {
    code()
  }

  override fun onDraw(ctx: DrawingContext) {
    val myLeft = 15.pp
    val myWidth = width.d - 30.pp
    //  Draw background
    ctx.fillRect(Rect(0.0,0.0,width.d,height.d), DrawingStyle(color=Color.TICS))
    if (beats > 0.0) {
      //  Draw tic marks
      p.color = Color.WHITE
      p.lineWidth = 1.0
      (1 .. beats.floor.i).forEach { loc ->
        val x = myLeft+myWidth*loc/beats
        ctx.drawLine(x, 0.0, x, height/4.0, p)
      }
      //  Draw tic labels
      if (beats > 4.0) {
        val y = height * 5.0 / 8.0
        val x1 = myLeft + myWidth * 2.0 / beats
        p.textSize = height / 3.0
        p.textAlign = TextAlign.CENTER
        ctx.fillText("Start", x1, y, p)
        val x2 = myLeft + myWidth * (beats - 2.0) / beats
        ctx.fillText("End", x2, y, p)
        if (parts.isNotEmpty()) {
          val denom = (parts.count() + 1)
          for (i in parts.indices) {
            if (parts[i] < beats-4) {
              val x = myLeft + myWidth * (2.0 + parts[i]) / beats
              val text = if (isParts && i == 0) "Part 2"
              else if (isParts||isCalls) (i+2).toString()
              else (i+1).toString() + "/" + denom
              ctx.fillText(text, x, y, p)
            }
          }
        }
      }
    }
  }

  fun setTics(b:Double, partstr:String, isParts:Boolean=false, isCalls:Boolean=false) {
    beats = b
    this.isParts = isParts
    this.isCalls = isCalls
    parts = doubleArrayOf()
    if (partstr.isNotEmpty()) {
      val t = partstr.split(";")
      parts = DoubleArray(t.count())
      var s = 0.0
      for (i in 0 until t.count()) {
        val p = t[i].d
        parts[i] = p + s
        s += p
      }
    }
    invalidate()
  }

}