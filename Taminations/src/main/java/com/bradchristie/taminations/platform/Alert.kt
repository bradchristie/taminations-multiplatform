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

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.bradchristie.taminations.Taminations.Companion.context
import com.bradchristie.taminations.common.Color


actual class Alert actual constructor(title:String)
  : LinearLayout(Direction.HORIZONTAL) {

  class AndroidAlert : DialogFragment() {

    var okCode = { }
    var hasCancel = false
    var title = ""
    val content = LinearLayout(Direction.VERTICAL).apply {
      borders.width = 1
      backgroundColor = Color.WHITE
      /* No need to make our own title, Android does it
      textView(title) {
        textStyle = "bold"
        backgroundColor = Color.BLUE.darker()
        textColor = Color.WHITE
        paddings = 8
      }  */
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
      val builder = AlertDialog.Builder(activity)
      builder.setTitle(title)
      builder.setView(content.div)
      builder.setPositiveButton("Ok") { _, _ -> okCode() }
      if (hasCancel)
        builder.setNegativeButton("Cancel") { _,_ -> }
      return builder.create()
    }

  }

  private val androidAlert = AndroidAlert()

  init {
    androidAlert.title = title
    //  Users's code includes all customization
    //  including call to okAction
    System.later {
      //  So now we are ready to show the alert
      androidAlert.show(context.fragmentManager, "alert")
    }
  }

  actual fun okAction(cancel:Boolean, code:()->Unit) {
    androidAlert.okCode = code
    if (cancel)
      androidAlert.hasCancel = true
  }

  override fun <T : View> appendView(child: T, code: T.() -> Unit) =
      androidAlert.content.appendView(child, code).apply {
        margins = 8
      }

}