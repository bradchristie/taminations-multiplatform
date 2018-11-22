package com.bradchristie.taminations
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

import kotlinx.coroutines.*
import com.bradchristie.taminations.common.Request
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.platform.Alert
import com.bradchristie.taminations.platform.System
import com.bradchristie.taminations.platform.textView
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//  A class that runs a list of sequences
//  and checks that they all work properly
//  This class is instantiated by the html code of the test page
class SequenceTestRunner {

  init {
    //  We con't start testing until Taminations is fully initialized.
    //  So this sets a hook when Taminations is ready.
    TamUtils.testAction {
      System.log("Test code activated")
      runSequencerTests()
    }
  }

  //  Run all the tests using coroutines.
  //  Since loading a script would involve callbacks needed
  //  to read XML files, we use coroutines to wrap the callbacks
  //  and make sure each test is run sequentially.
  private fun runSequencerTests() {
    GlobalScope.launch {
      var okcount = 0
      var failcount = 0
      testSequences.forEach { (name,calls) ->
        val result = doOneSequence(calls.split("\n")).await()
        System.log("$name ${if (result) "OK" else "FAILURE"}")
        when (result) {
          true -> okcount += 1
          false -> failcount += 1
        }
      }
      System.log("All tests complete, $okcount succeeded, $failcount failed.")
      Alert("Sequence Tests").apply {
        textView("All tests complete, $okcount succeeded, $failcount failed.")
        okAction {  }
      }
    }
  }

  //  Load one sequence, check for success, and return the result
  //  Return value is a "Deferred",  actual value is retrieved with
  //  .await(), which forces completion of the coroutine.
  private fun doOneSequence(seq:List<String>) = GlobalScope.async {
    suspendCoroutine<Boolean> { cont ->
      var innerResult = true
      //  First set the callback code to handle the results
      Application.setTestMesseger { request ->
        //  Sequencer sends READY message when sequence has
        //  been completely processed
        if (request.action == Request.Action.SEQUENCER_READY) {
          cont.resume(innerResult)
        } else if (request.action == Request.Action.SEQUENCER_ERROR) {
          System.log("Error detected: ${request["error"]}")
          innerResult = false
        }
      }
      //  Now send the sequence.
      //  Using System.later lets the browser display catch up
      System.later {
        Application.sendRequest(
            Request.Action.SEQUENCER,
            "formation" to "Static Square",
            "calls" to seq.joinToString(";")
        )
      }
    }
  }


  private val testSequences = arrayOf(

      "Back Away" to
"""Heads Right and Left Thru and Back Away
Sides Pass the Ocean""",

      "Beaus Run" to
"""Heads Pass the Ocean
Extend
Hinge
Centers Trade
Centers Run
Beaus Run
Recycle
Touch a Quarter
Girls Circulate
Boys Run
Bend the Line
Quarter Out
Allemande Left""",

      "Belles Run" to
"""Heads Lead Right
Sides Half Sashay
Touch a Quarter
Centers Run
Bend the Line
Pass Thru
Belles Trade
Swing Thru
Recycle
Veer Left
Bend the Line
Touch a Quarter
Circulate
Boys Run
Allemande Left""",

      "Box Counter Rotate" to
"""Heads Right and Left Thru
Centers Box Counter Rotate
Double Pass Thru """,

      "Box the Gnat" to
"""Heads Box the Gnat
Sides Separate and Box the Gnat
Centers Pass In
Slide Thru
Cloverleaf
Zoom
Centers Pass Thru
Allemande Left""",

      "Brace Thru" to
"""Heads Pass the Ocean
Extend
Leads Turn Back
Brace Thru
Slide Thru
Right and Left Thru
Pass Thru
Trade By
Allemande Left"""


  )



}