package com.bradchristie.taminations.common.calls
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

import com.bradchristie.taminations.common.capWords
import com.bradchristie.taminations.common.r

abstract class CodedCall(name:String) : Call(name.capWords()) {

  companion object {
    //  Note that String.matches(Regex) requires that the Regex match the entire String
    operator fun Regex.contains(s:String):Boolean = s.matches(this)
    private const val specifier = "\\s*(boys?|girls?|beaus?|belles?|centers?|ends?|leaders?|trailers?|heads?|sides?|very centers?)\\s*"
    fun getCodedCall(callname:String):CodedCall? {
      val c = callname.toLowerCase()
      return when (c) {
        in "and".r -> And()
        in "roll".r -> Roll()
        in "and spread".r -> Spread()
        in "sweep (a quarter|1/4)".r -> SweepAQuarter()
        in "back away".r -> BackAway()
        in "beaus?".r -> Beaus()
        in "belles?".r -> Belles()
        in "box counter rotate".r -> BoxCounterRotate()
        in "box the gnat".r -> BoxtheGnat()
        in "brace thru".r -> BraceThru()
        in "men|boys?".r -> Boys()
        in "california twirl".r -> CaliforniaTwirl()
        in "centers?\\s*(six|6)?".r ->
          if (c.contains("six|6".r)) CenterSix() else Centers()
        in "(all (eight|8) )?circulate".r -> Circulate()
        in "(cross )?clover and (\\w.*)".r -> CloverAnd(c)
        in "cloverleaf".r -> Cloverleaf()
        in "courtesy turn".r -> CourtesyTurn()
        in "cross run".r -> CrossRun()
        in "cross".r -> Cross()
        in "ends?".r -> Ends()
        in "face (in|out|left|right)".r -> FaceIn(c)
        in "facing (dancers?)?".r -> FacingDancers()
        in "fold".r -> Fold()
        in "ladies|girls?".r -> Girls()
        in "(half)|(1/2)".r -> Half()
        in "half sashay".r -> HalfSashay()
        in "heads?".r -> HeadsSides(c)
        in "(left )?(single|partner)?\\s*hinge".r -> Hinge(c)
        in "lead(er)?s?".r -> Leaders(c)
        in "nothing".r -> Nothing()
        in "(onc?e and a half)|(1 1/2)".r -> OneAndaHalf()
        in "out(er|sides?)( (2|two|4|four|6|six))?".r -> Outsides(c)
        in "partner tag".r -> PartnerTag()
        in "pass thru".r -> PassThru()
        in "points".r -> Outsides(c)
        in "(quarter|1/4) (in|out)".r -> QuarterIn(c)
        in "run".r -> Run()
        in "separate".r -> Separate()
        in "sides?".r -> HeadsSides(c)
        in "(left )?single wheel".r -> SingleWheel(c)
        in "slide thru".r -> SlideThru()
        in "slip".r -> Slip()
        in "star thru".r -> StarThru()
        in "(3.4|three quarters?) tag( the line)?".r -> ThreeQuartersTag()
        in "trailers?".r -> Trailers(c)
        in "(left )?touch (a )?(quarter|1/4)".r -> TouchAQuarter(c)
        in "(partner )?trade".r -> Trade()
        in "triple trade".r -> TripleTrade()
        in "turn back".r -> TurnBack()
        in "(left )?turn thru".r -> TurnThru(c)
        in "(go )?twice".r -> Twice()
        in "very centers?".r -> VeryCenters()
        //  standard Walk and Dodge from waves, columns, etc
        //  also Centers Walk and Dodge goes through here
        in "walk and dodge".r -> WalkandDodge(c)
        //  Boys Walk Girls Dodge etc
        //  Also handles Heads Boy Walk Girl Dodge
        in "$specifier walk (and )?$specifier dodge".r -> WalkandDodge(c)
        //  Head Boy Walk Head Girl Dodge etc
        in "$specifier $specifier walk (and )?$specifier $specifier dodge".r -> WalkandDodge(c)
        in "wheel around".r -> WheelAround()
        in "z[ai]g".r -> Zig(c)
        in "z[ai]g z[ai]g".r -> ZigZag(c)
        in "zoom".r -> Zoom()
        in "square the set" -> SquareTheSet()
        else -> null
      }
    }
  }

  //  Any XML files that might be needed to apply a call
  open val requires:List<String> = listOf()

}