package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations
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
import android.widget.SeekBar
import com.bradchristie.taminations.Taminations
import com.bradchristie.taminations.common.d
import com.bradchristie.taminations.common.i

actual class Slider : View() {

  override val div = android.widget.SeekBar(Taminations.context).apply {
    max = 1000
    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onStartTrackingTouch(seekBar: SeekBar?) { }
      override fun onStopTrackingTouch(seekBar: SeekBar?) { }
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (fromUser)
          slideCode(progress.d / 10.0)
      }
    })
  }

  private var slideCode:(Double)->Unit = { }

  actual fun slideAction(code:(Double)->Unit) {
    slideCode = code
  }

  //  param for setValue ranges from 0 to 100
  actual fun setValue(v:Double) { div.progress = (v*10).i }

}