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
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.Color

actual class CachingLinearLayout actual constructor(private val adapter:CachingAdapter) : ViewGroup() {

  override val div = RecyclerView(Taminations.context).apply {
    layoutManager = LinearLayoutManager(Taminations.context)
    this.adapter = RecyclerViewAdapter()
  }

  class CachingHolder(val vg:ViewGroup) : RecyclerView.ViewHolder(vg.div)

  inner class RecyclerViewAdapter : RecyclerView.Adapter<CachingHolder> () {
    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): CachingHolder {
      val cell = LinearLayout(LinearLayout.Direction.HORIZONTAL)
      cell.div.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
      return CachingHolder(cell)
    }

    override fun getItemCount(): Int {
      return adapter.numberOfItems()
    }

    override fun onBindViewHolder(holder: CachingHolder, position: Int) {
      holder.vg.clear()
      val view = adapter.getItem(position)
      holder.vg.appendView(view)
      view.fillHorizontal()
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