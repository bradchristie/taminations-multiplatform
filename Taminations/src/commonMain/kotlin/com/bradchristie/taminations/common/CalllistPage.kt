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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.platform.Page

class CalllistPage : Page()  {

  override val view = CallListView()
  val model = CallListModel(view)
  var level = ""

  init {
    onAction(Request.Action.CALLLIST) { request ->
      level = request["level"]
      model.reset(level)
      Application.titleBar.title = LevelObject.find(level).name
      Application.titleBar.level = ""
    }
    onMessage(Request.Action.CALLITEM) { request ->
      Application.sendRequest(Request(Request.Action.ANIMLIST,
          "level" to request["sublevel"],
          "link" to request["link"]))

    }
  }

}