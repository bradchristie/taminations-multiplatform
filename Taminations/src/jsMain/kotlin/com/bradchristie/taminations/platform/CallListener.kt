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

actual class CallListener actual constructor(callHandler:(String)->Unit,
                                      errorHandler:(String)->Unit) {

  actual companion object {
    actual val available = false
  }

  actual fun initializeSpeechRecognizer() : Boolean = true

  actual fun pause() { }
}