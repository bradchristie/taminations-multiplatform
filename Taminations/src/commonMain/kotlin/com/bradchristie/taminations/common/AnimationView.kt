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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.common.Color.Companion.FLOOR
import com.bradchristie.taminations.platform.*
import kotlin.math.PI

class AnimationView : Canvas() {

  companion object {
    const val SLOWSPEED =     1500.0
    const val MODERATESPEED = 1000.0
    const val NORMALSPEED =    500.0
    const val FASTSPEED =      200.0
  }


  private var looping = false
  private var speed = NORMALSPEED
  private var showGrid = false
  private var showPaths = false
  private var randomColors = Setting("Dancer Colors").s == "Random"
  private var geometry = Geometry.SQUARE
  //var parts:String = ""
  private var tam: TamElement? = null
  var dancers = arrayOf<Dancer>()
  private var interactiveDancer = -1
  private var interactiveRandom = true
  private var idancer: InteractiveDancer? = null
  private var leadin = 2.0
  private var leadout = 2.0
  private var beats = 0.0
  var beat = 0.0
  private var showPhantoms = false
  var isRunning = false
  private var lastTime = 0L
  private var iscore = 0.0
  private var prevbeat = -2.0
  var partsstr = ""
  private var currentPart = 0
  var hasParts = false
  var readyListener:()->Unit = { }
  var partListener:(part:Int)->Unit = { }
  private val randomColorArray = arrayOf(
      Color.BLACK,
      Color.BLUE,
      Color.CYAN,
      Color.GRAY,
      Color.GREEN,
      Color.MAGENTA,
      Color.ORANGE,
      Color.RED,
      Color.WHITE,
      Color.YELLOW
  )
  private val dropDown = DropDownMenu()

  init {
    touchDownAction { button,x,y ->
      dropDown.hide()
      val (dx, dy) = mouse2dancer(x, y)
      if (button == 0) {
        doTouch(dx, dy)
      }
      if (button == 2) {
        dancerAt(dx,dy)?.let { d ->
          parentView?.appendView(dropDown)
          dropDown.showAt(x, y)
          dropDown.selectAction { name ->
            if (name == "default") {
              d.fixcolor = false
              setOneColor(d)
            } else {
              d.fillcolor = Color(name)
              d.fixcolor = true
            }
            dropDown.hide()
            invalidate()
          }
        }
      }
    }
    dropDown.addItem("Black") {
      backgroundColor = Color.BLACK
      textColor = Color.WHITE
    }
    dropDown.addItem("Blue") {
      backgroundColor = Color.BLUE
      textColor = Color.WHITE
    }
    dropDown.addItem("Cyan") { backgroundColor = Color.CYAN }
    dropDown.addItem("Gray") { backgroundColor = Color.GRAY }
    dropDown.addItem("Green") { backgroundColor = Color.GREEN }
    dropDown.addItem("Magenta") { backgroundColor = Color.MAGENTA }
    dropDown.addItem("Orange") { backgroundColor = Color.ORANGE }
    dropDown.addItem("Red") { backgroundColor = Color.RED }
    dropDown.addItem("White") { backgroundColor = Color.WHITE }
    dropDown.addItem("Yellow") { backgroundColor = Color.YELLOW }
    dropDown.addItem("default")
  }

  private fun setInteractiveDancerControls() {
    touchDownAction { id,x,y ->
      val (dx,dy) = mouse2dancer(x,y)
      idancer?.touchDown(id,dx,dy) ?: doTouch(dx,dy)
    }
    touchUpAction { id,x,y ->
      val (dx,dy) = mouse2dancer(x,y)
      idancer?.touchUp(id,dx,dy)
    }
    touchMoveAction { id,x,y ->
      val (dx,dy) = mouse2dancer(x,y)
      idancer?.touchMove(id,dx,dy)
    }
    keyDownAction { key ->
      idancer?.keyDown(key)
    }
    keyUpAction { key ->
      idancer?.keyUp(key)
    }
  }

  private fun releaseInteractiveDancerControls() {
    keyDownAction(null)
  }

  /**
   *   Starts the animation
   */
  fun doPlay() {
    lastTime = System.currentTime()
    if (beat > beats)
      beat = -leadin
    isRunning = true
    iscore = 0.0
    if (idancer != null) {
      setInteractiveDancerControls()
      focus()
    }
    invalidate()
  }

  /**
   * Pauses the dancers update & animation.
   */
  fun doPause() { isRunning = false }

  /**
   *  Rewinds to the start of the animation, even if it is running
   */
  fun doRewind() {
    beat = -leadin
    invalidate()
  }

  /**
   *   Moves to the end of the animation
   */
  fun doEnd() {
    beat = beats
    invalidate()
  }

  /**
   *   Moves the animation back a little
   */
  fun doBackup() {
    beat = beat-0.1 max -leadin
    invalidate()
  }

  /**
   *   Moves the animation forward a little
   */
  fun doForward() {
    beat = beat+0.1 min beats
    invalidate()
  }

  /**
   *   Return number of parts
   */
  //  not used fun nParts():Int = if (partsstr.isEmpty()) 1 else partsstr.split(";").count() + 1

  /**
   *   Build an array of floats out of the parts of the animation
   */
  private fun partsValues():List<Double> =
      if (partsstr.isEmpty())
        listOf(-2.0, 0.0)
      else {
        var b = 0.0
        val t = partsstr.split(";")
        (-2 until t.count()).map {
          when (it) {
            -2 -> -leadin
            -1 -> 0.0
            //  FIXME I don't think the follow is ever added
            t.count() -> beats - 2.0
            t.count() + 1 -> beats
            else -> { b += t[it].d; b }
          }
        }
      }

  /**
   *   Moves the animation to the next part
   */
  fun doNextPart() {
    if (beat < beats) {
      beat = (partsValues().asSequence().dropWhile{it <= beat}.firstOrNull() ?: beats) + 0.01
      invalidate()
    }
  }

  /**
   *   Moves the animation to the previous part
   */
  fun doPrevPart() {
    if (beat > -leadin) {
      beat = partsValues().reversed().asSequence().dropWhile{it >= beat}.first()
      invalidate()
    }
  }

  /**
   *   Go to a specific part.
   *   The first part is 1.
   */
  fun goToPart(i:Int) {
    val pv = partsValues()
    beat = pv[i+1 min pv.count()-1] + 0.01  // skip leadin
    invalidate()
  }

  /**
   *   Set the visibility of the grid
   */
  fun setGridVisibility(show:Boolean) {
    showGrid = show
    invalidate()
  }

  /**
   *   Set the visibility of phantom dancers
   */
  private fun setPhantomVisibility(show:Boolean) {
    showPhantoms = show
    dancers.forEach{d -> d.hidden = d.isPhantom && !show}
    invalidate()
  }

  /**
   *  Turn on drawing of dancer paths
   */
  private fun setPathVisibility(show:Boolean) {
    showPaths = show
    invalidate()
  }

  /**
   *   Set animation looping
   */
  private fun setLoop(loopit:Boolean) {
    looping = loopit
    invalidate()
  }

  /**
   *   Set display of dancer numbers
   */
  private fun setNumbers(numberem:Int) {
    val n = if (interactiveDancer >= 0) Dancer.NUMBERS_OFF else numberem
    dancers.forEach { d -> d.showNumber = n }
    invalidate()
  }
  private fun setNumbers(numberstr:String) = when (numberstr) {
    "1-8" -> setNumbers(Dancer.NUMBERS_DANCERS)
    "1-4" -> setNumbers(Dancer.NUMBERS_COUPLES)
    else -> setNumbers(Dancer.NUMBERS_OFF)
  }
  private fun setOneColor(d:Dancer) {
    val usercolor = Setting("Couple ${d.number_couple}").s
    if (usercolor != null)
      d.fillcolor = Color(usercolor)
    else
      d.fillcolor = dancerColor[d.number_couple.i]

  }
  private fun setColors(isOn:Boolean) {
    dancers.forEach { d ->
      d.showColor = isOn
      if (!d.fixcolor)
        setOneColor(d)
    }
    invalidate()
  }
  private fun setShapes(isOn:Boolean) {
    dancers.forEach { d -> d.showShape = isOn }
    invalidate()
  }

  //  Except for the phantoms, these are the standard colors
  //  used for teaching callers
  private val dancerColor get() = if (geometry == Geometry.HEXAGON)
    arrayOf(Color.LIGHTGRAY, Color.RED, Color.GREEN, Color.MAGENTA,
        Color.BLUE, Color.YELLOW, Color.CYAN,
        Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY)
  else
    arrayOf(Color.LIGHTGRAY, Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
        Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY, Color.LIGHTGRAY)


  private fun setRandomColors() {
    randomColorArray.shuffle()
    dancers.forEachIndexed { i, dancer ->
      dancer.showColor = true
      dancer.fillcolor = if (dancer.gender == Gender.PHANTOM)
        Color.LIGHTGRAY
      else
        randomColorArray[i]
    }
  }


  /**
   *   Set speed of animation
   */
  fun setSpeed(myspeed:String) {
    speed = when (myspeed) {
      "Slow" -> SLOWSPEED
      "Moderate" -> MODERATESPEED
      "Fast" -> FASTSPEED
      else -> NORMALSPEED
    }
    invalidate()
  }

  private fun setNewGeometry(g:Int) {
    if (geometry != g) {
      geometry = g
      resetAnimation()
    }
  }

  val totalBeats:Double get() = leadin + beats
  val movingBeats:Double get() = beats - leadout
  val score:Double get() = iscore

  /**
   *   Set time of animation as offset from start including leadin
   */
  fun setTime(b:Double) {
    beat = b - leadin
    invalidate()
  }


  //  Convert x and y to dance floor coords
  private fun mouse2dancer(x:Int,y:Int):Pair<Double,Double> {
    val range = width min height
    val s = range / 13.0
    val dx = -(y - height / 2.0) / s
    val dy = -(x - width / 2.0) / s
    return Pair(dx,dy)
  }

  //  Find dancer at floor coordinates
  private fun dancerAt(dx:Double,dy:Double) : Dancer? {
    return dancers.filter {
      //  Coordinates must be on dancer
      d -> (d.location - Vector(dx,dy)).length < 0.5
    } .minBy {
      //  In case of multiple hits, return the best
      val loc = it.location
      (loc.x - dx) * (loc.x - dx) + (loc.y - dy) * (loc.y - dy)
    }

  }

  //  Touching a dancer shows and hides its path
  private fun doTouch(dx:Double,dy:Double) {
    dancerAt(dx,dy)?.let { d ->
      d.showPath = !d.showPath
      invalidate()
    }
  }

  private fun isInteractiveDancerOnTrack():Boolean {
    //  Get where the dancer should be
    val computetx = idancer!!.computeMatrix(beat)
    //  Get computed and actual location vectors
    val ivu = Matrix(idancer!!.tx).location
    val ivc = computetx.location

    //  Check dancer's facing direction
    val au = Matrix(idancer!!.tx).angle
    val ac = computetx.angle
    return (ivu-ivc).length < 2.0 &&
        angleAngleDiff(au, ac).abs < PI/4 &&
        //  Check relationship with the other dancers
        //  TODO should this be all?
        dancers.filter{it != idancer}.any{d: Dancer ->
          val dv = Matrix(d.tx).location
          //  Compare angle to computed vs actual
          val d2ivu = dv.vectorTo(ivu)
          val d2ivc = dv.vectorTo(ivc)
          val a = d2ivu.angleDiff(d2ivc)
          a.abs < PI/4
        }
  }

  override fun onDraw(ctx: DrawingContext) {
    if (tam != null) {

      //  Update the animation time
      val now = System.currentTime()
      val diff = now - lastTime
      if (isRunning)
        beat += diff/speed
      lastTime = now

      //  Move the dancers
      updateDancers()
      //  Draw the dancers
      doDraw(ctx)

      //  Remember time of this update, and handle loop and end
      prevbeat = beat
      if (beat >= beats) {
        if (looping && isRunning) {
          prevbeat = -leadin
          beat = -leadin
        }
        else if (isRunning) {
          isRunning = false
          if (idancer != null)
            releaseInteractiveDancerControls()
          Application.sendMessage(Request.Action.ANIMATION_DONE)
        }
      }
      Application.sendMessage(Request.Action.ANIMATION_PROGRESS,
          "beat" to (beat+leadin).s)
      //progressListener(beat+leadin)
      //  Continually repeat by telling the system to re-draw
      if (isRunning)
        invalidate()
    }
  }

  private fun doDraw(ctx: DrawingContext) {
    ctx.save()
    //  Draw background
    ctx.fillRect(Rect(0.0,0.0,width.d,height.d),DrawingStyle(color=FLOOR))
    val range = width min height
    val p = DrawingStyle().apply {
      color = Color.BLACK
      textSize = (range / 15.0)
    }
    //  For interactive leadin, show countdown
    if (idancer != null && beat < 0.0) {
      val tminus = beat.floor.i.toString()
      p.textAlign = TextAlign.CENTER
      p.textSize = range / 2.0
      p.color = Color.GRAY
      ctx.fillText(tminus, range / 2.0, range.d, p)
    }
    //  Scale coordinate system to dancer's size
    ctx.translate(width / 2.0, height / 2.0)
    val s = range / 13.0
    //  Flip and rotate
    ctx.scale(s, -s)
    ctx.rotate(PI/2.0)
    //  Draw grid if on
    if (showGrid)
      Geometry(geometry, 0).drawGrid(ctx)
    //  Always show bigon center mark
    if (geometry == Geometry.BIGON) {
      val pline = DrawingStyle().apply {
        color = Color.BLACK
        val m = (height min width) / 13.0
        lineWidth = 1.0/m
      }
      ctx.drawLine(0.0, -0.5, 0.0, 0.5, pline)
      ctx.drawLine(-0.5, 0.0, 0.5, 0.0, pline)
    }
    //  Draw paths if requested
    dancers.forEach { d ->
      if (!d.hidden && (showPaths || d.showPath))
        d.drawPath(ctx)
    }

    //  Draw handholds
    val hline = DrawingStyle().apply {
      color = Color.ORANGE
      lineWidth = 0.05
    }
    dancers.forEach { d ->
      val loc = d.location
      if (d.rightHandVisibility) {
        if (d.rightdancer == null) {  // hexagon center
          ctx.drawLine(loc.x, loc.y, 0.0, 0.0, hline)
          ctx.fillCircle(0.0, 0.0, 0.125, hline)
        } else if (d.rightdancer!! < d) {
          val loc2 = d.rightdancer!!.location
          ctx.drawLine(loc.x, loc.y, loc2.x, loc2.y, hline)
          ctx.fillCircle((loc.x + loc2.x) / 2.0,
              (loc.y + loc2.y) / 2.0, .125, hline)
        }
      }
      if (d.leftHandVisibility) {
        if (d.leftdancer == null) {  // hexagon center
          ctx.drawLine(loc.x, loc.y, 0.0, 0.0, hline)
          ctx.fillCircle(0.0, 0.0, 0.125, hline)
        } else if (d.leftdancer!! < d) {
          val loc2 = d.leftdancer!!.location
          ctx.drawLine(loc.x, loc.y, loc2.x, loc2.y, hline)
          ctx.fillCircle((loc.x + loc2.x) / 2.0,
              (loc.y + loc2.y) / 2.0, .125, hline)
        }
      }
    }

    //  Draw dancers
    dancers.filter { !it.hidden }.forEach { d ->
      ctx.save()
      ctx.transform(d.tx)
      d.draw(ctx)
      ctx.restore()
    }

  }

  //  Check that there isn't another dancer in the middle of
  //  a computed handhold.  Can happen when dancers are in
  //  tight formations like tidal waves.
  private fun dancerInHandhold(hh:Handhold): Boolean {
    val hhloc = (hh.dancer1.location + hh.dancer2.location).scale(0.5,0.5)
    return dancers.any { d ->
      d != hh.dancer1 && d != hh.dancer2 &&
          (d.location - hhloc).length < 0.5
    }
  }

  /**
   * Updates dancers positions based on the passage of realtime.
   * Called at the start of onDraw().
   */
  private fun updateDancers() {
    //  Move dancers
    //  For big jumps, move incrementally -
    //  this helps hexagon and bigon compute the right location
    val delta = beat - prevbeat
    val incs = delta.abs.ceil.i
    for (j in 1 .. incs)
      dancers.forEach{d -> d.animate(prevbeat + j*delta/incs)}

    //  Find the current part, and send a message if it's changed
    val thispart = if (beat < 0 || beat  > beats) 0 else
      partsValues().indexOfLast { it < beat }
    if (thispart != currentPart) {
      currentPart = thispart
      Application.sendMessage(Request.Action.ANIMATION_PART,
          "part" to currentPart.s)
    }

    //  Compute handholds
    val hhlist = mutableListOf<Handhold>()
    dancers.forEach{d0 ->
      d0.rightdancer = null
      d0.leftdancer = null
      d0.rightHandVisibility = false
      d0.leftHandVisibility = false
    }
    for (i1 in 0 until dancers.count()-1) {
      val d1 = dancers[i1]
      if (!d1.isPhantom || showPhantoms) {
        (i1+1 until dancers.count()).forEach { i2 ->
          val d2 = dancers[i2]
          if (!d2.isPhantom || showPhantoms) {
            val hh = Handhold(d1, d2, geometry)
            if (hh != null)
              hhlist.add(0,hh)
          }
        }
      }
    }
    //  Sort the array to put best scores first
    val hhsorted = hhlist.sortedBy { it.score }
    //  Apply the handholds in order from best to worst
    //  so that if a dancer has a choice it gets the best handhold
    hhsorted.filter { !dancerInHandhold(it) }.forEach { hh ->
      //  Check that the hands aren't already used
      val incenter = geometry == Geometry.HEXAGON && hh.inCenter
      if (incenter ||
          (hh.hold1 == Hands.RIGHTHAND && hh.dancer1.rightdancer == null ||
              hh.hold1 == Hands.LEFTHAND && hh.dancer1.leftdancer == null) &&
              (hh.hold2 == Hands.RIGHTHAND && hh.dancer2.rightdancer == null ||
                  hh.hold2 == Hands.LEFTHAND && hh.dancer2.leftdancer == null)) {
        //      	Make the handhold visible
        //  Scale should be 1 if distance is 2
        //  float scale = hh.distance/2f;
        if (hh.hold1 == Hands.RIGHTHAND || hh.hold1 == Hands.GRIPRIGHT) {
          hh.dancer1.rightHandVisibility = true
          hh.dancer1.rightHandNewVisibility = true
        }
        if (hh.hold1 == Hands.LEFTHAND || hh.hold1 == Hands.GRIPLEFT) {
          hh.dancer1.leftHandVisibility = true
          hh.dancer1.leftHandNewVisibility = true
        }
        if (hh.hold2 == Hands.RIGHTHAND || hh.hold2 == Hands.GRIPRIGHT) {
          hh.dancer2.rightHandVisibility = true
          hh.dancer2.rightHandNewVisibility = true
        }
        if (hh.hold2 == Hands.LEFTHAND || hh.hold2 == Hands.GRIPLEFT) {
          hh.dancer2.leftHandVisibility = true
          hh.dancer2.leftHandNewVisibility = true
        }

        if (!incenter) {
          if (hh.hold1 == Hands.RIGHTHAND) {
            hh.dancer1.rightdancer = hh.dancer2
            if ((hh.dancer1.hands and Hands.GRIPRIGHT) == Hands.GRIPRIGHT)
              hh.dancer1.rightgrip = hh.dancer2
          } else {
            hh.dancer1.leftdancer = hh.dancer2
            if ((hh.dancer1.hands and Hands.GRIPLEFT) == Hands.GRIPLEFT)
              hh.dancer1.leftgrip = hh.dancer2
          }
          if (hh.hold2 == Hands.RIGHTHAND) {
            hh.dancer2.rightdancer = hh.dancer1
            if ((hh.dancer2.hands and Hands.GRIPRIGHT) == Hands.GRIPRIGHT)
              hh.dancer2.rightgrip = hh.dancer1
          } else {
            hh.dancer2.leftdancer = hh.dancer1
            if ((hh.dancer2.hands and Hands.GRIPLEFT) == Hands.GRIPLEFT)
              hh.dancer2.leftgrip = hh.dancer1
          }
        }
      }
    }
    //  Clear handholds no longer visible
    dancers.forEach { d ->
      if (d.leftHandVisibility && !d.leftHandNewVisibility)
        d.leftHandVisibility = false
      if (d.rightHandVisibility && !d.rightHandNewVisibility)
        d.rightHandVisibility = false
    }

    //  Update interactive dancer score
    if (idancer != null && beat > 0.0 && beat < beats-leadout) {
      idancer!!.onTrack = isInteractiveDancerOnTrack()
      if (idancer!!.onTrack)
        iscore += (beat - (prevbeat max 0.0)) * 10.0
    }
  }

  /**
   *   This is called to generate or re-generate the dancers and their
   *   animations based on the call, geometry, and other settings.
   * @param xtam     XML element containing the call
   * @param intdan  Dancer controlled by the user, or -1 if not used
   */
  fun setAnimation(xtam:TamElement, intdan:Int = -1, intrand:Boolean = true) {
    TamUtils.tamXref(xtam) { element ->
      tam = element
      interactiveDancer = intdan
      interactiveRandom = intrand
      resetAnimation()
      Application.sendMessage(Request.Action.ANIMATION_LOADED)
    }
  }


  private fun resetAnimation() {
    if (tam != null) {
      leadin = if (interactiveDancer < 0) 2.0 else 3.0
      leadout = if (interactiveDancer < 0) 2.0 else 1.0
      if (isRunning)
        Application.sendMessage(Request.Action.ANIMATION_DONE)
      isRunning = false
      beats = 0.0
      val tlist = tam!!.children("formation")
      val formation = when {
        tlist.count() > 0 -> tlist.first()
        tam!!.hasAttribute("formation") -> TamUtils.getFormation(tam!!.getAttribute("formation")!!)
        else -> tam!!  // formation passed in for sequencer
      }
      val flist = formation.children("dancer")
      dancers = arrayOf()

      //  Get numbers for dancers and couples
      //  This fetches any custom numbers that might be defined in
      //  the animation to match a Callerlab or Ceder Chest illustration
      val paths = tam!!.children("path")
      val numbers = when {
        geometry == Geometry.HEXAGON ->
          arrayOf("A", "E", "I",
              "B", "F", "J",
              "C", "G", "K",
              "D", "H", "L",
              "u", "v", "w", "x", "y", "z")
        geometry == Geometry.BIGON ->
          arrayOf("1", "2", "3", "4", "5", "6", "7", "8")
        paths.count() == 0 ->
          arrayOf("1", "5", "2", "6", "3", "7", "4", "8")
        else ->
          TamUtils.getNumbers(tam!!)
      }
      val couples = when {
        geometry == Geometry.HEXAGON ->
          arrayOf("1", "3", "5", "1", "3", "5",
              "2", "4", "6", "2", "4", "6",
              "7", "8", "7", "8", "7", "8")
        geometry == Geometry.BIGON ->
          arrayOf( "1", "2", "3", "4", "5", "6", "7", "8" )
        paths.count() == 0 ->
          arrayOf("1", "3", "1", "3", "2", "4", "2", "4")
        else ->
          TamUtils.getCouples(tam!!)
      }
      randomColorArray.shuffle()
      val geoms = Geometry.getGeometry(geometry)

      //  Select a random dancer of the correct gender for the interactive dancer
      var icount = -1
      val im = Matrix()
      if (interactiveDancer > 0) {
        val glist = formation.children("dancer").filter { d ->
          d["gender"] == if (interactiveDancer == Gender.BOY) "boy" else "girl"
        }
        //  Select either the first or random dancer to be interactive
        icount = if (interactiveRandom) (random() * glist.count()).i else 0
        //  Find the angle the interactive dancer faces at start
        //  We want to rotate the formation so that direction is up
        val iangle = glist[icount].attr("angle").d
        im.preRotate(-iangle.toRadians)
        //  Adjust icount for looping through geometry below
        icount = icount * geoms.size + 1
      }

      //  Create the dancers and set their starting positions
      var dnum = 0
      for (i in 0 until flist.count()) {
        val fd = flist[i]
        val x = fd.attr("x").d
        val y = fd.attr("y").d
        val angle = fd.attr("angle").d
        val g = when (fd.attr("gender")) {
          "girl" -> Gender.GIRL
          "phantom" -> Gender.PHANTOM
          else -> Gender.BOY
        }
        val movelist = if (paths.count() > i)
          TamUtils.translatePath(paths[i])
        else
          listOf()
        //  Each dancer listed in the formation corresponds to
        //  one, two, or three real dancers depending on the geometry
        geoms.forEach { geom ->
          val m = Matrix().postRotate(angle.toRadians).postTranslate(x, y).postConcatenate(im)
          val nstr = if (g == Gender.PHANTOM) " " else numbers[dnum]
          val cstr = if (g == Gender.PHANTOM) " " else couples[dnum]
          val colorstr = if (g == Gender.PHANTOM) " " else couples[dnum]
          val usercolor = Setting("Couple $colorstr").s
          val color = when {
            g == Gender.PHANTOM -> Color.LIGHTGRAY
            randomColors -> randomColorArray[dnum]
            usercolor != null -> Color(usercolor)
            else -> dancerColor[colorstr.i]
          }
          //  add one dancer
          if (g==interactiveDancer && run {icount -= 1; icount==0}) {
            idancer = InteractiveDancer(nstr, cstr, g, color, m, geom.clone(), movelist)
            dancers += idancer!!
          }
          else  // not interactive dancer
            dancers  += Dancer(nstr, cstr, g, color, m, geom.clone(), movelist)
          if (g == Gender.PHANTOM && !showPhantoms)
            dancers.last().hidden = true
          beats = beats max (dancers.last().beats + leadout)
          dnum += 1
        }
      }  //  All dancers added

      //  Initialize other instance variables
      partsstr = tam!!.attr("parts") + tam!!.attr("fractions")
      hasParts = tam!!.attr("parts").isNotEmpty()
      isRunning = false
      beat = -leadin
      prevbeat = -leadin
      invalidate()
      readyListener()
    }
  }

  fun recalculate()  {
    beats = 0.0
    dancers.forEach {
      d -> beats = beats max (d.beats + leadout)
    }
  }

  fun readAnimationSettings() {
    setNewGeometry(Geometry(Setting("Special Geometry").s ?: "None").geometry)
    setGridVisibility(Setting("Grid").b == true)
    setLoop(Setting("Loop").b == true)
    setPathVisibility(Setting("Paths").b == true)
    setSpeed(Setting("Dancer Speed").s ?: "Normal")
    setNumbers(Setting("Numbers").s ?: "None")
    setPhantomVisibility(Setting("Phantoms").b == true)
    setColors(true)
    setShapes(true)
    invalidate()
  }

  fun readSequencerSettings() {
    //  TODO setNewGeometry(Geometry(Setting("Special Geometry").s ?: "None").geometry)
    setNewGeometry(Geometry("None").geometry)
    setGridVisibility(Setting("Grid").b == true)
    if (Setting("embed").b == true)
      setLoop(Setting("Loop").b == true)
    else
      setLoop(false)
    setPathVisibility(false)
    setSpeed(Setting("Dancer Speed").s ?: "Normal")
    setNumbers(when(Setting("Dancer Identification").s) {
      "None" -> Dancer.NUMBERS_OFF
      "Dancer Numbers" -> Dancer.NUMBERS_DANCERS
      "Couple Numbers" -> Dancer.NUMBERS_COUPLES
      "Names" -> Dancer.NUMBER_NAMES
      else -> Dancer.NUMBERS_OFF
    })
    setPhantomVisibility(false)
    when (Setting("Dancer Colors").s) {
      "None" -> setColors(false)
      "Random" -> setRandomColors()
      else -> setColors(true)
    }
    setShapes(Setting("Dancer Shapes").b != false)
    invalidate()
  }

}