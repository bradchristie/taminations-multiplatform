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

// import platform.CoreFoundation.*

actual fun String.decodeURI(): String {
  TODO("not implemented")
}

actual fun String.encodeURI(): String {
  TODO("not implemented")
}
actual object System {
  //  Get the current time in milliseconds
  actual fun currentTime(): Long = 0L // (CFAbsoluteTimeGetCurrent() * 1000.0).toLong()

  //  Run code after display has refreshed
  actual fun later(code: () -> Unit) {}

  //  Get the user's preferred language two-letter code
  actual val userLanguage: String
    get() = TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

  //  Convenience method to retrieve an XML document
  actual fun getXMLAsset(name: String, callback: (TamDocument) -> Unit) {}

  //  Copy text to clipboard.  Used by sequencer
  actual fun copyTextToClipboard(text: List<String>) {}

  //  Paste from clipboard.  Used by sequencer
  actual fun pasteTextFromClipboard(code: (String) -> Unit) {}

  //  Share action - copy current link to clipboard
  actual fun share() {}

  actual fun shareButton(): Button {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }

  actual fun audio(filename: String): Audio {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }


}

actual class Audio actual constructor(filename: String) {
  actual fun play() {}
}