package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

expect fun String.decodeURI() : String
expect fun String.encodeURI() : String

expect object System {

  //  Get the current time in milliseconds
  fun currentTime() : Long

  //  Run code after display has refreshed
  fun later(code:()->Unit)

  //  Get the user's preferred language two-letter code
  val userLanguage:String

  //  Convenience method to retrieve an XML document
  fun getXMLAsset(name:String, callback:(TamDocument)->Unit)

  //  Copy text to clipboard.  Used by sequencer
  fun copyTextToClipboard(text:List<String>)

  //  Paste from clipboard.  Used by sequencer
  fun pasteTextFromClipboard(code:(String)->Unit)

  //  Share action - copy current link to clipboard
  fun share()
  fun shareButton():Button

  fun audio(filename:String) : Audio

  fun log(msg:String)

}

expect class Audio(filename:String) {
  fun play()
}
