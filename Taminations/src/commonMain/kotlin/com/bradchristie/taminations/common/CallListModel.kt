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

import com.bradchristie.taminations.platform.attr

data class CallListData(val title:String, val link:String, val sublevel:String)

class CallListModel(private val view: CallListView) {

  fun reset(level:String, search:String = "") {
    view.clearItems()
    val d = LevelObject.find(level)
    val list = d.doc.evalXPath(d.selector)
    list.filter { it.attr("title").toLowerCase().contains(search.toLowerCase()) }
        .forEach {
      view.addItem(CallListData(
          it.getAttribute("title")!!,
          it.getAttribute("link")!!,
          it.getAttribute("sublevel")!!))
    }
    view.searchInput.keyAction {
      reset(level,view.searchInput.text)
    }
  }

}