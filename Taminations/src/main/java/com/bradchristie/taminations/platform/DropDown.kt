package com.bradchristie.taminations.platform
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
import com.bradchristie.taminations.Taminations

actual class DropDown actual constructor(title:String) : TextView(title) {

  private val dropDown = android.widget.PopupMenu(Taminations.context,div).apply {
    setOnMenuItemClickListener {
      selectCode(it.title.toString())
      true
    }
  }
  private var selectCode:(item:String)->Unit = { s: String -> }

  init {
    clickAction {
      dropDown.show()
    }
    div.setOnClickListener { clickCode() }
  }

  actual fun addItem(name:String, code:View.()->Unit):View {
    dropDown.menu.add(name)
    //  code not supported at this time
    return View()  // TODO
  }

  actual fun selectAction(action:(item:String)->Unit) {
    selectCode = action
  }

}