package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.capWords
import com.bradchristie.taminations.common.TamUtils
import com.bradchristie.taminations.common.calls.a1.*
import com.bradchristie.taminations.common.calls.a2.*
import com.bradchristie.taminations.common.calls.b1.*
import com.bradchristie.taminations.common.calls.b2.*
import com.bradchristie.taminations.common.calls.c1.*
import com.bradchristie.taminations.common.calls.c2.*
import com.bradchristie.taminations.common.calls.c3a.SnapTheLock
import com.bradchristie.taminations.common.calls.ms.*
import com.bradchristie.taminations.common.calls.plus.*
import com.bradchristie.taminations.common.r
import com.bradchristie.taminations.common.ri

abstract class CodedCall(val norm:String, name:String=norm) : Call(name.capWords()) {

  companion object {

    //  "simple" calls are ones where we don't need the original text
    private val simpleCallMaker:Map<String,()->CodedCall> = mapOf(
        "aceydeucey" to { AceyDeucey() },
        "and" to { And() },
        "and14more" to { QuarterMore() },
        "androll" to { Roll("androll", "and Roll") },
        "around1toaline" to { AroundToALine("around1toaline","Around One to a Line") },
        "around2toaline" to { AroundToALine("around2toaline","Around Two to a Line") },
        "around1andcomeintothemiddle" to
            { AroundToALine("around1andcomeintothemiddle",
                "Around One and Come Into the Middle") },
        "roll" to { Roll("roll", "Roll") },
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
        "crossovercirculate" to { CrossOverCirculate() },
        "cross" to { Cross() },
        "doublestarthru" to { DoubleStarThru() },
        "end" to { Ends() },
        "facein" to { FaceIn("facein", "Face In") },
        "faceout" to { FaceIn("faceout", "Face Out") },
        "faceleft" to { FaceIn("faceleft","Face Left" ) },
        "faceright" to { FaceIn("faceright","Face Right" ) },
        "facing" to { FacingDancers() },
        "fold" to { Fold() },
        "girl" to { Girls() },
        "grandleftswingthru" to {
          GrandSwingThru("grandleftswingthru","Grand Left Swing Thru") },
        "grandswingthru" to { GrandSwingThru("grandswingthru","Grand Swing Thru") },
        "_grandswingleft" to { GrandSwingX("grandswingleft","Grand Swing Left") },
        "_grandswingright" to { GrandSwingX("grandswingright","Grand Swing Right") },
        "12tag" to { HalfTag("halftag", "Half Tag") },
        "left12tag" to { HalfTag("lefthalftag","Left Half Tag") },
        "hinge" to { Hinge("hinge", "Hinge") },
        "singlehinge" to { Hinge("hinge","Single Hinge") },
        "partnerhinge" to { Hinge("hinge","Partner Hinge") },
        "lefthinge" to { Hinge("lefthinge","Left Hinge") },
        "leftpartnerhinge" to { Hinge("lefthinge","Left Partner Hinge") },
        "jaywalk" to { Jaywalk() },
        "ladies" to { Girls() },
        "12" to { Fraction("12","Half") },
        "12sashay" to { HalfSashay("12sashay","Half Sashay") },
        "reverse12sashay" to { HalfSashay("reverse12sashay","Reverse Half Sashay") },
        "circulate" to { Circulate() },
        "all8circulate" to { Circulate() },
        "makemagic" to { MakeMagic() },
        "nothing" to { Nothing() },
        "partnertag" to { PartnerTag() },
        "passin" to { PassIn() },
        "passout" to { PassOut() },
        "passthru" to { PassThru("passthru","Pass Thru") },
        "leftpassthru" to { PassThru("leftpassthru","Left Pass Thru") },
        "pullby" to { PullBy("pullby","Pull By") },
        "leftpullby" to { PullBy("leftpullby","Left Pull By") },
        "point" to { Points() },
        "14in" to { QuarterIn("14in", "Quarter In") },
        "14out" to { QuarterIn("14out", "Quarter Out") },
        "14tag" to { QuarterTag("quartertag","Quarter Tag") },
        "left14tag" to { QuarterTag("leftquartertag","Left Quarter Tag") },
        "ramble" to { Ramble() },
        "run" to { Run("run", "Run") },
        "runright" to { Run("runright", "Run Right") },
        "runleft" to { Run("runleft", "Run Left") },
        "scootandramble" to { ScootAndRamble() },
        "separate" to { Separate() },
        "slideleft" to { SlideDir("slideleft","Slide Left") },
        "slideright" to { SlideDir("slideright","Slide Right") },
        "slidein" to { SlideDir("slidein","Slide In") },
        "slideout" to { SlideDir("slideout","Slide Out") },
        "slidethru" to { SlideThru() },
        "slip" to { Slip() },
        "slide" to { Slide() },
        "swing" to { Swing() },
        "slither" to { Slither() },
        "squeeze" to { Squeeze() },
        "squeezethehourglass" to { SqueezeTheHourglass() },
        "squeezethegalaxy" to { SqueezeTheGalaxy() },
        "starthru" to { StarThru("starthru","Star Thru") },
        "steptoacompactwave" to { StepToACompactWave("","") },
        "steptoacompactlefthandwave" to { StepToACompactWave("left","") },
        //  "Left" Star Thru is only used for Double/Triple Star Thru
        "_leftstarthru" to { StarThru("leftstarthru","Left Star Thru") },
        "stepahead" to { Step() },
        "step" to { Step() },
        "switchtheline" to { SwitchTheLine() },
        "tagtheline" to { TagTheLine() },
        "32aceydeucey" to { ThreeByTwoAceyDeucey() },
        "34tag" to { ThreeQuartersTag("34tag","3/4 Tag the Line") },
        "34tagtheline" to { ThreeQuartersTag("34tag","3/4 Tag the Line") },
        "left34tag" to { ThreeQuartersTag("left34tag","Left 3/4 Tag the Line") },
        "left34tagtheline" to { ThreeQuartersTag("left34tag","Left 3/4 Tag the Line") },
        "touch" to { Touch("touch","Touch") },
        "lefttouch" to { Touch("lefttouch","Left Touch") },
        "trade" to { Trade() },
        "partnertrade" to { Trade() },
        "thosewhocan" to { ThoseWhoCan() },
        "touch14" to { TouchAQuarter( "touch14","Touch a Quarter") },
        "lefttouch14" to { TouchAQuarter("lefttouch14","Left Touch a Quarter") },
        "triplestarthru" to { TripleStarThru() },
        "tripletrade" to { TripleTrade() },
        "turnback" to { TurnBack() },
        "uturnback" to { TurnBack() },
        "twisttheline" to { TwistAnything("twisttheline","Twist the Line") },
        "zoom" to { Zoom("zoom","Zoom") },
        "singlewheel" to { SingleWheel("singlewheel","Single Wheel") },
        "leftsinglewheel" to { SingleWheel("leftsinglewheel","Left Single Wheel") },
        "squaretheset" to { SquareTheSet() },
        "sweep14" to { SweepAQuarter() },
        "turnthru" to { TurnThru("turnthru","Turn Thru") },
        "leftturnthru" to { TurnThru("leftturnthru","Left Turn Thru") },
        "twice" to { Twice("twice","Twice") },
        "gotwice" to { Twice("twice","Go Twice") },
        "verycenter" to { VeryCenters() },
        "veryend" to { VeryEnds() },
        //  standard Walk and Dodge from waves, columns, etc
        //  also Centers Walk and Dodge goes through here
        "walkanddodge" to { WalkandDodge("walkanddodge","Walk and Dodge") },
        "wheelaround" to { WheelAround("wheelaround","Wheel Around") },
        "withtheflow" to { WithTheFlow() },
        "reversewheelaround" to { WheelAround("reversewheelaround",
                                              "Reverse Wheel Around") },
        "zig" to { Zig("zig", "Zig") },
        "zag" to { Zig("zag", "Zag") },
        "zigzig" to { ZigZag("zigzig", "Zig Zig") },
        "zigzag" to { ZigZag("zigzag", "Zig Zag") },
        "zagzig" to { ZigZag("zagzig", "Zag Zig") },
        "zagzag" to { ZigZag("zagzag", "Zag Zag") },
        "zing" to { Zoom("zing","Zing") },
        "toawave" to { ToAWave() },
        "kickoff" to { KickOff("kickoff","Kick Off") },
        "crosskickoff" to { KickOff("crosskickoff","Cross Kick Off") },
        "singlecrossandwheel" to { SingleCrossAndWheel() },
        "crossandwheel" to { CrossAndWheel() },
        "crosstradeandwheel" to { CrossTradeAndWheel() },
        "grandcrosstradeandwheel" to { GrandCrossTradeAndWheel() },
        "singlecrosstradeandwheel" to { SingleCrossTradeAndWheel() },
        "bendtheline" to { BendTheLine() },
        "diamondcirculate" to { DiamondCirculate() },
        "everyone" to { Everyone("everyone","Everyone") },
        "everybody" to { Everyone("everybody","Everybody") },
        "shazam" to { Shazam() },
        "counterrotate" to { CounterRotate() },
        "snapthelock" to { SnapTheLock() },
        "castoff34" to { CastOffThreeQuarters() },
        "peeltoadiamond" to { PeelToADiamond() },
        "hocuspocus" to { HocusPocus() },
        "explode" to { Explode() },
        "crossramble" to { CrossRamble() },
        "castback" to { CastBack("castback","Cast Back") },
        "crosscastback" to { CastBack("crosscastback","Cross Cast Back") },
        "horseshoeturn" to { HorseshoeTurn() },
        "scootandcrossramble" to { ScootAndCrossRamble() },
        "splitcirculate" to { SplitCirculate() },
        "dosado" to { Dosado("dosado","Dosado") },
        "leftdosado" to { Dosado("leftdosado","Left Dosado") },
        "promenadehome" to { PromenadeHome("promenadehome","Promenade Home") },
        "thek" to { TheK() },
        "halfthek" to { HalfTheK() },
        "flipyourneighbor" to { FlipYourNeighbor("flipyourneighbor","Flip Your Neighbor") },
        "flipyourcrossneighbor" to
            { FlipYourNeighbor("flipyourcrossneighbor",
                "Flip Your Cross Neighbor") },
        "flipyourcrisscrossneighbor" to
            { FlipYourNeighbor("flipyourcrisscrossneighbor",
                "Flip Your Criss Cross Neighbor") },
        "detour" to { Detour() }
    )

    //  More complex calls where the text is needed either to select
    //  the correct variation or to echo the expected name
    private val complexCallMaker:Map<String,(String,String)->CodedCall> = mapOf(
        "head" to { norm:String,call:String -> HeadsSides(norm,call) },
        "lead" to { norm:String,call:String -> Leaders(norm,call) },
        "side" to { norm:String,call:String -> HeadsSides(norm,call) },
        "trail" to { norm:String,call:String -> Trailers(norm,call) },
        "112" to { norm:String,call:String -> OneAndaHalf(norm,call) }
    )

    //  Note that String.matches(Regex) requires that the Regex match the entire String
    //  Here we hack the "in" operator to use in the match below
    operator fun Regex.contains(s:String):Boolean = s.matches(this)
    const val specifier = "\\s*(?:boys?|girls?|beaus?|belles?|centers?|ends?|lead(?:er)?s?|trail(?:er)?s?|heads?|sides?|very ?centers?)\\s*"
    fun getCodedCall(callname:String):CodedCall? {
      val callnorm = TamUtils.normalizeCall(callname)
      //  Most calls can be found by a lookup in one of the maps
      return simpleCallMaker[callnorm]?.invoke() ?:
             complexCallMaker[callnorm]?.invoke(callnorm,callname) ?:
        //  More complex cases need to be parsed by a regex
             when (callnorm) {
        in "(cross)?cloverand(\\w.*)".r -> CloverAnd(callnorm, callname)
        //  Be careful not to parse Wheel and Deal and Roll as
        //  Wheel and (Deal and Roll)
        in "(reverse)?wheeland(?!deal)(\\w.*)".r -> WheelAnd(callnorm, callname)
        in "out(er|sides?)(2|4|6)?".r -> Outsides(callnorm, callname)
        in "in(ner|sides?)(2|4|6)?".r -> Insides(callnorm,callname)
        in "center(2|4|6)".r -> Insides(callnorm,callname)
        //  Boys Walk Girls Dodge etc
        //  Also handles Heads Boy Walk Girl Dodge
        in "${specifier}walk(and)?${specifier}dodge".r ->
          WalkandDodge(callnorm, callname)
        //  Head Boy Walk Head Girl Dodge etc
        in "${specifier}${specifier}walk(and)?${specifier}${specifier}dodge".r -> WalkandDodge(callnorm, callname)
        in "(left)?spinthewindmill(left|right|in|out|forward)".r ->
          SpinTheWindmill(callnorm, callname)
        in "_windmill(in|out|left|right|forward)".r -> Windmillx(callnorm, callname)
        in "(left)?squarethru(1|2|3|4|5|6|7)?(toawave)?".r -> SquareThru(callnorm, callname)
        in "(left)?splitsquarethru(2|3|4|5|6|7)?".r -> SplitSquareThru(callnorm, callname)
        in "(head|side)start.+".r ->
          //  Don't want to match Sides Star Thru e.g.
          if (callname.toLowerCase() in ".*\\bstart\\b.*".r)
            HeadsStart(callnorm, callname) else null
        in "circleby.*".r -> CircleBy(callnorm, callname)
        in "while(the)?(others?)?.+".r -> While(callnorm,callname)
        in "(the)?other?.+".r -> While(callnorm,callname)
        in "(inside|outside|inpoint|outpoint|tandembased|wavebased)?trianglecirculate".r ->
          TriangleCirculate(callnorm, callname)
        in "minibusybut.*".r -> MiniBusyBut(callnorm, callname)
        in "(left|right)rolltoawave".r -> RollToaWave(callnorm, callname)
        in "(left|right)rollto".r -> RollTo(callnorm, callname)
        in "(and)?spread".r -> Spread(callnorm, callname)
        in "(left)?(split)?catch(1|2|3|4)".r -> Catch(callnorm, callname)
        in "butterfly.*".r -> Butterfly(callnorm, callname)
        in "zipcode\\d".r -> ZipCode(callnorm, callname)
        in "twistand.*".r -> TwistAnything(callnorm, callname)
        in "ascouples.*".r -> AsCouplesConcept(callnorm, callname)
        in "tandem.*".r -> TandemConcept(callnorm, callname)
        in "siamese.*".r -> SiameseConcept(callnorm, callname)
        in "(12|34)?(reverse)?crazy.*".r -> Crazy(callnorm, callname)
        in "(left)?verticaltagback(toawave)?".r -> VerticalTagBack(callnorm, callname)
        in "(left)?vertical(left)?(14|12|34)?tag".r -> VerticalTag(callnorm,callname)
        in "adjustto.*".r -> Adjust(callnorm,callname)
        in "bounce(the)?$specifier".r -> Bounce(callnorm, callname)
        in "(left)?tagback(toawave)?".r -> TagBack(callnorm, callname)
        in "transferand(.+)".r -> TransferAnd(callnorm, callname)
        in "(left)?turnanddeal".r -> TurnAndDeal(callnorm, callname)
        in "phantom(.+)".r -> PhantomConcept(callnorm,callname)
        in "relocate(.+)".r -> Relocate(callnorm,callname)
        in "(scootand)?(outside|point)?(out|in|left|right|(go)?(forward|asyouare))?little".r -> Little(callnorm,callname)
        in "(scootand)?little(outside|point)(in|out|left|right|(go)?(forward|asyouare))?".r -> Little(callnorm,callname)
        in "(reverse)?truck".r -> Truck(callnorm,callname)
        in "swingandcircle(12|34)?".r -> SwingAndCircle(callnorm,callname)
        in "concentric(.+)".r -> ConcentricConcept(callnorm,callname)
        in "checkpoint(.+)by(.*)".r -> CheckpointConcept(callnorm,callname)
        in "(left|right|in|out)loop(1|2|3)".r -> Loop(callnorm,callname)
        in "stagger(.+)".r -> StaggerConcept(callnorm,callname)
        in "(left|vertical){0,2}tagyour((criss)?cross)?neighbor".r -> TagYourNeighbor(callnorm,callname)
        in "castashadowcenter(go|cast)?34".r -> CastAShadow(callnorm,callname)
        in "finish.*".r -> Finish(callnorm,callname)
        in ".*(motivate|coordinate|percolate|perkup)".r ->
          AnythingConcept(callnorm,callname)
        in "\\d\\d".r -> Fraction(callnorm,callname)
        in "triplebox.*".r -> TripleBoxConcept(callnorm,callname)
        in "triple(lines?|waves?|columns?).*".r -> TripleLineConcept(callnorm,callname)
        in "rotate(14|12|34)".r -> Rotate(callnorm,callname)
        in "rotary.+".r -> Rotary(callnorm,callname)
        in "ignore.+".r -> Ignore(callnorm,callname)
        in "swing(your)?corner(and)?promenade(home)?".r -> PromenadeHome(callnorm,callname)
        in "${specifier}crossrun".r -> CrossRun(callnorm,callname)
        in "samesex(es)?.+".r -> SameSex(callnorm,callname)
        //  not ready yet    in "disconnected.+".r -> DisconnectedConcept(callnorm,callname)
        in "(left|right)?ripple.*".r  -> Ripple(callnorm,callname)
        else -> null
      }
        //  Other calls not easily handled by when expression
      ?: if (callname.matches("o .+".ri))
          OFormation(callnorm, callname) else null
      //  Anything Chain Thru should not match Square Chain Thru or others
      ?: if (callnorm.matches(".*chainthru".r) &&
            !callnorm.matches(".*(cross|eight|peel|scatter|spin|square|swing|tag)chainthru".r))
          AnythingChainThru(callnorm,callname) else null
      //  Start should not match Star Thru e.g.
      ?: if (callname.matches("start .+".ri))
          Start(callnorm,callname) else null
      ?: if (callname.matches("stretch .+".ri))
          StretchConcept(callnorm,callname) else null
    }
  }

  //  Any XML files that might be needed to apply a call
  open val requires:List<String> = listOf()

}