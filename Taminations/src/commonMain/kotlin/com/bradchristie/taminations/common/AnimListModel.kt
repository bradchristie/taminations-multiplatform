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
import com.bradchristie.taminations.common.Color.Companion.COMMON
import com.bradchristie.taminations.common.Color.Companion.EXPERT
import com.bradchristie.taminations.common.Color.Companion.HARDER
import com.bradchristie.taminations.common.Color.Companion.WHITE
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.SelectablePanel
import com.bradchristie.taminations.platform.*
import com.bradchristie.taminations.platform.System.later

class AnimListPage : Page() {

  override val view = AnimListView()
  lateinit var animListModel: AnimListModel

  init {
    onAction(Request.Action.ANIMLIST) { request ->
      view.clearItems()
      if (Application.isPortrait)
        view.buttonView.show()
      else
        view.buttonView.hide()
      animListModel = AnimListModel(view, request)
    }
    onMessage(Request.Action.ANIMATION) { request ->
      if (Application.isPortrait)
        Application.sendRequest(request)
      else
        Application.updateLocation(Request(Request.Action.ANIMATION,request))
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      if (Application.isPortrait) {
        when (request["button"]) {
          "Settings" -> Application.sendRequest(Request.Action.SETTINGS)
          "Definition" -> Application.sendRequest(Request.Action.DEFINITION,
              "link" to animListModel.link)
        }
      }
    }
  }

}

class AnimListModel(private val alview: AnimListView, val request: Request ) {

  private var animListItems = mutableListOf<AnimListItem>()
  private var selectanim: SelectablePanel? = null
  var link = request["link"]
  private var itemsLeft = 100  // flag to tell when all list items are complete

  init {
    System.getXMLAsset(link) { doc ->
      val tams = doc.tamList()
      Application.titleBar.title = doc.getTitle()
      val level = link.split("/")[0]
      Application.titleBar.level = LevelObject.find(level).name
      // Fetch the list of animations and build the table
      var prevtitle = ""
      var prevgroup = ""
      alview.keyView.hide()
      tams.filter { it.attr("display") != "none" }.forEach { tam ->
        val tamtitle = tam.attr("title")
        var from = "from"  // updated later after tamxref is loaded
        val group = tam.attr("group")
        if (group.isNotEmpty()) {
          // Add header for new group as needed
          if (group != prevgroup) {
            if (group.matches(Regex("\\s+"))) {
              // Blank group, for calls with no common starting phrase
              // Add a separator unless it's the first group
              if (alview.count > 0)
                addSeparator(AnimListItem(CellType.Separator, "", "", ""))
            } else
            // Named group e.g. "As Couples.."
            // Add a header with the group name, which starts
            // each call in the group
              addSeparator(AnimListItem(CellType.Header, "", group, ""))
          }
          from = tamtitle.replace(group, " ").trim()
        } else if (tamtitle != prevtitle)
        // Not a group but a different call
        // Put out a header with this call
          addSeparator(AnimListItem(CellType.Header, "", "$tamtitle from", ""))
        //  Build list item for this animation
        prevtitle = tamtitle
        prevgroup = group
        // Put out a selectable item
        val i = animListItems.count()
        when {
          group.isBlank() && group.length > 0 ->
            addItem(tam, AnimListItem(CellType.Plain, tamtitle, from, group, i))
          group.isNotEmpty() ->
            addItem(tam, AnimListItem(CellType.Indented, tamtitle, from, group, i))
          else ->
            addItem(tam, AnimListItem(CellType.Indented, tamtitle,
                from, "$tamtitle from", i))
        }
      }
      //  Unblock count, could be zero here but probably not
      itemsLeft -= 100
      if (itemsLeft <= 0)
        later {
          Application.sendMessage(Request(Request.Action.ANIMATION_READY, request))
        }
    }
  }

  private fun addSeparator(item: AnimListItem) {
    val v = TextView(item.name).apply {
      textSize = 20
    }
    alview.addItem(item,v)
  }

  //  Select an animation based on the name, such as
  //  "Fan the Top from Right-Hand Waves"
  //  Returns true if animation was found
  fun selectAnimationByName(animname:String):Boolean =
        animListItems.firstOrNull {
          animname.w.lc == it.fullname.w.lc }?.also {
          selectAnimation(it)
        } != null

  fun selectFirstAnimation() {
    selectAnimation(animListItems.first { it.animnumber >= 0 })
  }

  private fun selectAnimation(item:AnimListItem) {
    selectanim?.isSelected = false
    selectanim = item.view
    item.view?.isSelected = true
    Application.sendMessage(Request.Action.ANIMATION,
        "link" to link,
        "name" to item.fullname,
        "title" to item.title,  // needed for definition
        "animnum" to item.animnumber.toString())
  }

  private fun addItem(tam: TamElement, item: AnimListItem) {
    animListItems.add(item)
    itemsLeft += 1
    val v = SelectablePanel()
    alview.addItem(item,v)
    item.view = v
    if (item.animnumber >= 0) {
      v.clickAction {
        selectAnimation(item)
      }
    }
    TamUtils.tamXref(tam) { tamref ->
      val difficulty = "0${tamref.attr("difficulty")}".i
      if (difficulty > 0)
        alview.keyView.show()
      v.clear()
      v.backgroundColor =
          if (item.celltype == CellType.Separator || item.celltype == CellType.Header)
            Color(0x804080)
          else when (difficulty) {
            3 -> EXPERT
            2 -> HARDER
            1 -> COMMON
            else -> WHITE
          }
      //  select the first animation at start
      if (item.animnumber == 0) {
        v.isSelected = true
        selectanim = v
      }
      v.textView(if (item.name == "from") tamref.attr("from") else item.name) {
        textSize = 20
      }
      item.fullname = when {
        item.name == "from" -> item.title + " from " + tamref.attr("from")
        item.group.isNotBlank() -> item.group + " " + item.name
        else -> item.name
      }
      //  List of all animations completed?
      itemsLeft -= 1
      if (itemsLeft <= 0)
      later {
        Application.sendMessage(Request(Request.Action.ANIMATION_READY, request))
      }
    }
  }

}