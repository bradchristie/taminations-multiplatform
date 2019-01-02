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

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.max

actual class MultiColumnLayout : ViewGroup() {

  private val innerdiv = object : android.view.ViewGroup(Taminations.context) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
      var colWidth = 0
      var totWidth = 0
      var colHeight = 0
      val maxHeight = android.view.View.MeasureSpec.getSize(heightMeasureSpec)
      val childColumn = mutableListOf<View>()
      val measureOneColumn = {
        val spec = android.view.View.MeasureSpec.makeMeasureSpec(colWidth,
            android.view.View.MeasureSpec.EXACTLY)
        childColumn.forEach {
          it.div.measure(spec,heightMeasureSpec)
          //measureChildWithMargins(it.div,spec,0,heightMeasureSpec,0)
        }
        childColumn.clear()
        totWidth += colWidth
        colHeight = 0
        colWidth = 0
      }
      children.forEach { child ->
        child.div.measure(widthMeasureSpec,heightMeasureSpec)
        if (colHeight + child.div.measuredHeight > maxHeight)
          measureOneColumn()
        colHeight += child.div.measuredHeight
        colWidth = colWidth max child.div.measuredWidth
        childColumn += child
      }
      measureOneColumn()
      setMeasuredDimension(totWidth+colWidth,maxHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
      var colWidth = 0
      var totWidth = 0
      var colHeight = 0
      val childColumn = mutableListOf<View>()
      val layoutOneColumn = {
        var h = 0
        childColumn.forEach {
          it.div.layout(totWidth, h,
              totWidth + colWidth, h + it.div.measuredHeight)
          h += it.div.measuredHeight
        }
        childColumn.clear()
        totWidth += colWidth
        colHeight = 0
        colWidth = 0
      }
      children.forEach { child ->
        if (colHeight + child.div.measuredHeight > bottom)
          layoutOneColumn()
        childColumn += child
        colWidth = colWidth max child.div.measuredWidth
        colHeight += child.div.measuredHeight
      }
      layoutOneColumn()
    }

  }


  override val div = android.widget.HorizontalScrollView(Taminations.context).apply {
    addView(innerdiv)
    layoutParams = android.widget.FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    val layout = android.view.ViewGroup.MarginLayoutParams(MATCH_PARENT, WRAP_CONTENT)
    child.div.layoutParams = layout
    innerdiv.addView(child.div)
    children.add(child)
    child.parentView = this
    child.code()
    return child
  }

  override fun clear() {
    innerdiv.removeAllViews()
    children.clear()
  }

}