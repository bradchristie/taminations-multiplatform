package com.bradchristie.taminations.common

/*

  Taminations Square Dance Animations for Web Browsers
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

import com.bradchristie.taminations.platform.*
import kotlin.math.min

object TamUtils {

  data class CallListDatum(val title:String,
                           val norm:String,
                           val link:String,
                           val sublevel:String,
                           val languages:String,
                           val audio:String)

  //  Load required XML documents, then let the program know when ready
  private var doccount = 3  // because we have 3 documents to read at startup
  lateinit var calldoc: TamDocument
  var calldata:List<CallListDatum> = listOf()
  var callmap:MutableMap<String,MutableList<CallListDatum>> = mutableMapOf()
  //  Keep a set of all words used in calls.
  //  Used to check sequencer abbreviations - don't let the use make
  //  an abbreviation for a real word.
  var words:MutableSet<String> = mutableSetOf()
  //  Build map of formations for fast retrieval
  private var formations:MutableMap<String,TamElement> = mutableMapOf()
  //  And map of moves
  private var moves:MutableMap<String,TamElement> = mutableMapOf()
  private var initCallback:() -> Unit = { }
  private var testCallback:() -> Unit = { }
  var testing = false
  init {
    System.getXMLAsset("src/calls") { doc ->
      //  Read the global list of calls and save in a local list
      //  to speed up searching
      calldoc = doc
      val nodelist = calldoc.evalXPath("/calls/call")
      calldata = nodelist.map {
        CallListDatum(
            it.attr("title"),
            normalizeCall(it.attr("title")),
            it.attr("link"),
            it.attr("sublevel"),
            it.attr("languages"),
            it.attr("audio")
        )
      }
      calldata.forEach { data ->
        data.title.split("\\s+".r).forEach {
          words.add(it.toLowerCase())
        }
        val norm = data.norm
        if (callmap.containsKey(norm))
          callmap[norm]?.add(data)
        else
          callmap[norm] = mutableListOf(data)
      }
      checkForInit()
    }
    System.getXMLAsset("src/formations") {
      it.evalXPath("/formations/formation").forEach { f ->
        formations[f["name"]] = f
      }
      checkForInit()
    }
    System.getXMLAsset("src/moves") {
      it.evalXPath("/moves/path").forEach { m ->
        moves[m["name"]] = m
      }
      checkForInit()
    }
  }

  //  Needed for web version
  fun waitForInit(callback:()->Unit) {
    initCallback = callback
  }
  fun testAction(callback:()->Unit) {
    testing = true
    if (doccount <= 0)
      testCallback()
    else
      testCallback = callback
  }
  private fun checkForInit() {
    doccount -= 1
    if (doccount == 0) {
      initCallback()
      testCallback()
    }
  }

  //  Returns animation element, looking up cross-reference if needed.
  fun tamXref(tam:TamElement, callback:(TamElement)->Unit) {
    if (tam.hasAttribute("xref-link")) {
      val link = tam["xref-link"]
      System.getXMLAsset(link) {
        var s = "//tam"
        if (tam.hasAttribute("xref-title"))
          s += "[@title='${tam.getAttribute("xref-title")}']"
        if (tam.hasAttribute("xref-from"))
          s += "[@from='${tam.getAttribute("xref-from")}']"
        if (tam.hasAttribute("xref-formation"))
          s += "[@formation='${tam.getAttribute("xref-formation")}']"
        val n = it.evalXPath(s)
        callback(n.first())
      }
    } else
      callback(tam)
  }

  fun getFormation(fname:String):TamElement = formations[fname] ?:
    throw CallError("Internal error: formation $fname not found.")

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
    val elemlist = pathelem.children("*")
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
    val pathelem = moves[movename]!!
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
  fun getMove(name:String): Path = Path(translate(moves[name]!!))

  /**
   *   Returns an array of numbers to use numbering the dancers
   */
  fun getNumbers(tam:TamElement):Array<String> {
    val paths = tam.children("path")
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
    val paths = tam.children("path")
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
      callname.toLowerCase().trim()
          .replace("\\(.*\\)".r,"")
          .replace("&","and")
          .replace("\\s+".r," ")
          .replace("[^a-zA-Z0-9_ ]".r,"")
          //  Through => Thru
          .replace("\\bthrou?g?h?\\b".r,"thru")
          //  One and a half
          .replace("(onc?e and a half)|(1 12)|(15)".r,"112")
          //  Process fractions 1/2 3/4 1/4 2/3
          //  Non-alphanums are not used in matching
          //  so these fractions become 12 34 14 23
          //  Fortunately two-digit numbers are not used in calls
          .replace("\\b12|((a|one).)?half\\b".r,"12")
          .replace("\\b(three.quarters?|34)\\b".r,"34")
          .replace("\\b(((a|one).)?quarter|14)\\b".r,"14")
          .replace("\\b23|two.thirds?\\b".r,"23")
          //  Process any other numbers
          .replace("\\bzero\\b".r,"0")
          .replace("\\b(1|onc?e)\\b".r,"1")
          .replace("\\b(2|two)\\b".r,"2")
          .replace("\\b(3|three)\\b".r,"3")
          .replace("\\b(4|four)\\b".r,"4")
          .replace("\\b(5|five)\\b".r,"5")
          .replace("\\b(6|six)\\b".r,"6")
          .replace("\\b(7|seven)\\b".r,"7")
          .replace("\\b(8|eight)\\b".r,"8")
          .replace("\\b(9|nine)\\b".r,"9")
          //  Standardize 6 by 2, 6-2, 6 2 Acey Deucey
          .replace("(six|6)\\s*(by)?x?-?\\s*(two|2)".r,"62")
          .replace("(three|3)\\s*(by)?x?-?\\s*(two|2)".r,"32")
          //  'Column' of Magic Column is optional
          .replace("magic (?!column)(?!o)(?!expand)".r,"magic column ")
          //  Use singular form
          .replace("\\b(boy|girl|beau|belle|center|end|point|head|side)s\\b".r,"$1")
          //  Misc other variations
          .replace("\\bswap(\\s+around)?\\b".r,"swap")
          .replace("\\bmen\\b".r,"boy")
          .replace("\\bwomen\\b".r,"girl")
          .replace("\\blead(er)?(ing)?s?\\b".r,"lead")
          .replace("\\btrail(er)?(ing)?s?\\b".r,"trail")
          .replace("\\b(1|3)4 tag the line\\b".r,"$14 tag")
          //  'Dixie Style' -> 'Dixie Style to a Wave'
          .replace("\\bdixie style(?! to)".r,"dixie style to a wave")
          .replace("\\bchase left\\b".r,"left chase")
          //  Accept optional "dancers" e.g. "head dancers" == "heads"
          .replace("\\bdancers?\\b".r,"")
          //  Also handle "Lead Couples" as "Leads"
          //  but make sure not to clobber "As Couples" or "Couples Hinge"
          .replace("((head|side|lead|trail|center|end).)couple".r,"$1")
          //  Finally remove non-alphanums and strip spaces
          .replace("\\W".r,"")
          .replace("\\s".r,"")

}

//  Returns list of animations from an xml document
//  This selects both tam and tamxref elements
fun TamDocument.tamList() = this.evalXPath("/tamination/*[@title]")

//  Return the main title from an animation xml doc
fun TamDocument.getTitle():String {
  val tamination = evalXPath("/tamination").first()
  return tamination.getAttribute("title")!!
}
