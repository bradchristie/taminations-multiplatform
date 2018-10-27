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

import com.bradchristie.taminations.platform.decodeURI
import com.bradchristie.taminations.platform.encodeURI

class Request(val action:Action, vararg pairs:Pair<String,String>) {

  enum class Action {
    //  Actions for page changes
    //  Some of these may also be sent as messages
    NONE,  // dummy value for no accepted actions
    STARTUP,
    ABOUT,
    SETTINGS,
    SEQUENCER,
    STARTPRACTICE,
    TUTORIAL,
    PRACTICE,
    LEVEL,
    CALLLIST,
    CALLITEM,
    ANIMLIST,
    ANIMATION,
    DEFINITION,
    ABBREVIATIONS,

    //  Other messages
    SETTINGS_CHANGED,
    ANIMATION_READY,
    ANIMATION_LOADED,
    ANIMATION_SELECTED,
    ANIMATION_PART,
    ANIMATION_PROGRESS,
    ANIMATION_DONE,
    SEQUENCER_CALLLIST,
    SEQUENCER_LISTEN,
    BUTTON_PRESS,
    SLIDER_CHANGE,
    TRANSITION_COMPLETE,
    TITLE,
    ABBREVIATIONS_CHANGED

  }
  private val params:HashMap<String,String> = hashMapOf()

  init {
    pairs.forEach { (k,v) -> params[k] = v }
  }

  //  Copy params from another request
  constructor(action:Action, from: Request) : this(action) {
    from.params.forEach { (k,v) -> params[k] = v }
  }

  //  Parse keys and values out of an URL
  constructor(url:String) :
      this(Action.valueOf(Regex("action=(\\w+)").find(url)
      ?.groupValues?.get(1)?.toUpperCase() ?: "STARTUP")) {
    url.replace("#", "")
        .split("&")
        .filter { it.isNotEmpty() }
        .forEach {
          if (it.contains("=")) {
            val (key, value) = it.split("=", limit = 2)
            if (value == "delete")
              params.remove(key.decodeURI())
            else if (key != "action")
              params[key.decodeURI()] = value.decodeURI()
          } else {
            //  Accept a param with no value as a boolean "true"
            params[it.decodeURI()] = "true"
          }
        }
  }

  operator fun get(key:String):String = params[key] ?: ""
  operator fun set(key:String, value:String) { params[key] = value }

  operator fun plus(request: Request):Request = Request("$this&$request")

  //  Convert keys and values back into a hash for an URL
  override fun toString():String =
      (params.map { (key,value) -> if (value.isNotBlank())
        key.encodeURI() + "=" + value.encodeURI()
        else
        key.encodeURI()
      } + "action=$action").joinToString(separator = "&")
  val s get() = toString()

}