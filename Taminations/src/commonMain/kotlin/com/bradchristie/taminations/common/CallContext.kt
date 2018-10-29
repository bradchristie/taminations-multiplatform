package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.common.calls.Action
import com.bradchristie.taminations.common.calls.Call
import com.bradchristie.taminations.common.calls.CodedCall
import com.bradchristie.taminations.common.calls.XMLCall
import com.bradchristie.taminations.platform.*
import kotlin.math.PI

class CallContext {

  companion object {

    //  XML files that have been loaded
    val loadedXML = mutableMapOf<String,TamDocument>()

    //  Angle of d2 as viewed from d1
    //  If angle is 0 then d2 is in front of d1
    //  Angle returned is in the range -pi to pi
    fun angle(d1: Dancer, d2: Dancer):Double =
        d2.location.concatenate(d1.tx.inverse()).angle

    //  Distance between two dancers
    fun distance(d1: Dancer, d2: Dancer) =
        (d1.location - d2.location).length

    //  Angle of dancer to the origin
    fun angle(d: Dancer): Double =
        Vector().preConcatenate(d.tx.inverse()).angle

    //  Distance of one dancer to the origin
    fun distance(d1: Dancer) = d1.location.length

    //  Other geometric interrogatives
    fun isFacingIn(d: Dancer): Boolean {
      val a: Double = angle(d).abs
      return !a.isApprox(PI / 2) && a < PI / 2
    }

    fun isFacingOut(d: Dancer): Boolean {
      val a: Double = angle(d).abs
      return !a.isApprox(PI / 2) && a > PI / 2
    }

    //  Curried functions, test if dancer d2 is directly in front, back. left, right of dancer d1
    fun isInFrontF(d1: Dancer) = { d2: Dancer -> d1 != d2 && angle(d1, d2).angleEquals(0.0) }
    fun isInFront(d1:Dancer,d2:Dancer) = isInFrontF(d1)(d2)

    fun isInBackF(d1: Dancer) = { d2: Dancer -> d1 != d2 && angle(d1, d2).angleEquals(PI) }
    fun isInBack(d1:Dancer,d2:Dancer) = isInBackF(d1)(d2)

    fun isLeftF(d1: Dancer) = { d2: Dancer -> d1 != d2 && angle(d1, d2).angleEquals(PI / 2) }
    fun isLeft(d1: Dancer, d2: Dancer) = isLeftF(d1)(d2)

    fun isRightF(d1: Dancer) = { d2: Dancer -> d1 != d2 && angle(d1, d2).angleEquals(3 * PI / 2) }
    fun isRight(d1: Dancer, d2: Dancer) = isRightF(d1)(d2)

    //  Load all XML files that might be used to interpret a call
    fun loadCalls(calltext:List<String>, allFilesLoaded:()->Unit) {
      var numfiles = 100  // make sure all possibilities are checked
      val loadOneFile = { link: String ->
        if (!loadedXML.containsKey(link)) {
          numfiles += 1
          System.getXMLAsset(link) {
            //  TODO check for xref
            loadedXML[link] = it
            numfiles -= 1
            if (numfiles == 0)
              allFilesLoaded()
          }
        }
      }
      calltext.forEach { line ->
        line.minced().forEach { name ->
          //  Load any animation files that match
          val query = Regex(TamUtils.callnameQuery(name))
          val callfiles = TamUtils.calllistdata.filter {
            it.text.matches(query)
          }
          callfiles.forEach {
            loadOneFile(it.link)
          }
          //  Check for coded calls that require xml files
          CodedCall.getCodedCall(name)?.requires?.forEach { loadOneFile(it) }
        }
      }
      //  All possibilities checked - remove cap
      numfiles -= 100
      if (numfiles == 0)
        allFilesLoaded()
    }

  }  // end of companion object

  var callname = ""
  var level = LevelObject.find("b1")
  var callstack = mutableListOf<Call>()
  var dancers = listOf<Dancer>()
  var nosync = false  //  Set to true to do calls asynchronously
  private var source: CallContext? = null
  private val genderMap = mapOf("boy" to Gender.BOY, "girl" to Gender.GIRL, "phantom" to Gender.PHANTOM)

  //  For cases where creating a new context from a source,
  //  get the dancers from the source and clone them.
  //  The new context contains the dancers in their current location
  //  and no paths.
  constructor(source: CallContext, sourcedancers: List<Dancer> = source.dancers) {
    dancers = sourcedancers.map { it.animateToEnd(); Dancer(it) }
    this.source = source
  }

  //  Create a context from an array of Dancer
  constructor(sourcedancers: Array<Dancer>) {
    dancers = sourcedancers.map { it.animateToEnd(); Dancer(it) }
  }

  //  Create a context from a formation defined in XML
  //  The element passed in can be either a "tam" from
  //  an animation, or a formation
  constructor(tam: TamElement) {
    val numberArray = TamUtils.getNumbers(tam)
    val coupleArray = TamUtils.getCouples(tam)
    val f = if (tam.hasAttribute("formation"))
      TamUtils.getFormation(tam.attr("formation"))
    else
      tam.evalXPath("formation").firstOrNull() ?: tam
    dancers = f.evalXPath("dancer").mapIndexed { i, element ->
      //  TODO later this assumes square geometry
      //  Make sure each dancer in the list is immediately followed by its
      //  diagonal opposite.  Required for mapping.
      listOf(
          Dancer(numberArray[i * 2], coupleArray[i * 2],
              genderMap[element.attr("gender")]!!,
              Color.WHITE, // color not important, these are never displayed
              Matrix()
                  .preTranslate(element.attr("x").d, element.attr("y").d)
                  .preRotate(element.attr("angle").d.toRadians),
              Geometry.getGeometry(Geometry.SQUARE).first(),
              listOf()
          ),
          Dancer(numberArray[i * 2 + 1], coupleArray[i * 2 + 1],
              genderMap[element.attr("gender")]!!,
              Color.WHITE,
              Matrix()
                  .preTranslate(element.attr("x").d, element.attr("y").d)
                  .preRotate(element.attr("angle").d.toRadians),
              Geometry.getGeometry(Geometry.SQUARE)[1],
              listOf()
          )

      )
    }.flatten()
  }

  //  Get the active dancers, e.g. for "Boys Trade" the boys are active
  val actives:List<Dancer> get() = dancers.filter { it.data.active }

  //  For convenience, methods forwarded to the companion object
  fun angle(d1: Dancer, d2: Dancer):Double = Companion.angle(d1, d2)
  fun angle(d1: Dancer):Double = Companion.angle(d1)

  /**
   * Append the result of processing this CallContext to it source.
   * The CallContext must have been previously cloned from the source.
   */
  fun appendToSource(): CallContext {
    dancers.forEach {
      it.clonedFrom!!.path.add(it.path)
      it.clonedFrom.animateToEnd()
    }
    if (source != null && source!!.level < level)
      source?.level = level
    return this
  }

  private fun applyCall(calltext: String): CallContext {
    interpretCall(calltext)
//    checkForAction(calltext)
    performCall()
    appendToSource()
    return this
  }

  private fun checkForAction(calltext:String) {
    if (callstack.filter{c -> c is Action || c is XMLCall}.isEmpty())
      throw CallError("$calltext does nothing")
  }

  fun applyCalls(vararg calltext:String):CallContext {
    calltext.forEach {
      CallContext(this).applyCall(it)
    }
    return this
  }

  /**
   * This is the main loop for interpreting a call
   * @param calltxt  One complete call, lower case, words separated by single spaces
   */
  fun interpretCall(calltxt:String): CallContext {
    var calltext = calltxt.replace(Regex("\\s+")," ")
    var err: CallError = CallNotFoundError(calltxt)
    //  Clear out any previous paths from incomplete parsing
    dancers.forEach { it.path = Path() }
    callname = ""
    //  If a partial interpretation is found (like 'boys' of 'boys run')
    //  it gets popped off the front and this loop interprets the rest
    while (calltext.isNotEmpty()) {
      //  Try chopping off each word from the end of the call until
      //  we find something we know
      if (!calltext.chopped().any { callname ->
        var success = false
        //  First try to find an exact match in Taminations
        try {
          success = matchXMLcall(callname)
        } catch (err2: CallError) {
          err = err2
        }
        //  Then look for a code match
        try {
          success = success || matchCodedCall(callname)
        } catch (err2: CallError) {
          err = err2
        }
        //  Finally try a fuzzier match in Taminations
        try {
          success = success || matchXMLcall(callname,fuzzy=true)
        } catch (err2: CallError) {
          err = err2
        }
        if (success) {
          //  Remove the words we matched, break out of and
          //  the chopped loop, and continue if any words left
          calltext = calltext.replaceFirst(callname,"").trim()
        }
        success
      })
      //  Every combination from callwords.chopped failed
      throw err
    }
    checkForAction(calltxt)
    return this
  }

  //  Main routine to map a call to an animation in a Taminations XML file
  private fun matchXMLcall(calltext:String, fuzzy:Boolean=false):Boolean {
    val found:Boolean
    val matches:Boolean
    val ctx0 = this
    var ctx1 = this
    //  If there are precursors, run them first so the result
    //  will be used to match formations
    //  Needed for calls like "Explode And ..."
    if (callstack.isNotEmpty()) {
      ctx1 = CallContext(this)
      ctx1.callstack = callstack
      ctx1.performCall()
    }
    //  If actives != dancers, create another call context with just the actives
    val dc = ctx1.dancers.count()
    val ac = ctx1.actives.count()
    if (dc != ac) {
      //  Don't try to match unless the actives are together
      if (ctx1.actives.any { d ->
            ctx1.inBetween(d,ctx1.actives.first()).any { !it.data.active }
      })
        return false
      ctx1 = CallContext(ctx1,ctx1.actives)
    }
    //  Try to find a match in the xml animations
    val callnorm = TamUtils.normalizeCall(calltext)
    val callfiles = TamUtils.callmap[callnorm] ?: listOf<TamUtils.CallListDatum>()
    //  Found xml file with call, now look through each animation
    found = callfiles.isNotEmpty()
    matches = callfiles.any {
      loadedXML[it.link]!!.evalXPath("/tamination/tam").asSequence().filter { tam -> tam.attr("sequencer")!="no" &&
          TamUtils.normalizeCall(tam.attr("title")) == callnorm
      }.any { tam ->
        //  Calls that are gender-specific, e.g. Star Thru,
        //  are specifically flagged in the XML
        val sexy = tam.attr("sequencer") == "gender-specific"
        //  Make sure we don't mismatch heads and sides
        //  on calls that specifically refer to them
        val headsmatchsides = !tam.attr("title").contains("Heads?|Sides?".r)
        //  Try to match the formation to the current dancer positions
        val ctx2 = CallContext(tam)
        val mm = matchFormations(ctx1, ctx2, sexy=sexy, fuzzy=fuzzy,headsmatchsides=headsmatchsides)
        if (mm != null) {
          val xmlCall = XMLCall(tam,mm,ctx2)
          if (xmlCall.name in listOf(
                  "Allemande Left",
                  "Dixie Grand",
                  "Right and Left Grand")) {
            if (!checkResolution(ctx2, mm))
              throw ResolutionError()
          }
          // add XMLCall object to the call stack
          ctx0.callstack.add(xmlCall)
          ctx0.callname = callname + tam.attr("title") + " "
          // set level to max of this and any previous
          val thislevel = LevelObject.find(it.sublevel)
          if (thislevel > ctx0.level)
            ctx0.level = thislevel
          true
        }
        else
          false
      }
    }
    if (found && !matches)
      //  Found the call but formations did not match
      throw FormationNotFoundError(calltext)
    return matches
  }

  //  For calls that should only be used when the square is resolved,
  //  check that the dancers are in the correct order.
  //  This is only used for XML calls, coded calls check in their code.
  //  Since the XML dancers are resolved, the user's dancers must map
  //  to them in order plus or minus a rotation.
  //  So the mapping of the couples numbering mod 4 must be the same.
  private fun checkResolution(ctx2:CallContext, mapping:IntArray):Boolean =
      dancers.asSequence().mapIndexed { i, d ->
        (d.number_couple.d - ctx2.dancers[mapping[i]].number_couple.d + 4) % 4
      }.distinct().count() == 1

  //  Once a mapping of two formations is found,
  //  this computes the difference between the two.
  data class FormationMatchResult(
    var transform:Matrix,
    var offsets:Array<Vector>
  )
  fun computeFormationOffsets(ctx2: CallContext, mapping:IntArray):FormationMatchResult {
    var dvbest = emptyArray<Vector>()
    var dtotbest = 0.0
    //  We don't know how the XML formation needs to be turned to overlap
    //  the current formation.  So do an RMS fit to find the best match.
    val bxa = arrayOf(doubleArrayOf(0.0,0.0),
                      doubleArrayOf(0.0,0.0))
    actives.forEachIndexed{ i,d1 ->
      val v1 = d1.location
      val v2 = ctx2.dancers[mapping[i]].location
      bxa[0][0] += v1.x * v2.x
      bxa[0][1] += v1.y * v2.x
      bxa[1][0] += v1.x * v2.y
      bxa[1][1] += v1.y * v2.y
    }
    val (u,_,v) = Matrix(bxa[0][0], bxa[1][0], 0.0, bxa[0][1], bxa[1][1], 0.0).svd22()
    val ut = u.transpose()
    val rotmat = v.preConcatenate(ut)
    //  Now rotate the formation and compute any remaining
    //  differences in position
    actives.forEachIndexed { j,d2 ->
      val v1 = d2.location
      val v2 = ctx2.dancers[mapping[j]].location.concatenate(rotmat)
      dvbest += v1 - v2
      dtotbest += dvbest[j].length
    }
    return FormationMatchResult(rotmat,dvbest)
  }


  /*
   * Algorithm to match formations
   * Match dancers relative to each other, rather than compare absolute positions
   * Returns integer values for axis and quadrant directions
   *           0
   *         7 | 1
   *       6 --+-- 2
   *         5 | 3
   *           4
   * 2 cases
   *   1.  Dancers facing same or opposite directions
   *       - If dancers are lined up 0, 90, 180, 270 angles must match
   *       - Other angles match by quadrant
   *   2.  Dancers facing other relative directions (commonly 90 degrees)
   *       - Dancers must match quadrant or adj boundary
   *
   *
   *
   */
  private fun angleBin(a:Double):Int = when {
    a.angleEquals(0.0) -> 0
    a.angleEquals(PI/2) -> 2
    a.angleEquals(PI) -> 4
    a.angleEquals(-PI/2) -> 6
    a > 0 && a < PI/2 -> 1
    a > PI/2 && a < PI -> 3
    a < 0 && a > -PI/2 -> 7
    a < -PI/2 && a > -PI -> 5
    else -> -1
  }
  private fun dancerRelation(d1: Dancer, d2: Dancer): Int =
    // if (d1.startAngle.anglesEqual(d2.startAngle))
    //     angleBin(angle(d1,d2))
    //  else
    //    make this one fuzzy
    angleBin(angle(d1, d2))

  private fun matchFormations(ctx1: CallContext, ctx2: CallContext,
                              sexy:Boolean=false,
                              fuzzy:Boolean=false,
                              rotate:Boolean=false,
                              handholds: Boolean=true,
                              headsmatchsides:Boolean=true): IntArray? {
    if (ctx1.dancers.count() != ctx2.dancers.count())
      return null
    //  Find mapping using DFS
    val mapping = IntArray(ctx1.dancers.count()) { -1 }
    var bestmapping:IntArray? = null
    var bestOffset = 0.0
    val rotated = BooleanArray(ctx1.dancers.count(), { false })
    var mapindex = 0
    while (mapindex >= 0 && mapindex < ctx1.dancers.count()) {
      var nextmapping = mapping[mapindex] + 1
      var found = false
      while (!found && nextmapping < ctx2.dancers.count()) {
        //  Dancers in both contexts must be pairs of diagonal opposites
        //  Makes mapping much more efficient
        mapping[mapindex] = nextmapping
        mapping[mapindex + 1] = nextmapping xor 1
        if (testMapping(ctx1, ctx2, mapping, mapindex, sexy=sexy, fuzzy=fuzzy, handholds=handholds, headsmatchsides=headsmatchsides))
          found = true
        else
          nextmapping += 1
      }
      if (nextmapping >= ctx2.dancers.count()) {
        //  No more mappings for this dancer
        mapping[mapindex] = -1
        mapping[mapindex + 1] = -1
        //  If requested, try rotating this dancer
        if (rotate && !rotated[mapindex]) {
          ctx1.dancers[mapindex].rotateStartAngle(180.0)
          ctx1.dancers[mapindex+1].rotateStartAngle(180.0)
          rotated[mapindex] = true
        } else {
          rotated[mapindex] = false
          mapindex -= 2
        }
      } else {
        //  Mapping for this dancer found
        mapindex += 2
        if (mapindex >= ctx1.dancers.count()) {
          //  All dancers mapped
          //  Rate the mapping and save if best
          val matchResult = ctx1.computeFormationOffsets(ctx2,mapping)
          val totOffset = matchResult.offsets.fold(0.0) { s,v -> s+v.length }
          if (bestmapping == null || totOffset < bestOffset) {
            bestmapping = mapping.copyOf()
            bestOffset = totOffset
          }
          // continue to look for more mappings
          mapindex -= 2
        }
      }
    }
    return bestmapping
  }

  private fun testMapping(ctx1: CallContext, ctx2: CallContext,
                          mapping:IntArray, i:Int,
                          sexy:Boolean=false,
                          fuzzy:Boolean=false,
                          handholds:Boolean=true,
                          headsmatchsides:Boolean=true):Boolean {
    if (sexy && (ctx1.dancers[i].gender != ctx2.dancers[mapping[i]].gender))
      return false
    if (!headsmatchsides && ctx1.dancers[i].number_couple.i % 2 !=
                            ctx2.dancers[mapping[i]].number_couple.i % 2)
      return false
    return ctx1.dancers.allIndexed { j, _ ->
      if (mapping[j] < 0 || i == j)
        true
      else {
        val relq1 = dancerRelation(ctx1.dancers[i], ctx1.dancers[j])
        val relt1 = dancerRelation(ctx2.dancers[mapping[i]], ctx2.dancers[mapping[j]])
        val relq2 = dancerRelation(ctx1.dancers[j], ctx1.dancers[i])
        val relt2 = dancerRelation(ctx2.dancers[mapping[j]], ctx2.dancers[mapping[i]])
        //  If dancers are side-by-side, make sure handholding matches by checking distance
        when {
          handholds && (relq1 == 2 || relq1 == 6) && (relq2 == 2 || relq2 == 6) -> {
            val d1 = distance(ctx1.dancers[i], ctx1.dancers[j])
            val d2 = distance(ctx2.dancers[mapping[i]], ctx2.dancers[mapping[j]])
            relq1 == relt1 && relq2 == relt2 && (d1 < 2.1) == (d2 < 2.1)
          }
          fuzzy -> {
            val reldif1 = (relt1-relq1).abs
            val reldif2 = (relt2-relq2).abs
            (reldif1==0 || reldif1==1 || reldif1==7) &&
                (reldif2==0 || reldif2==1 || reldif2==7)
          }
          else -> relq1 == relt1 && relq2 == relt2
        }
      }
    }
  }

  private fun matchCodedCall(calltext: String): Boolean {
    val call = CodedCall.getCodedCall(calltext)
    if (call != null) {
      callstack.add(call)
      callname = callname + call.name + " "
      if (call.level > level)
        level = call.level
      return true
    }
    return false
  }

  //  Perform calls by popping them off the stack until the stack is empty.
  //  This doesn't run an animation, rather it takes the stack of calls
  //  and builds the dancer movements.
  fun performCall() {
    analyze()
    callstack.forEachIndexed{ i,c ->
      c.performCall(this,i)
      if (c is Action && i < callstack.count()-1) {
        analyze()
      }
    }
    callstack.forEachIndexed{ i,c -> c.postProcess(this,i) }
  }

  //  Re-center dancers
  fun center() {
    val xave = dancers.asSequence().map{it.location.x}.sum() / dancers.count()
    val yave = dancers.asSequence().map{it.location.y}.sum() / dancers.count()
    dancers.forEach {
      it.starttx = it.starttx.postTranslate(xave, yave)
    }
  }

  //  See if the current dancer positions resemble a standard formation
  //  and, if so, snap to the standard
  private val standardFormations = listOf(
      "Normal Lines Compact",
      "Normal Lines",
      "Double Pass Thru",
      "Quarter Tag",
      "Tidal Line RH",
      "Diamonds RH Girl Points",
      "Diamonds RH PTP Girl Points",
      "Hourglass RH BP",
      "Galaxy RH GP",
      "Butterfly RH",
      "O RH",
      "Sausage RH",
      "T-Bone URRD",
      "T-Bone RUUL",
      "T-Bone DLDL",
      "T-Bone RDRD",
      "Static Square"
  )
  data class BestMapping(
      var name:String,
      var mapping:IntArray,
      var offsets:Array<Vector>,
      var totOffset:Double
  )
  fun matchStandardFormation() {
    System.log("Attempting formation match")
    //  Make sure newly added animations are finished
    dancers.forEach { d -> d.path.recalculate(); d.animateToEnd() }
    //  Work on a copy with all dancers active, mapping only uses active dancers
    val ctx1 = CallContext(this)
    ctx1.dancers.forEach { d -> d.data.active = true }
    var bestMapping: BestMapping? = null
    standardFormations.forEach { f ->
      val ctx2 = CallContext(TamUtils.getFormation(f))
      //  See if this formation matches
      val mapping = matchFormations(ctx1,ctx2,sexy=false,fuzzy=true,rotate=true,handholds=false)
      if (mapping != null) {
        //  If it does, get the offsets
        val matchResult = ctx1.computeFormationOffsets(ctx2,mapping)
        //  If the match is at some odd angle (not a multiple of 90 degrees)
        //  then consider it bogus
        val angsnap = matchResult.transform.angle/(PI/4)
        System.log("    $f: $angsnap")
        val totOffset = matchResult.offsets.fold(0.0) { s,v -> s+v.length }
        //  Favor formations closer to the top of the list
        if (angsnap.isApproxInt() && (bestMapping==null || totOffset+0.1 < bestMapping!!.totOffset))
          bestMapping = BestMapping(
              f,  // only used for debugging
              mapping,
              matchResult.offsets,
              totOffset
          )
      }
    }
    if (bestMapping != null) {
      this.dancers.forEachIndexed { i,d ->
        if (bestMapping!!.offsets[i].length > 0.1) {
          //  Get the last movement
          val m = if (d.path.movelist.count() > 0) d.path.pop() else TamUtils.getMove("Stand").pop()
          //  Transform the offset to the dancer's angle
          d.animateToEnd()
          val vd = bestMapping!!.offsets[i].rotate(-d.tx.angle)
          //  Apply it
          d.path.add(m.skew(-vd.x,-vd.y))
          d.animateToEnd()
        }
      }
    }
  }

  //  Return max number of beats among all the dancers
  fun maxBeats() = dancers.fold(0.0) { v,d -> v max d.path.beats }

  //  Return all dancers, ordered by distance, that satisfies a conditional
  private fun dancersInOrder(d: Dancer, f:(Dancer)->Boolean): List<Dancer> =
      (dancers-d).asSequence().filter(f).sortedBy { distance(d, it) }.toList()

  //  Return closest dancer that satisfies a given conditional
  fun dancerClosest(d: Dancer, f:(Dancer)->Boolean): Dancer? =
      dancersInOrder(d,f).firstOrNull()

  //  Return dancer directly in front of given dancer
  fun dancerInFront(d: Dancer): Dancer? = dancerClosest(d, isInFrontF(d))
  //  Return dancer directly in back of given dancer
  fun dancerInBack(d: Dancer): Dancer? = dancerClosest(d, isInBackF(d))
  //  Return dancer directly to the right of given dancer
  fun dancerToRight(d: Dancer): Dancer? = dancerClosest(d, isRightF(d))
  //  Return dancer directly to the left of given dancer
  fun dancerToLeft(d: Dancer): Dancer? = dancerClosest(d, isLeftF(d))

  //  Return dancer that is facing the front of this dancer
  fun dancerFacing(d: Dancer): Dancer? {
    val d2 = dancerInFront(d)
    return if (d2 != null && dancerInFront(d2) == d) d2 else null
  }

  //  Return dancers that are in between two other dancers
  fun inBetween(d1: Dancer, d2: Dancer):List<Dancer> =
      dancers.filter { it != d1 && it != d2 &&
          (distance(it, d1) + distance(it, d2)).isApprox(distance(d1, d2))  }

  //  Return all the dancers to the right, in order
  fun dancersToRight(d: Dancer):List<Dancer> = dancersInOrder(d, isRightF(d))

  //  Return all the dancers to the left, in order
  fun dancersToLeft(d: Dancer):List<Dancer> = dancersInOrder(d, isLeftF(d))

  //  Return all the dancers in front, in order
  private fun dancersInFront(d: Dancer):List<Dancer> = dancersInOrder(d, isInFrontF(d))

  //  Return all the dancers in back, in order
  private fun dancersInBack(d: Dancer):List<Dancer> = dancersInOrder(d, isInBackF(d))

  //  Return outer 2, 4 , 6 dancers
  fun outer(num:Int):List<Dancer> =
    dancers.sortedBy{d -> d.location.length}.drop(dancers.count() - num)

    //  Return true if this dancer is in a wave or mini-wave
  fun isInWave(d:Dancer,d2:Dancer?=d.data.partner):Boolean {
    return d2 != null && angle(d, d2).angleEquals(angle(d2, d))
  }

  //  Return true if this dancer is part of a couple facing same direction
  fun isInCouple(d: Dancer, d2:Dancer?=d.data.partner):Boolean {
    return d2 != null && d.tx.angle.angleEquals(d2.tx.angle)
  }

  //  Return true if this dancer is in tandem with another dancer
  fun isInTandem(d: Dancer):Boolean = when {
      d.data.trailer ->  this.dancerInFront(d)!!.data.leader
      d.data.leader -> this.dancerInBack(d)!!.data.trailer
      else -> false
  }

/*
  //  Return true if this is 4 dancers in a box
  fun isBox():Boolean =
      //  Must have 4 dancers
      dancers.count() == 4 &&
      //  Each dancer must have a partner
      //  and must be either a leader or a trailer
      dancers.all { d -> d.data.partner != null && (d.data.leader || d.data.trailer) }
*/

  //  Return true if this is 4 dancers in a line
  fun isLine():Boolean =
      //  Must have 4 dancers
      dancers.count() == 4 &&
          //  Each dancer must have right or left shoulder to origin
          dancers.all { d -> angle(d).abs.isApprox(PI / 2) } &&
         //  All dancers must either be on the y axis
           (dancers.all { d -> d.location.x.isApprox(0.0) } ||
         //  or on the x axis
           dancers.all {d -> d.location.y.isApprox(0.0) })

  //  Return true if 8 dancers are in 2 general lines of 4 dancers each
  fun isLines():Boolean =
    dancers.all {
      d -> dancersToRight(d).count() + dancersToLeft(d).count() == 3
    }

  //  Return true if 8 dancers are in 2 general columns of 4 dancers each
  fun isColumns():Boolean =
      dancers.all {
        d -> dancersInFront(d).count() + dancersInBack(d).count() == 3
      }

  //  Return true if 8 dancers are in two-faced lines
  fun isTwoFacedLines():Boolean =
      isLines() &&
      dancers.all { d -> isInCouple(d) } &&
      dancers.asSequence().filter { d -> d.data.leader }.count() == 4 &&
      dancers.asSequence().filter { d -> d.data.leader }.count() == 4

  //  Get direction dancer would roll
  data class Rolling(val direction:Double) {
    val isLeft:Boolean get() = direction > 0.1
    val isRight:Boolean get() = direction < -0.1
  }
  fun roll(d:Dancer):Rolling {
    //  Look at the last curve of the past, excluding post-processing adjustments
    var beats = 0.0
    return Rolling(d.path.movelist.first { move ->
      beats += move.beats
      beats >= d.data.actionBeats
    }.brotate.rolling())
  }


  //  Level off the number of beats for each dancer
  fun extendPaths() {
    //  get the longest number of beats
    val maxb = maxBeats()
    //  add that number as needed by using the "Stand" move
    dancers.forEach { d ->
      d.data.actionBeats = d.path.beats
      val b = maxb - d.path.beats
      if (b > 0)
        d.path.add(TamUtils.getMove("Stand").changebeats(b))
    }
  }

  //  Find the range of the dancers current position
  //  For now we assume the dancers are centered
  //  and return a vector to the max 1st quadrant rectangle point
  //  not currently used
  //fun bounds():Vector3D =
  //    dancers.map { it.location }.reduce { v1, v2 -> Vector3D(v1.x max v2.x, v1.y max v2.y) }

  fun analyze() {
    dancers.forEach { d ->
      d.animateToEnd()
      d.data.beau = false
      d.data.belle = false
      d.data.leader = false
      d.data.trailer = false
      d.data.partner = null
      d.data.center = false
      d.data.end = false
      d.data.verycenter = false
    }
    var istidal = false
    dancers.forEach { d1 ->
      var bestleft: Dancer? = null
      var bestright: Dancer? = null
      var leftcount = 0
      var rightcount = 0
      var frontcount = 0
      var backcount = 0
      dancers.filter { it != d1 }.forEach { d2 ->
        //  Count dancers to the left and right,
        //  and find the closest on each side
        if (isRightF(d1)(d2)) {
          rightcount += + 1
          if (bestright == null || distance(d1, d2) < distance(d1, bestright!!))
            bestright = d2
        }
        else if (isLeftF(d1)(d2)) {
          leftcount += 1
          if (bestleft == null || distance(d1, d2) < distance(d1, bestleft!!))
            bestleft = d2
        }
        //  Also count dancers in front and in back
        else if (isInFrontF(d1)(d2))
          frontcount += 1
        else if (isInBackF(d1)(d2))
          backcount += 1
      }
      //  Use the results of the counts to assign belle/beau/leader/trailer
      //  and partner
      if (leftcount % 2 == 1 && rightcount % 2 == 0 &&
          distance(d1, bestleft!!) < 3) {
        d1.data.partner = bestleft
        d1.data.belle = true
      }
      else if (rightcount % 2 == 1 && leftcount % 2 == 0 &&
          distance(d1, bestright!!) < 3) {
        d1.data.partner = bestright
        d1.data.beau = true
      }
      if (frontcount % 2 == 0 && backcount % 2 == 1)
        d1.data.leader = true
      else if (frontcount % 2 == 1 && backcount % 2 == 0)
        d1.data.trailer = true
      //  Assign ends
      if (rightcount == 0 && leftcount > 1)
        d1.data.end = true
      else if (leftcount == 0 && rightcount > 1)
        d1.data.end = true
      else if (frontcount == 3 && backcount == 0)
        d1.data.end = true
      else if (backcount == 3 && frontcount == 0)
        d1.data.end = true
      //  The very centers of a tidal wave are ends
      //  Remember this special case for assigning centers later
      if (rightcount == 3 && leftcount == 4 ||
          rightcount == 4 && leftcount == 3) {
        d1.data.end = true
        istidal = true
      }
    }
    //  Analyze for centers and very centers
    //  Sort dancers by distance from center
    val dorder = dancers.sortedBy{d -> d.location.length}
    //  The 2 dancers closest to the center
    //  are centers (4 dancers) or very centers (8 dancers)
    if (!dorder[1].location.length.isApprox(dorder[2].location.length)) {
      if (dancers.count() == 4) {
        dorder.first().data.center = true
        dorder[1].data.center = true
      } else {
        dorder.first().data.verycenter = true
        dorder[1].data.verycenter = true
      }
    }
    // If tidal, then the next 4 dancers are centers
    if (istidal)
      listOf(2, 3, 4, 5).forEach { i -> dorder[i].data.center = true }
    //  Otherwise, if there are 4 dancers closer to the center than the other 4,
    //  they are the centers
    else if (dancers.count() > 4 &&
        !distance(dorder[3]).isApprox(distance(dorder[4])))
      listOf(0, 1, 2, 3).forEach { i -> dorder[i].data.center = true }
  }

}