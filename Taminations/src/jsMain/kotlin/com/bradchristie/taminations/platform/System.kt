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

import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.XMLDocument
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Date

actual object System {

  //  Get the current time in milliseconds
  actual fun currentTime() = Date().getTime().toLong()

  //  Run code after display has refreshed
  actual fun later(code:()->Unit) {
    window.requestAnimationFrame { code() }
  }

  //  Get the user's preferred language two-letter code
  actual val userLanguage:String get() = window.navigator.language.substring(0,2)

  //  Convenience method to retrieve an XML document
  actual fun getXMLAsset(name:String, callback:(TamDocument)->Unit) {
    XMLHttpRequest().apply {
      onload = {
        val doc = responseXML!! as XMLDocument
        callback(TamDocument(doc))
      }
      onerror = {
        window.alert("Error loading $name.xml: Error $status $statusText")
      }
      ontimeout = {
        window.alert("Timeout loading $name.xml")
      }
      open("GET", "$name.xml")
      send()
    }
  }

  suspend fun getXMLAsset(name:String) =
      suspendCoroutine<TamDocument> { cont ->
        getXMLAsset(name) {
          cont.resume(it)
        }
      }

  //  readProp not used, writeProp only used for non-standard styles to
  //  turn off text select in View.kt
  //val readProp:(obj:Any,prop:String)->dynamic =
  //    js("function(obj,prop) { return obj[prop] }") as (Any,String)->dynamic
  val writeProp:(obj:Any,prop:String,value:Any)->Unit =
      js("function(obj,prop,value) { obj[prop] = value } ") as (Any,String,Any)->Unit

  //  Copy text to clipboard.  Used by sequencer
  actual fun copyTextToClipboard(text:List<String>) {
    val textarea = document.body!!.appendHTMLElement("textarea") as HTMLTextAreaElement
    textarea.value = text.joinToString("\r\n")
    textarea.select()
    document.execCommand("Copy")
    document.body!!.removeChild(textarea)
  }

  //  Paste from clipboard.  Used by sequencer
  actual fun pasteTextFromClipboard(code:(String)->Unit) {
    lateinit var textarea:HTMLTextAreaElement
    Alert("Sequencer").apply {
      textView("Press Control-V to paste")
      textarea = appendHTMLElement("textarea") as HTMLTextAreaElement
      textarea.rows = 10
      textarea.cols = 40
      later {
        textarea.focus()
      }
      okAction(cancel=true) {
        code(textarea.value)
      }
    }
  }

  //  Share action - copy current link to clipboard
  actual fun share() {
    copyTextToClipboard(listOf(window.location.href))
    Alert("Taminations").apply {
      textView("Link copied to clipboard")
      okAction { }
    }
  }
  actual fun shareButton():Button = Button("Share")

  actual fun audio(filename:String) = Audio(filename)

  actual fun log(msg:String) {
    console.log(msg)
  }
}

actual external class Audio actual constructor(filename:String) {
  actual fun play()
}

//  For some reason these are not in the Kotlin JS library
external fun encodeURIComponent(str:String):String
external fun decodeURIComponent(str:String):String
//  And we will make them String methods
actual fun String.encodeURI():String = encodeURIComponent(this)
actual fun String.decodeURI():String = decodeURIComponent(this)
