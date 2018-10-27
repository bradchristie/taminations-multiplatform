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

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent.ACTION_UP
import android.view.KeyEvent.KEYCODE_ENTER
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.bradchristie.taminations.Taminations

actual class TextInput : View() {

  override val div = android.widget.EditText(Taminations.context)

  private var onReturn = { }
  private var onKey = { }

  actual var text:String
    get() = div.text.toString()
    set(value) {
      if (value=="")
        div.text.clear()
      else
        div.setText(value,TextView.BufferType.EDITABLE)
    }
  actual var hint:String
    get() = div.hint.toString()
    set(v) { div.hint = v }

  //  Bluetooth keyboard tabs to next object when Enter is pressed.
  //  That's not what we want, so capture the Enter event
  //  and send it to the sequencer
  init {
    div.setOnEditorActionListener { _, actionId, event ->
      if (event!!.keyCode == KEYCODE_ENTER && event.action == ACTION_UP)
        onReturn()
      //  Return false if user presses "Search" so Android will
      //  go ahead and hide the keyboard
      when (actionId) {
        EditorInfo.IME_ACTION_SEARCH -> false
        else -> true
      }
    }
    div.addTextChangedListener(object : TextWatcher {
      override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { }
      override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
      override fun afterTextChanged(s: Editable) {
        onKey()
      }
    })
  }


  actual fun returnAction(code:()->Unit) {
    onReturn = code
  }
  actual fun keyAction(code:()->Unit) {
    onKey = code
  }
}