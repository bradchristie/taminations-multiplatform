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


//  Class to show a web page

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.TamUtils
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
  private var doExtraStuff = true

  init {
    frame.onload = { _ ->
      eval("showPlatformElements('dom')") { }
      if (src.contains("about")) {
        eval("checkVersion(this)") { }
      }
    }
    frame.src = src
    frame.style.overflowY = "auto"
  }

  //  This method is called by JavaScript (framecode.js)
  //  when the user clicks on the Load Files button on the About page
  @JsName("loadMyFiles")
  fun loadMyFiles(ctx:WebView) {
    //  Get the list of all files
    val allMyFiles = TamUtils.calldata.map { it.link }.toMutableList()
    val totalCount = allMyFiles.count()
    var loadedCount = 0
    //  Set up recursive method to load files
    //  Completion of each file triggers the next
    var loadNext:WebView.()->Unit = { }
    val loadNextRecurse:WebView.()->Unit = {
      loadedCount += 1
      //  Show the user how many files we have loaded
      Application.titleBar.title = "Loading $loadedCount of $totalCount"
      if (allMyFiles.isNotEmpty()) {
        val file = allMyFiles.removeAt(0)
        //  Load the xml file
        System.getXMLAsset(file) {
          //  and the html file, with any images
          ctx.setSource("$file.html", loadNext)
        }
      }
      else {
        //  All files loaded, reset the About page
        ctx.setSource("info/about.html") {
          doExtraStuff = true
          eval("allFilesLoaded()") { }
        }
        Application.titleBar.title = "Taminations"
      }
    }
    //  final setup
    loadNext = loadNextRecurse
    doExtraStuff = false
    //  Start the load
    val file = allMyFiles.removeAt(0)
    System.getXMLAsset(file) {
      ctx.setSource("$file.html", loadNext)
    }
  }

  actual fun setSource(src:String, afterload:WebView.()->Unit) {
    layout.clear()
    frame = layout.appendHTMLElement("iframe") {
      //  Set props here as by now window has been sized
      style.width = "100%"
      style.height = "100%"
      style.overflowY = "auto"
    } as HTMLIFrameElement
    frame.src = src
    frame.onload = { _ ->
      later {
        afterload()
        if (doExtraStuff) {
          eval("showPlatformElements('dom')") { }
          if (src.contains("about")) {
            eval("checkVersion(this)") { }
          }
        }
      }
    }
  }

  actual fun eval(script:String,code:(String)->Unit) {
    if (frame.contentWindow != null) {
      val retval = eval("this.iframeFrame.contentWindow.$script")
      code(retval as? String ?: "")
    }
  }

}
