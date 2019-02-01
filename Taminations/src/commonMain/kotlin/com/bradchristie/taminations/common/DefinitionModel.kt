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

import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.Setting
import com.bradchristie.taminations.platform.System

class DefinitionPage : Page() {

  override val view = DefinitionView()
  private val model = DefinitionModel(view)

  init {
    onAction(Request.Action.DEFINITION) { request ->
      model.setDefinition(request["link"],request["title"])
    }
    onMessage(Request.Action.ANIMATION) { request ->
      model.setTitle(request["title"])
    }
    onMessage(Request.Action.ANIMATION_PART) { request ->
      model.setPart(request["part"].i)
    }
  }

}

class DefinitionModel(private val dv: DefinitionView) {

  private var currentLink = ""
  private var currentCall = ""

  init {
    dv.abbrevRB.clickAction {
      dv.eval("setAbbrev(true)")
      Setting("DefinitionAbbrev").b = true
    }
    dv.fullRB.clickAction {
      dv.eval("setAbbrev(false)")
      Setting("DefinitionAbbrev").b = false
    }

  }

  fun setDefinition(link:String, title:String) {
    if (link != currentLink) {
      var langlink = link
      //  See if we have it in user's language
      val lang = System.userLanguage
      if (lang != "en" &&
        TamUtils.calllistdata.any {
          link == it.link && it.languages.contains(lang)
        })
        langlink += ".lang-$lang"
      dv.setSource("$langlink.html") {
        val isAbbrev = Setting("DefinitionAbbrev").b == true
        if (isAbbrev)
          dv.abbrevRB.isChecked = true
        else
          dv.fullRB.isChecked = true
        dv.eval("setAbbrev($isAbbrev)") {
          if (it.contains("false"))
            dv.buttonView.hide()
          else
            dv.buttonView.show()
        }
      }
    }
    if (title.isNotEmpty())
      currentCall = title.replace(" ", "")
  }

  //  This is needed for highlighting definitions that contain several calls
  //  such as Swing Thru and Left Swing Thru
  fun setTitle(title:String) {
    currentCall = title.replace(" ","")
  }

  fun setPart(part:Int) {
    dv.eval("setPart($part,'$currentCall')")
  }

}