package com.bradchristie.taminations.common

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

import com.bradchristie.taminations.platform.*
import kotlin.math.min

object TamUtils {

  data class CallListDatum(val title:String,
                           val text:String,
                           val link:String,
                           val sublevel:String,
                           val languages:String,
                           val audio:String)

  //  Load required XML documents, then let the program know when ready
  private var doccount = 4  // because we have 4 documents to read at startup
  lateinit var calldoc: TamDocument
  lateinit var indexdoc: TamDocument
  private lateinit var formationDoc: TamDocument
  private lateinit var movementDoc: TamDocument
  var calllistdata:List<CallListDatum> = listOf()
  var callmap:MutableMap<String,MutableList<CallListDatum>> = mutableMapOf()
  //  Keep a set of all words used in calls.
  //  Used to check sequencer abbreviations - don't let the use make
  //  an abbreviation for a real word.
  var words:MutableSet<String> = mutableSetOf()
  private var initCallback:() -> Unit = { }
  init {
    System.getXMLAsset("src/calls") {
      calldoc = it
      checkForInit()
    }
    System.getXMLAsset("src/callindex") { doc ->
      //  Read the global list of calls and save in a local list
      //  to speed up searching
      indexdoc = doc
      val nodelist = indexdoc.evalXPath("/calls/call[@level!='Info']")
      calllistdata = nodelist.map {
        CallListDatum(
            it.attr("title"),
            it.attr("text"),
            it.attr("link"),
            it.attr("sublevel"),
            it.attr("languages"),
            it.attr("audio")
        )
      }
      calllistdata.forEach { data ->
        data.title.split("\\s+".r).forEach {
          words.add(it.toLowerCase())
        }
        val norm = normalizeCall(data.title)
        if (callmap.containsKey(norm))
          callmap[norm]?.add(data)
        else
          callmap[norm] = mutableListOf(data)
      }
      checkForInit()
    }
    System.getXMLAsset("src/formations") { formationDoc = it; checkForInit() }
    System.getXMLAsset("src/moves") { movementDoc = it; checkForInit() }
  }

  //  Needed for web version
  fun waitForInit(callback:()->Unit) {
    initCallback = callback
  }
  private fun checkForInit() {
    doccount -= 1
    if (doccount == 0)
      initCallback()
  }

  //  Returns animation element, looking up cross-reference if needed.
  fun tamXref(tam:TamElement, callback:(TamElement)->Unit) {
    if (tam.hasAttribute("xref-link")) {
      val link = tam.getAttribute("xref-link")!!
      System.getXMLAsset(link) {
        var s = "//tam"
        if (tam.hasAttribute("xref-title"))
          s += "[@title='${tam.getAttribute("xref-title")}']"
        if (tam.hasAttribute("xref-from"))
          s += "[@from='${tam.getAttribute("xref-from")}']"
        val n = it.evalXPath(s)
        callback(n.first())
      }
    } else
      callback(tam)
  }

  fun getFormation(fname:String):TamElement =
      formationDoc.evalXPath("/formations/formation[@name='$fname']").first()

  private fun translate(elem:TamElement):List<Movement> = when (elem.tag) {
    "path" -> translatePath(elem)
    "move" -> translateMove(elem)
    "movement" -> translateMovement(elem)
    else -> listOf()
  }

  //  Takes a path, which is an XML element with children that
  //  are moves or movements.
  //  Returns an array of movements
  fun translatePath(pathelem:TamElement):List<Movement> {
    val elemlist = pathelem.evalXPath("*")
    //  Send the result to translate
    //  to recursively process "move" elements
    return elemlist.flatMap { translate(it) }
  }

  //  Accepts a movement element from a XML file, either an animation definition
  //  or moves.xml
  //  Returns an array of a single movement
  private fun translateMovement(move:TamElement):List<Movement> = listOf(Movement(move))

  //  Takes a move, which is an XML element that references another XML
  //  path with its "select" attribute
  //  Returns an array of movements
  private fun translateMove(move:TamElement):List<Movement> {
    //  First retrieve the requested path
    val movename = move.attr("select")
    val pathelem = movementDoc.evalXPath("/moves/path[@name='$movename']").first()
    //  Get the list of movements
    val movements = translatePath(pathelem)
    //  Get any modifications
    val scaleX = if (move.hasAttribute("scaleX"))
      move.attr("scaleX").d else 1.0
    val scaleY = (if (move.hasAttribute("scaleY"))
      move.attr("scaleY").d else 1.0) *
        (if (move.hasAttribute("reflect")) -1.0 else 1.0)
    val offsetX = if (move.hasAttribute("offsetX"))
      move.attr("offsetX").d else 0.0
    val offsetY = if (move.hasAttribute("offsetY"))
      move.attr("offsetY").d else 0.0
    val hands = move.attr("hands")
    //  Sum up the total beats so if beats is given as a modification
    //  we know how much to change each movement
    val oldbeats = movements.fold(0.0) { b, m -> b + m.beats }
    val beatfactor = if (move.hasAttribute("beats"))
      move.attr("beats").d / oldbeats else 1.0
    //  Now go through the movements applying the modifications
    //  The resulting list is the return value
    return movements.map { m ->
      m.useHands(if (hands.isNotEmpty()) getHands(hands) else m.hands)
          .scale(scaleX, scaleY)
          .skew(offsetX, offsetY)
          .time(m.beats * beatfactor)
    }
  }

  /**
   *   Gets a named path (move) from the file of moves
   */
  fun getMove(name:String): Path =
      Path(translate(movementDoc.evalXPath("/moves/path[@name='$name']").first()))


  /**
   *   Returns an array of numbers to use numbering the dancers
   */
  fun getNumbers(tam:TamElement):Array<String> {
    val paths = tam.evalXPath("path")
    val retval = arrayOf("1","2","3","4","5","6","7","8",
        "","","","","","","","")
    val np = min(paths.count(),4)
    for (i in 0 until paths.count()) {
      val p = paths[i]
      val n = p.attr("numbers")
      when {
        n.length >= 3 -> { // numbers supplied in animation XML
          retval[i*2] = n.substring(0..0)
          retval[i*2+1] = n.substring(2..2)
        }
        i > 3 -> { // phantoms
          retval[i * 2] = " "
          retval[i * 2 + 1] = " "
        }
        else -> { // default numbers
          retval[i * 2] = "${i + 1}"
          retval[i * 2 + 1] = "${i + 1 + np}"
        }
      }
    }
    return retval
  }

  fun getCouples(tam:TamElement):Array<String> {
    val retval = arrayOf("1","3","1","3",
        "2","4","2","4",
        "5","6","5","6",
        " "," "," "," ")
    val paths = tam.evalXPath("path")
    for (i in 0 until paths.count()) {
      val p = paths[i]
      val c = p.attr("couples")
      if (c.isNotEmpty()) {
        retval[i*2] = c.substring(0..0)
        retval[i*2+1] = c.substring(2..2)
      }
    }
    return retval
  }

  /**  Standardize a call name to match against other names  */
  fun normalizeCall(callname:String):String =
      callname.toLowerCase().replace("&","and")
          .replace("[^a-zA-Z0-9 ]".r,"")
          //  Through => Thru
          .replace("\\bthrou?g?h?\\b".r,"thru")
          //  Process fractions 1/2 3/4 1/4 2/3
          //  Non-alphanums are not used in matching
          //  so these fractions become 12 34 14 23
          //  Fortunately two-digit numbers are not used in calls
          .replace("\\b12|((a|one).)?half\\b".r,"12")
          .replace("\\b(three.quarters?|34)\\b".r,"34")
          .replace("\\b(((a|one).)?quarter|14)\\b".r,"14")
          .replace("\\b23|two.thirds?\\b".r,"23")
          //  One and a half
          .replace("\\b1.5\\b".r,"112")
          //  Process any other numbers
          .replace("\\b(1|onc?e)\\b".r,"1")
          .replace("\\b(2|two)\\b".r,"2")
          .replace("\\b(3|three)\\b".r,"3")
          .replace("\\b(4|four)\\b".r,"4")
          .replace("\\b(5|five)\\b".r,"5")
          .replace("\\b(6|six)\\b".r,"6")
          .replace("\\b(7|seven)\\b".r,"7")
          .replace("\\b(8|eight)\\b".r,"8")
          .replace("\\b(9|nine)\\b".r,"9")
          //  Use singular form
          .replace("\\b(\\w+)s\\b".r,"$1")
          //  Accept optional "dancers" e.g. "head dancers" == "heads"
          .replace("\\bdancers?\\b".r,"")
          //  Misc other variations
          .replace("\\bswap(\\s+around)?\\b".r,"swap")
          .replace("\\bmen\\b".r,"boy")
          .replace("\\bwomen\\b".r,"girl")
          .replace("\\blead(er)?(ing)?\\b","lead")
          .replace("\\btrail(er)?(ing)?\\b".r,"trail")
          //  Finally remove non-alphanums and strip spaces
          .replace("\\W".r,"")
          .replace("\\s".r,"")


  fun callnameQuery(query:String):String =
      query.toLowerCase().replace("&","and")
          .replace("[^a-zA-Z0-9 ]".r,"")
          //  Use upper case and dup numbers while building regex
          //  so expressions don't get compounded
          //  Through => Thru
          .replace("\\bthrou?g?h?\\b".r,"THRU")
          //  Process fractions 1/2 3/4 1/4 2/3
          .replace("\\b12|((a|one).)?half\\b".r,"((A|ONE)?HALF|1122)")
          .replace("\\b(three.quarters?|34)\\b".r,"(THREEQUARTERS|3344)")
          .replace("\\b(((a|one).)?quarter|14)\\b".r,"((A|ONE)?QUARTER|1144)")
          .replace("\\b23|two.thirds?\\b".r,"(TWOTHIRDS|2233)")
          //  One and a half
          .replace("\\b1.5\\b".r,"ONEANDAHALF")
          //  Process any other numbers
          .replace("\\b(1|onc?e)\\b".r,"(11|ONE)")
          .replace("\\b(2|two)\\b".r,"(22|TWO)")
          .replace("\\b(3|three)\\b".r,"(33|THREE)")
          .replace("\\b(4|four)\\b".r,"(44|FOUR)")
          .replace("\\b(5|five)\\b".r,"(55|FIVE)")
          .replace("\\b(6|six)\\b".r,"(66|SIX)")
          .replace("\\b(7|seven)\\b".r,"(77|SEVEN)")
          .replace("\\b(8|eight)\\b".r,"(88|EIGHT)")
          .replace("\\b(9|nine)\\b".r,"(99|NINE)")
          //  Accept single and plural forms of some words
          .replace("\\bboys?\\b".r,"BOYS?")
          .replace("\\bgirls?\\b".r,"GIRLS?")
          .replace("\\bends?\\b".r,"ENDS?")
          .replace("\\bcenters?\\b".r,"CENTERS?")
          .replace("\\bheads?\\b".r,"HEADS?")
          .replace("\\bsides?\\b".r,"SIDES?")
          //  Accept optional "dancers" e.g. "head dancers" == "heads"
          .replace("\\bdancers?\\b".r,"(DANCERS?)?")
          //  Misc other variations
          .replace("\\bswap(\\s+around)?\\b".r,"SWAP (AROUND)?")

          //  Finally repair the upper case and dup numbers
          //  and make spaces optional
          .toLowerCase().replace("([0-9])\\1".r, "$1").replace("\\s+".r,"\\s*")


}

//  Returns list of animations from an xml document
//  This selects both tam and tamxref elements
fun TamDocument.tamList() = this.evalXPath("/tamination/*[@title]")

//  Return the main title from an animation xml doc
fun TamDocument.getTitle():String {
  val tamination = evalXPath("/tamination").first()
  return tamination.getAttribute("title")!!
}
