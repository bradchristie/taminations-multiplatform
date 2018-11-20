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

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import com.bradchristie.taminations.Application
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.Color
import com.bradchristie.taminations.common.i
import org.w3c.dom.Document
import java.io.InputStream
import java.net.URLDecoder
import java.net.URLEncoder
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

actual fun String.encodeURI():String = URLEncoder.encode(this,"UTF-8")
actual fun String.decodeURI():String = URLDecoder.decode(this,"UTF-8")

//  Android color needs an alpha value in high byte
val Color.a:Int get() = color or 0xff000000.i

actual object System {

  actual fun currentTime() = java.lang.System.currentTimeMillis()

  actual fun later(code:()->Unit) {
    android.os.Handler().post {
      code()
    }
  }

  //  local was deprecated at API 24
  //  but we support API >= 16
  @Suppress("DEPRECATION")
  actual val userLanguage = Taminations.context.resources.configuration.locale.language ?: "en"

  actual fun getXMLAsset(name:String, callback:(TamDocument)->Unit) {
    val dbf: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
    val db: DocumentBuilder = dbf.newDocumentBuilder()
    val istr: InputStream = Taminations.context.assets.open(name.replace(Regex("\\..*"),"")+".xml")
    val doc: Document = db.parse(istr)
    istr.close()
    callback(TamDocument(doc))
  }

  //  Copy text to clipboard.  Used by sequencer
  actual fun copyTextToClipboard(text:List<String>) {
    val clipboard = Taminations.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Square Dance Calls", text.joinToString("\n"))
    clipboard.primaryClip = clip
  }

  //  Paste from clipboard.  Used by sequencer
  actual fun pasteTextFromClipboard(code:(String)->Unit) {
    val clipboard = Taminations.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    if (clipboard.hasPrimaryClip()) {
      @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS") val pasteText = clipboard.primaryClip.getItemAt(0).text
      code(pasteText.toString())
    }
  }

  //  Share the current location
  actual fun share() {
    val url = "https://www.tamtwirlers.org/taminations/index.html#" +
        Application.location
    val sendIntent = Intent()
        .setAction(Intent.ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT,url)
    Taminations.context.startActivity(Intent.createChooser(sendIntent, "Share via"))
  }
  actual fun shareButton():Button {
    val button = Button("")
    @Suppress("DEPRECATION")
    button.setImage(Taminations.context.resources.getDrawable(android.R.drawable.ic_menu_share))
    return button
  }

  actual fun audio(filename:String) = Audio(filename)

  actual fun log(msg:String) {
    Log.d("Taminations",msg)
  }

}

//  Code for playing audio
actual class Audio actual constructor(private val filename:String) : MediaPlayer.OnPreparedListener {

  override fun onPrepared(mp: MediaPlayer?) { }

  actual fun play() {
    val mp = MediaPlayer()
    val afd = Taminations.context.assets.openFd(filename)
    mp.setDataSource(afd.fileDescriptor,afd.startOffset,afd.declaredLength)
    mp.setOnPreparedListener { mpp -> mpp?.start() }
    mp.setOnCompletionListener { mpc -> mpc.release() }
    mp.prepare()
  }

}