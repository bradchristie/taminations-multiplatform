package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.capWords
import com.bradchristie.taminations.common.r
import com.bradchristie.taminations.common.TamUtils

abstract class CodedCall(val norm:String, name:String=norm) : Call(name.capWords()) {

  companion object {

    //  "simple" calls are ones where we don't need the original text
    private val simpleCallMaker = mapOf(
        "aceydeucey" to { AceyDeucey() },
        "and" to { And() },
        "and14more" to { QuarterMore() },
        "roll" to { Roll() },
        "andspread" to { Spread() },
        "backaway" to { BackAway() },
        "beau" to { Beaus() },
        "belle" to { Belles() },
        "boxcounterrotate" to { BoxCounterRotate() },
        "boxthegnat" to { BoxtheGnat() },
        "bracethru" to { BraceThru() },
        "boy" to { Boys() },
        "californiatwirl" to { CaliforniaTwirl() },
        "center" to { Centers() },
        "center6" to { CenterSix() },
        "cloverleaf" to { Cloverleaf() },
        "courtesyturn" to { CourtesyTurn() },
        "crossfold" to { CrossFold() },
        "crossrun" to { CrossRun() },
        "cross" to { Cross() },
        "doublestarthru" to { DoubleStarThru() },
        "end" to { Ends() },
        "facein" to { FaceIn("facein","Face In") },
        "faceout" to { FaceIn("faceout","Face Out") },
        "faceleft" to { FaceIn("faceleft","Face Left") },
        "faceright" to { FaceIn("faceright","Face Right") },
        "facing" to { FacingDancers() },
        "fold" to { Fold() },
        "girl" to { Girls() },
        "hinge" to { Hinge("hinge","Hinge") },
        "singlehinge" to { Hinge("hinge","Single Hinge") },
        "partnerhinge" to { Hinge("hinge","Partner Hinge") },
        "lefthinge" to { Hinge("lefthinge","Left Hinge") },
        "leftpartnerhinge" to { Hinge("lefthinge","Left Partner Hinge") },
        "jaywalk" to { Jaywalk() },
        "ladies" to { Girls() },
        "12" to { Half() },
        "12sashay" to { HalfSashay() },
        "circulate" to { Circulate() },
        "all8circulate" to { Circulate() },
        "nothing" to { Nothing() },
        "partnertag" to { PartnerTag() },
        "passin" to { PassIn() },
        "passout" to { PassOut() },
        "passthru" to { PassThru("passthru","Pass Thru") },
        "leftpassthru" to { PassThru("leftpassthru","Left Pass Thru") },
        "point" to { Outsides("point","Points") },
        "14in" to { QuarterIn("14in","Quarter In") },
        "14out" to { QuarterIn("14out","Quarter Out") },
        "run" to { Run() },
        "separate" to { Separate() },
        "slidethru" to { SlideThru() },
        "slip" to { Slip() },
        "starthru" to { StarThru("starthru","Star Thru") },
        "steptoacompactwave" to { StepToACompactWave("","") },
        "steptoacompactlefthandwave" to { StepToACompactWave("left","") },
        "leftstarthru" to { StarThru("leftstarthru","Left Star Thru") },
        "step" to { Step() },
        "34tag" to { ThreeQuartersTag() },
        "34tagtheline" to { ThreeQuartersTag() },
        "trade" to { Trade() },
        "partnertrade" to { Trade() },
        "touch14" to { TouchAQuarter("touch14","Touch a Quarter") },
        "lefttouch14" to { TouchAQuarter("lefttouch14","Left Touch a Quarter") },
        "triplestarthru" to { TripleStarThru() },
        "tripletrade" to { TripleTrade() },
        "turnback" to { TurnBack() },
        "zoom" to { Zoom() },
        "singlewheel" to { SingleWheel("singlewheel","Single Wheel") },
        "leftsinglewheel" to { SingleWheel("leftsinglewheel","Left Single Wheel") },
        "squaretheset" to { SquareTheSet() },
        "sweep14" to { SweepAQuarter() },
        "turnthru" to { TurnThru("turnthru","Turn Thru") },
        "leftturnthru" to { TurnThru("leftturnthru","Left Turn Thru") },
        "twice" to { Twice("twice","Twice") },
        "gotwice" to { Twice("twice","Go Twice") },
        "verycenter" to { VeryCenters() },
        //  standard Walk and Dodge from waves, columns, etc
        //  also Centers Walk and Dodge goes through here
        "walkanddodge" to { WalkandDodge("walkanddodge","Walk and Dodge") },
        "wheelaround" to { WheelAround("wheelaround","Wheel Around") },
        "withtheflow" to { WithTheFlow() },
        "reversewheelaround" to { WheelAround("reversewheelaround","Reverse Wheel Around") },
        "zig" to { Zig("zig","Zig") },
        "zag" to { Zig("zag","Zag") },
        "zigzig" to { ZigZag("zigzig","Zig Zig") },
        "zigzag" to { ZigZag("zigzag","Zig Zag") },
        "zagzig" to { ZigZag("zagzig","Zag Zig") },
        "zagzag" to { ZigZag("zagzag","Zag Zag") }
    )

    //  More complex calls where the text is needed either to select
    //  the correct variation or to echo the expected name
    private val complexCallMaker = mapOf(
        "head" to { norm:String,call:String -> HeadsSides(norm,call) },
        "lead" to { norm:String,call:String -> Leaders(norm,call) },
        "side" to { norm:String,call:String -> HeadsSides(norm,call) },
        "trail" to { norm:String,call:String -> Trailers(norm,call) },
        "112" to { norm:String,call:String -> OneAndaHalf(norm,call) }
    )

    //  Note that String.matches(Regex) requires that the Regex match the entire String
    //  Here we hack the "in" operator to use in the match below
    operator fun Regex.contains(s:String):Boolean = s.matches(this)
    private const val specifier = "\\s*(boys?|girls?|beaus?|belles?|centers?|ends?|leaders?|trailers?|heads?|sides?|very centers?)\\s*"
    fun getCodedCall(callname:String):CodedCall? {
      val callnorm = TamUtils.normalizeCall(callname)
      //  Most calls can be found by a lookup in one of the maps
      return simpleCallMaker[callnorm]?.invoke() ?:
             complexCallMaker[callnorm]?.invoke(callnorm,callname) ?:
        //  More complex cases need to be parsed by a regex
             when (callnorm) {
        in "(cross)?cloverand(\\w.*)".r -> CloverAnd(callnorm,callname)
        in "out(er|sides?)(2|4|6)?".r -> Outsides(callnorm,callname)
        in "in(ner|sides?)(2|4|6)?".r -> Insides(callnorm,callname)
        in "center(2|4|6)".r -> Insides(callnorm,callname)
        //  Boys Walk Girls Dodge etc
        //  Also handles Heads Boy Walk Girl Dodge
        in "${specifier}walk(and)?${specifier}dodge".r -> WalkandDodge(callnorm,callname)
        //  Head Boy Walk Head Girl Dodge etc
        in "${specifier}${specifier}walk(and)?${specifier}${specifier}dodge".r -> WalkandDodge(callnorm,callname)
        in "(left)?spinthewindmill(left|right|in|out|forward)".r -> SpinTheWindmill(callnorm,callname)
        in "_windmill(in|out|left|right|forward)".r -> Windmillx(callnorm,callname)
        in "(left)?squarethru(1|2|3|4|5|6|7)?".r -> SquareThru(callnorm,callname)
        in "(left)?splitsquarethru(2|3|4|5|6|7)?".r -> SplitSquareThru(callnorm,callname)
        in "(head|side)start.+".r ->
          //  Don't want to match Sides Star Thru e.g.
          if (callname.toLowerCase() in ".*\\bstart\\b.*".r)
            HeadsStart(callnorm,callname)  else null
        in "circleby.*".r -> CircleBy(callnorm,callname)
        in "while.+".r -> While(callnorm,callname)
        else -> null
      }
    }
  }

  //  Any XML files that might be needed to apply a call
  open val requires:List<String> = listOf()

}