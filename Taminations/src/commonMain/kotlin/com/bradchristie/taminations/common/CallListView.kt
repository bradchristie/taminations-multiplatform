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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.platform.*

class CallListView : LinearLayout(Direction.VERTICAL) {

  inner class CallListViewAdapter : CachingAdapter() {

    override fun numberOfItems(): Int {
      return items.count()
    }

    override fun getItem(i: Int): View {
      return makeSelectableView(i)
    }

  }

  private val callList = if (Application.isLandscape)
    MultiColumnLayout(CallListViewAdapter())
  else
    CachingLinearLayout(CallListViewAdapter())
  val searchInput = TextInput()
  private val textsize = 20
  private var dirty = false

  init {
    backgroundColor = Color.LIGHTGRAY
    appendView(searchInput) {
      weight = 0
      hint = "Search Calls"
    }
    appendView(callList) {
      isScrollable = true
    }
  }

  fun clearItems() {
    callList.clear()
    items.clear()
  }

  var items = mutableListOf<CallListData>()

  fun addItem(item: CallListData) {
    items.add(item)
    if (!dirty) {
      dirty = true
      System.later {
        callList.clear()
        dirty = false
      }
    }
  }

  fun makeSelectableView(position: Int): View {
    val item = items[position]
    return SelectablePanel().apply {
      clickAction {
        Application.sendMessage(Request.Action.CALLITEM,
            "title" to item.title,
            "link" to item.link,
            "level" to item.sublevel)
      }
      // not valid on Android weight = 0  // don't try to force list to fit screen
      backgroundColor = LevelObject.find(item.sublevel).color
      borders.width = 1
      textView(item.title) {
        margin.top = 4
        margin.bottom = 4
        margin.left = 4
        weight = 1
        textSize = textsize
        nowrap()
      }
      textView(LevelObject.find(item.sublevel).name) {
        textSize = textsize * 2 / 3
        margin.right = 12
        margin.top = 4
        weight = 0
      }
    }
  }

}