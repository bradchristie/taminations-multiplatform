package com.bradchristie.taminations
/*

  Taminations Square Dance Animations
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
    System.log("Sequence Test Runner init")
    TamUtils.testAction {
      System.log("Test code activated")
      runSequencerTests()
    }
  }

  //  Run all the tests using coroutines.
  //  Since loading a script would involve callbacks needed
  //  to read XML files, we use coroutines to wrap the callbacks
  //  and make sure each test is run sequentially.
  //
  //  Eventually there may be tests to check for specific errors,
  //  but for now these test only that good sequences work.
  private fun runSequencerTests() {
    GlobalScope.launch {
      var okcount = 0
      var failcount = 0
      testSequences.forEach { (name,calls) ->
        System.log("Launching test $name")
        val result = doOneSequenceAsync(calls.split("\n")).await()
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
  private fun doOneSequenceAsync(seq:List<String>) = GlobalScope.async {
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
Allemande Left""",

      "California Twirl" to
"""Sides Star Thru
Centers California Twirl
Star Thru
Right and Left Thru """,

      "Center 6" to
"""Heads Pass the Ocean
Center 6 Turn Back
Acey Deucey """,

      "Circle By" to
"""Heads Circle By 1/2 and 1/4
Girls Pass Thru
Centers Girls Cross
Checkmate
Bend the Line
Circle By 1/4 and 3/4
Boys Run
Ferris Wheel
Centers Pass Thru
Circle By 3/4 and Recycle
Pass the Ocean
Circulate
Boys Run
Bend the Line
Star Thru
Pass Thru
Allemande Left""",

      "Clover And" to
"""Heads Pass Thru
Clover and Touch 1/4
Centers Run
Centers Touch a Quarter
Out-Roll Circulate
Girls Run
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

      "Courtesy Turn" to
"""Heads Pass Thru
Centers Courtesy Turn and Back Away
Sides Star Thru
Double Pass Thru
Ends Courtesy Turn
Pass Thru
Trade By
Allemande Left""",

      "Cross" to
"""Heads Lead Right
Boys Cross
Cross
Trade By
Girls Cross
Circulate
Boys Run
Veer Left
Bend the Line
Centers Girls Cross""",

      "Cross 2" to
"""Heads Touch a Quarter and Cross 
Slide Thru 
Step and Slide 
Cloverleaf 
Split Square Thru 
Trade By 
Veer Left 
Bend the Line 
Square Thru 2 
Allemande Left""",

      "Cross Fold" to
"""Heads Pass the Ocean
Extend
Girls Cross Fold
Boys Trade and Roll and Roll
Peel Off
Boys Cross Fold
Peel and Trail
Spin the Top
Boys Cross Fold
Girls Mix
Peel Off
Boys Cross Fold
Trail Off
Crossfire
In-Roll Circulate
Quarter In
Right and Left Grand""",

      "Cross Run" to
"""Sides Pair Off
Pass the Ocean
Girls Cross Run
Boys Trade
Recycle""",

      "Double Star Thru" to
"""Heads Pass Thru
Sides Separate
Double Star Thru
Bend the Line
Centers Double Star Thru
Quarter In
Centers Trade and Roll
Double Star Thru
Cast Off Three Quarters
Pass the Ocean
Swing Thru
Boys Run
Wheel and Deal
Allemande Left""",

      "Face In" to
"""Sides Pair Off
Pass Thru
Face In
Right and Left Thru
Face In
Double Pass Thru""",

      "Facing Dancers" to
"""Heads Touch a Quarter
Facing Dancers Pass Thru
Facing Dancers Pass Thru
Facing Dancers Pass Thru
Centers Peel and Trail
Girls Trade
Chain Reaction
Centers Trade
Split Counter Rotate
Circulate
Girls Run
Trade By
Touch a Quarter
Boys Run
Wheel and Deal
Centers Square Thru 3
Allemande Left""",

      "Fold" to
"""Heads Pass the Ocean
Extend
Ends Fold
Peel Off
Boys Fold
Girls Turn Back
Peel and Trail
Spin the Top
Boys Fold
Trail Off
Boys Run
Girls Fold
Peel Off
Crossfire
In-Roll Circulate
Girls Run
Wheel and Deal
Centers Swap Around
Allemande Left""",

      "Half" to
"""Heads Pass the Ocean
Extend
Half Split Circulate
Diamond Circulate
Flip the Diamond
Half Circulate
Boys Hinge
Chain Reaction
Wheel and Deal
Slide Thru
Acey Deucey
Wheel and Deal
Touch a Quarter
Boys Run
Bend the Line
Touch a Quarter
Circulate
Boys Run
Allemande Left""",

  "Half Sashay" to
"""Sides Half Sashay
Heads Lead Right
Touch a Quarter
Centers Trade
Girls Run
Pass Thru
Centers Half Sashay
Wheel and Deal
Boys Pass Thru
Slide Thru
Bend the Line
Touch a Quarter
Circulate
Boys Run
Right and Left Thru
Pass Thru
Trade By
Allemande Left""",

      "Sides" to
"""Four Ladies Chain 3/4
Sides Pass the Ocean
Extend
Sides Trade
Circulate
Boys Run
Touch a Quarter
Circulate
Boys Run
Veer Left
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

      "Hinge" to
"""Heads Touch a Quarter
Sides Separate and Touch 1/4
Centers Hinge
Boys Pass Thru
Centers Hinge
Centers Hinge
Acey Deucey
Extend
Boys Run
Ferris Wheel
Centers Pass Thru
Allemande Left""",

      "Leaders" to
"""Heads Touch a Quarter
Leads Turn Back
Veer Right
Leaders Wheel Around
Girls Pass Thru
Leads Trade
Load the Boat
Star Thru
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

"Outer" to """Heads Pass the Ocean
Extend
Spin the Top
Outer 4 Turn Back
Hinge
Outer 4 Turn Back
Transfer the Column
Extend
Outer 6 Turn Back
Girls Wheel and Deal
Double Pass Thru
Track Two
Acey Deucey
Acey Deucey
Boys Run
Bend the Line
Touch a Quarter
Circulate
Boys Run
Allemande Left""",

      "Partner Tag" to
"""Heads Touch a Quarter and Roll
Sides Partner Tag
Any Hand Swing Thru
Partner Tag
Trade By
Partner Tag
Wheel and Deal
Girls Pass Thru
Slide Thru
Circulate
Wheel and Deal
Allemande Left
Right and Left Grand""",

      "Quarter In" to
"""Heads Lead Right
Quarter In
Pass Thru
Quarter In
Pass Thru
Trade By
Veer Left
1/4 In
Pass Thru
Girls Flutterwheel
Boys Trade
Double Pass Thru
Cloverleaf
Girls Pass Thru
Touch a Quarter
Boys Run
Wheel and Deal
Allemande Left""",

      "Roll" to
"""Heads Lead Right and Roll
Sides Separate and Touch 1/4
Split Circulate and Roll
Scoot Back
Follow Your Neighbor
Flip the Diamond
Centers Circulate
Boys Run
Pass Thru
Wheel and Deal
Centers Square Thru 3
Allemande Left""",

      "Run" to
"""Sides Pass the Ocean
Boys Run
Girls Run
Acey Deucey
Extend
Leaders Run
Pass Thru
Centers Boys Run
In-Roll Circulate
Belles Run
Any Hand Swing Thru
Wheel and Deal
Boys Pass Thru
Touch a Quarter
Boys Circulate
Boys Run
Circulate
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

      "Separate" to
"""Heads Lead Right
Pass Thru
Ends Separate
Pass the Ocean
Hinge
Centers Trade
Centers Run
Ferris Wheel
Double Pass Thru
Girls Separate
Boys Chase Right
Hinge
Partner Tag
Trade By
Touch a Quarter
Circulate
Boys Run
Ferris Wheel
Centers Reverse Swap Around
Allemande Left""",

      "Single Wheel" to
"""Heads Lead Right
Veer Left
Girls Single Wheel
Girls Touch 1/4
Cut the Diamond
Wheel Around
Single Wheel
Split Counter Rotate
Extend
Boys Single Wheel
Boys Face Right
Boys Hinge
Boys Single Wheel
Boys Left Touch 1/4
Boys Hinge
Girls Mix
Chain Reaction
Girls Single Wheel and Roll
Boys Hinge
Ends Pass Thru
Bend the Line
Pass the Ocean
Circulate
Boys Run
Ferris Wheel
Centers Reverse Swap Around
Allemande Left""",

      "Slide Thru" to
"""Heads Pass Out
Slide Thru
Hinge
Step and Slide
Face In
Slide Thru
Horseshoe Turn
Touch a Quarter
Slip
Centers Run
Ferris Wheel
Centers Slide Thru
Centers Walk and Dodge
Clover and Slide Thru
Girls Run
Boys Slide Thru
Turn and Deal
Centers Chase Right
Centers Girls Cross
Allemande Left""",

      "Spread" to
"""Heads Touch a Quarter and Spread
Centers Pass the Ocean
Flip the Diamond
In-Roll Circulate and Spread
Split Circulate
Recycle
Pass to the Center
Double Pass Thru and Spread
Hinge
Boys Run
Pass Thru
Wheel and Deal
Centers Reverse Swap Around
Allemande Left""",

      "Square the Set" to
"""Heads Lead Right
Veer Left
Bend the Line
Pass Thru
Wheel and Deal
Centers Veer Left
Centers Bend the Line
Square the Set
Heads Pass the Ocean
Extend
Recycle
Touch a Quarter
Boys Run
Wheel and Deal
Zoom
Centers Square Thru 3
Allemande Left""",

      "Star Thru" to
"""Heads Pass the Ocean
Extend
Quarter Thru
Centers Run
Bend the Line
Star Thru
Horseshoe Turn
Star Thru
Pass Thru
Wheel and Deal
Zoom
Centers Pass Thru
Allemande Left""",

      "Sweep 1/4" to
"""Heads Lead Right
Veer Left
Wheel and Deal and Sweep a Quarter
Pass the Ocean
Boys Run
Wheel and Deal and Sweep a Quarter
Pass the Ocean
Recycle and Sweep a Quarter
Pass the Sea
Recycle and Sweep a Quarter
Touch a Quarter
Circulate
Boys Run
Allemande Left""",

      "3/4 Tag" to
"""Heads Pass the Sea
Extend
3/4 Tag the Line
Centers 3/4 Tag the Line
Outer 6 Face Right
Cut the Hourglass
Turn and Deal
Slide Thru
Pass Thru
Wheel and Deal
Zoom
Centers Pass Thru
Allemande Left""",

      "Trailers" to
"""Sides Pass the Ocean
Extend
Trailers Turn Back
Bend the Line
Touch a Quarter
Trailers Turn Back
Trade By
Touch a Quarter
Boys Trade
Boys Run
Circulate
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

      "Triple Star Thru" to
"""Sides Lead Left
Veer Right
Leads Quarter In
Triple Star Thru
Clover and Right Roll to a Wave
Chain Reaction
Swing Thru
Boys Run
Ferris Wheel
Centers Reverse Swap Around
Allemande Left""",

      "Triple Trade" to
"""Sides Pass the Ocean
Triple Trade
Acey Deucey
Extend
Spin the Top
Mix
Triple Trade""",

      "Turn Thru" to
"""Heads Lead Right
Veer Left
Bend the Line
Ends Turn Thru
Hinge
Centers Trade
Centers Touch a Quarter
Ends Slide Thru
Ends Turn Back
Boys Turn Thru
Boys Run
Centers Walk and Dodge
Flutterwheel
Right and Left Thru
Allemande Left""",

      "ZigZag" to
"""Sides Star Thru and Spread
Touch a Quarter
Zig Zig
Left Touch a Quarter
Zag Zag
Pass Thru
Wheel and Deal
Zig
Girls Run
Boys Touch a Quarter
Zag
Zag Zig
Zig
Zig Zag
Boys 1/4 In
Heads Slide Thru
Bend the Line
Brace Thru
Pass Thru
Wheel and Deal
Centers Pass Thru
Allemande Left""",

      "Zoom" to
"""Sides Touch a Quarter
Boys Zoom
Centers Zoom
Ends Separate
Circulate
Centers Run
Ends Zoom
Circulate
Centers Pass the Ocean
Cut the Diamond
Boys Run
Wheel and Deal
Zoom
Centers Square Thru 3
Allemande Left""",

      "Split Square Thru" to
"""Heads Start Split Square Thru 3
Partner Trade Boys Roll
Split Square Thru 3
Trade By and Roll
Split Square Thru 4
Trade By
Right and Left Thru
Star Thru
Pass Thru
Wheel and Deal
Centers Pass Thru
Allemande Left""",

      "Left Split Square Thru" to
"""Sides Start Left Split Square Thru
Right Roll to a Wave
Recycle
Girls Face In
Left Split Square Thru 3
Step and Slide
Boys Face In
Left Split Square Thru 5
Wheel and Deal
Double Pass Thru
Right Roll to a Wave
Swing Thru
Boys Run
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

      "Square Thru" to
"""Sides Square Thru 2
Left Square Thru 3
Right Roll to a Wave
Square Thru 3
Trade By
Pass and Roll
Mix
Left Square Thru 2
Half Tag
Swing Thru
Boys Run
Circulate
Ferris Wheel
Centers Square Thru 3
Allemande Left""",

      "Cross Over Circulate" to
"""Heads Lead Right While Sides Half Sashay
Veer Left
Boys Cross Over Circulate
Bend the Line
Brace Thru
Boys Cross Over Circulate
Girls Cross Over Circulate
Boys Cross Over Circulate
Girls Cross Over Circulate
Star Thru
Right and Left Thru
Pass Thru
Trade By
Allemande Left""",

      "Interlocked Diamond Chain Thru" to
"""Heads Lead Right
Veer Left
Trade Circulate
Boys Hinge
Centers Switch the Wave
Interlocked Diamond Chain Thru
Centers Pass Thru
Girls Chase Right
Slide
Right and Left Grand """,

      "Triangle Circulate" to
"""Sides Pass the Ocean
Extend
Switch to an Hourglass
Outside Triangle Circulate
Points Turn Back
Tandem-Based Triangle Circulate
Flip the Hourglass
Centers Hinge
Inpoint Triangle Circulate
Outpoint Triangle Circulate
Lockit
Outside Triangle Circulate
Inside Triangle Circulate
Flip the Diamond
Recycle
Pass the Ocean
Circulate
Acey Deucey
Swing Thru
Recycle
Pass Thru
Centers Cross Trail Thru
Ends Turn Back
Allemande Left """,

      "Squeeze" to
"""Heads Slide Thru and Spread
Pass Thru
Wheel and Deal
Girls Squeeze
Girls Squeeze
Beaus Girls Cross
Girls Squeeze
Girls Squeeze
Girls Turn Back
Girls Squeeze
Girls Squeeze
Boys Separate
Girls Walk and Dodge
Any Hand Swing Thru
Tag the Line
Face Right
Boys Circulate
Ferris Wheel
Double Pass Thru
Ends Trade
Star Thru
Pass Thru
Wheel and Deal
Zoom
Centers Pass Thru
Pass Thru
Allemande Left """,

      "Squeeze 2" to
"""Heads Lead Right
Veer Left
Bend the Line
Touch a Quarter
Centers Circulate
Circulate
Girls Squeeze
Butterfly Circulate
Butterfly Circulate
Boys Squeeze
Circulate 1.5
Very Centers Squeeze
Galaxy Circulate
Flip the Galaxy
All 8 Swing Thru
All 8 Recycle
Heads Slide Thru
Slide Thru
Pass Thru
Wheel and Deal
Centers Reverse Swap Around
Allemande Left """,

      "Zing" to
"""Heads Pass the Ocean
Extend
Ends Zing
Centers Zing
Double Pass Thru
Boys Left Chase
Boys Zing
Clover and Pass Thru
Star Thru
Girls Trade
Wheel and Deal
Pass to the Center
Centers Pass Thru
Allemande Left""",

  "Squeeze the Hourglass" to
"""Heads Lead Right 
Veer Left 
Switch to an Hourglass 
Squeeze the Hourglass 
Boys Turn Back 
Flip the Galaxy 
All 8 Left Swing Thru 
Boys Run 
All 8 Crossfire 
Boys Run 
All 4 Couples Right and Left Thru""",

      "Squeeze the Galaxy" to
"""Heads Lead Right 
Veer Left 
Switch to an Hourglass 
Squeeze the Hourglass 
Squeeze the Galaxy 
Flip the Hourglass 
Girls Trade 
Circulate 
Ferris Wheel 
Centers Pass Thru 
Allemande Left 
Right and Left Grand""",

      "Butterfly" to
"""Heads Lead Right 
Pass Thru 
Outer 4 Squeeze 
Centers Pass Thru 
Butterfly Pass Out 
Butterfly Clover and Left Chase 
Butterfly Centers Box Counter Rotate 
Butterfly Trade 
Butterfly Center Boys Run 
Butterfly Left Roll to a Wave 
Butterfly Magic Column Circulate 
Butterfly Boys Run 
Butterfly Belles Zoom 
Butterfly Double Pass Thru 
Boys Squeeze 
Pass Thru 
Trade By 
Star Thru 
Bend the Line 
Star Thru 
Pass Thru 
Allemande Left""",

      "O" to
"""Sides Face Out 
O Pass Out 
O Centers Trade 
O Left Chase 
O Trade and Roll 
O Left Roll to a Wave 
O Magic Column Circulate 
O Facing Dancers Pass Thru 
Facing Dancers Slide In 
Trade By 
Touch a Quarter 
Acey Deucey 
Swing Thru 
Hinge 
Boys Run 
Ferris Wheel 
Centers Pass Thru 
Allemande Left"""


  )

}