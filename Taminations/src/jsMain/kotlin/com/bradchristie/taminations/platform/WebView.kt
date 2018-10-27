package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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


//  Class to show a web page


import com.bradchristie.taminations.platform.System.later
import org.w3c.dom.HTMLIFrameElement

actual class WebView
  actual constructor(src:String) : View() {

  private val layout = RelativeLayout(div)
  @JsName("iframeFrame")
  private var frame = layout.appendHTMLElement("iframe") {
    style.width = "100%"
    style.height = "100%"
    style.overflowY = "auto"
  } as HTMLIFrameElement

  init {
    frame.onload = { _ ->
      eval("showPlatformElements('dom')") { }
      if (src.contains("about")) {
        eval("loadFilesInBackground()") { }
      }
    }
    frame.src = "assets/$src"
    frame.style.overflowY = "auto"
  }

  actual fun setSource(src:String, afterload:WebView.()->Unit) {
    layout.clear()
    frame = layout.appendHTMLElement("iframe") {
      //  Set props here as by now window has been sized
      style.width = "100%"
      style.height = "100%"
      style.overflowY = "auto"
    } as HTMLIFrameElement
    frame.src = "assets/$src"
    frame.onload = { _ ->
      later {
        afterload()
        eval("showPlatformElements('dom')") { }
      }
    }
  }

  actual fun eval(script:String,code:(String)->Unit) {
    if (frame.contentWindow != null) {
      val retval = kotlin.js.eval("this.iframeFrame.contentWindow.$script")
      code(retval as? String ?: "")
    }
  }

}
