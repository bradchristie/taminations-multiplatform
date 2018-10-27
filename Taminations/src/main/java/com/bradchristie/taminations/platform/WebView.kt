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

import android.annotation.SuppressLint
import android.webkit.ValueCallback
import android.webkit.WebViewClient
import com.bradchristie.taminations.Taminations

actual class WebView actual constructor(src:String) : View() {

  @SuppressLint("SetJavaScriptEnabled")
  override val div = android.webkit.WebView(Taminations.context).apply {
    //  Turn on pinch-to-zoom, which is off(!) by default
    settings.builtInZoomControls = true
    //  Enable JavaScript so we can highlight parts of calls
    //  and switch between abbrev and full defs
    settings.javaScriptEnabled = true
  }

  init {
    setSource(src) { }
  }

  actual fun setSource(src:String, afterload:WebView.()->Unit) {
    div.webViewClient = object : WebViewClient() {
      override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
        afterload()
        eval("showPlatformElements('android')") { }
      }
    }
    div.loadUrl("file:///android_asset/$src")
  }

  private fun evaluateMyJavascript(script:String, code:(String)->Unit) {
    try {
      //  WebView.evaluateJavascript method available starting with KitKat (API 19)
      val m = div.javaClass.getMethod("evaluateJavascript", String::class.java, ValueCallback::class.java)
      m.invoke(div, script, ValueCallback<String> { value ->
        code(value ?: "")
      })
    } catch (e:Exception) {
      //  fall back to loadUrl which usually works
      div.loadUrl("javascript:$script")
    }
  }

  actual fun eval(script:String, code:(String)->Unit) =
    evaluateMyJavascript(script,code)


}