package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
  Copyright (C) 2020 Brad Christie

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

import org.w3c.dom.HTMLBodyElement
import org.w3c.dom.HTMLElement
import kotlin.browser.document
import kotlin.dom.clear

//  ViewGroup is any view that contains child views

//  Default constructor wraps ViewGroup on an existing HTML div
actual abstract class ViewGroup(div:HTMLElement) : View(div) {

  //  Alternate constructor creates a div and wraps it
  constructor() : this(document.createHTMLElement("div"))

  actual val children = mutableListOf<View>()
  private var isDisplayed = div is HTMLBodyElement

  override var displayCode = {
    children.forEach { child -> child.displayCode() }
    isDisplayed = true
  }

  //  Add a child view in various ways
  //  appendThisView cannot be overridden by other classes
  private fun <T : View> appendThisView(child:T, code: T.()->Unit = { }) : T {
    div.appendChild(child.div)
    children.add(child)
    child.parentView = this
    if (isDisplayed) {
      child.displayCode()
    }
    child.code()
    return child
  }
  //  appendView can be overridden
  //  Various forms for creating a view or using an existing view
  actual open fun<T : View> appendView(child:T, code: T.()->Unit) : T =
      appendThisView(child,code)
  actual open fun appendView(code: View.()->Unit) = appendView(View(),code)
  open fun appendHTMLElement(tag:String,code: HTMLElement.() -> Unit = { }) =
      div.appendHTMLElement(tag,code)
  actual fun removeView(v: View) {
    v.parentView = null
    div.removeChild(v.div)
    children.remove(v)
  }

  //  Apply code to all descendants
  actual fun onDescendants(code:View.()->Unit) {
    children.forEach { child ->
      child.code()
      if (child is ViewGroup)
        child.onDescendants(code)
    }
  }

  actual open fun clear() {
    children.forEach {
      if (it is ViewGroup)
        it.isDisplayed = false
    }
    div.clear()
    children.clear()
  }

}