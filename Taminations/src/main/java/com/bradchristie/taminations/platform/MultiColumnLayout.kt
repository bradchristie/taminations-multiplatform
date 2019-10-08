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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bradchristie.taminations.Application
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.i

actual class MultiColumnLayout actual constructor(private val adapter: CachingAdapter) : ViewGroup() {

  //  Found this formula by experimentation
  //  User's setting for font size affects sp2px
  //  User's setting for display size affects density
  val itemHeight get() = (20 * Application.sp2px + 16 * Application.density).i
  override val div = RecyclerView(Taminations.context).apply {
    layoutManager = LinearLayoutManager(Taminations.context,RecyclerView.HORIZONTAL,false)
    this.adapter = RecyclerViewAdapter()
    setHasFixedSize(true)
  }

  class CachingHolder(val vg:ViewGroup) : RecyclerView.ViewHolder(vg.div) {  }

  inner class RecyclerViewAdapter : RecyclerView.Adapter<CachingHolder> () {

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CachingHolder {
      val cell = LinearLayout(LinearLayout.Direction.VERTICAL)
      cell.div.layoutParams = RecyclerView.LayoutParams(WRAP_CONTENT, MATCH_PARENT)
      return CachingHolder(cell)
    }

    private val screenHeightPixels = Taminations.context.resources.displayMetrics.heightPixels

    private fun itemsPerColumn() = when {
      parentView != null && (parentView?.height ?: 0) > 0 -> (parentView?.height ?: 0) / itemHeight
      //  Hack to compute available height when Android won't tell me
      else -> (screenHeightPixels * 9 / 10 - 40) / itemHeight
    }

    override fun getItemCount(): Int {
      return (adapter.numberOfItems() + itemsPerColumn() - 1) / itemsPerColumn()
    }

    override fun onBindViewHolder(holder: CachingHolder, position: Int) {
      holder.vg.clear()
      for (i in position*itemsPerColumn() until (position+1)*itemsPerColumn()) {
        val view = if (i < adapter.numberOfItems())
          adapter.getItem(i)
        else
          SelectablePanel()
        holder.vg.appendView(view)
        view.weight = 1
      }
    }

  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit): T {
    throw NotImplementedError("Do not use appendView, use the adapter")
  }

  override fun clear() {
    div.adapter?.notifyDataSetChanged()
    super.clear()
  }

}