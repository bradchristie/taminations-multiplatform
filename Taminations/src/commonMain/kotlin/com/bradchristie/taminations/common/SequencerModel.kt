package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.Application
import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.common.shapes.PauseShape
import com.bradchristie.taminations.common.shapes.PlayShape
import com.bradchristie.taminations.platform.*
import com.bradchristie.taminations.platform.System.later

class SequencerInstructionPage : Page() {

  override val view = DefinitionView()
  private val model = DefinitionModel(view)

  init {
    onAction(Request.Action.SEQUENCERHELP) {
      model.setDefinition("info/sequencer","")
    }
  }

}

class SequencerSettingsPage : Page() {

  override val view = SettingsView()
  init {
    onAction(Request.Action.SEQUENCERSETTINGS) {
      view.showSequencerSettings()
    }
  }
}

class SequencerPage : Page() {

  private val callPage = SequencerCallsPage()
  private val instrPage = SequencerInstructionPage()
  private val settingsPage = SequencerSettingsPage()
  private val abbrPage = AbbreviationsPage()
  private val refpage = SequencerReferencePage()
  private val rightPage : NavigationPage = object : NavigationPage() {
    override val pages = listOf(instrPage,settingsPage,abbrPage,refpage)
  }
  private val seqView = SequencerLayout()
  override val view = when {
    Setting("embed").b == true -> seqView.apply {
      editButtons.hide()
      pageButtons.hide()
    }
    Application.isLandscape -> LinearLayout(LinearLayout.Direction.HORIZONTAL).apply {
      appendView(callPage.view) { weight = 1 }
      appendView(seqView) { weight = 1 }
      appendView(rightPage.view) { weight = 1 }
    }
    else -> LinearLayout(LinearLayout.Direction.VERTICAL).apply {
      appendView(seqView) { weight = 2 }
      appendView(callPage.view) { weight = 1 }
    }
  }
  private lateinit var model: SequencerModel
  private lateinit var panelModel: AnimationPanelModel

  init {
    rightPage.doRequest(Request.Action.SEQUENCERHELP)
    onAction(Request.Action.SEQUENCER) {
      Application.titleBar.title = "Taminations Sequencer"
      if (!::model.isInitialized) {
        model = SequencerModel(seqView, callPage)
        panelModel = AnimationPanelModel(seqView.panelLayout, seqView.animationView)
      }
      if (it["calls"].isNotBlank()) {
        model.loadCalls(it["calls"].split(";"), it["formation"])
        if (Setting("embed").b == true &&
            Setting("play").b == true) {
          seqView.panelLayout.playButton.setImage(PauseShape())
          seqView.animationView.doPlay()
        }
      }
      later {
        callPage.textInput.focus()
      }
    }
    onMessage(Request.Action.BUTTON_PRESS) { request ->
      when (request["button"]) {
        "Help" -> doRequest(Request.Action.SEQUENCERHELP)
        "Settings" -> doRequest(Request.Action.SEQUENCERSETTINGS)
        "Abbrev" -> doRequest(Request.Action.ABBREVIATIONS)
        "Calls" -> doRequest(Request.Action.SEQUENCERCALLS)
        "Undo" -> model.undoLastCall()
        "Reset" -> model.reset()
        "Copy" -> model.copyCallsToClipboard()
        "Paste" -> model.pasteCallsFromClipboard()
      }
      later {
        //  For paste, keep focus in paste box
        //  TODO probably specific only to DOM
        if (request["button"] != "Paste")
          callPage.textInput.focus()
      }
    }
    onMessage(Request.Action.ANIMATION_PROGRESS) { message ->
      val beat = message["beat"].d
      seqView.panelLayout.beatSlider.setValue(beat*100.0/seqView.animationView.totalBeats)
    }
    onMessage(Request.Action.ANIMATION_PART) { message ->
      val partnum = message["part"].i-1
      callPage.highlightCall(partnum)
      seqView.callText.text = if (partnum >= 0 && partnum < model.callNames.count())
        model.callNames[partnum]
      else
        ""
    }
    onMessage(Request.Action.ANIMATION_DONE) {
      seqView.panelLayout.playButton.setImage(PlayShape())
    }

    onMessage(Request.Action.SETTINGS_CHANGED) {
      seqView.animationView.readSequencerSettings()
      model.checkStartingFormation()
    }
    onMessage(Request.Action.SEQUENCER_CURRENTCALL) { message ->
      seqView.animationView.goToPart(message["item"].i)
    }
    onMessage(Request.Action.TRANSITION_COMPLETE) {
      callPage.textInput.focus()
    }
    onMessage(Request.Action.CALLITEM) { request ->
      model.loadOneCall(request["title"])
    }
    onMessage(Request.Action.SEQUENCER_LISTEN) {
      model.listen(callPage.listening)
    }
  }

  private fun doRequest(action: Request.Action) {
    if (Application.isPortrait)
      Application.sendRequest(action)
    else
      rightPage.doRequest(action)
  }

  override fun sendMessage(message: Request) {
    super.sendMessage(message)
    abbrPage.sendMessage(message)
  }

}


class SequencerModel(private val seqView: SequencerLayout,
                     private val callsView: SequencerCallsPage) {

  private var formation = Setting("Starting Formation").s ?: "Static Square"
  private val callBeats = mutableListOf<Double>()
  val callNames = mutableListOf<String>()
  private val boyNames = listOf("Adam","Brad","Carl","David",
      "Eric","Frank",
      "Gary","Hank",
      "John","Kevin","Larry",
      "Mark","Paul","Ray","Scott","Tim","Wally")
  private val girlNames = listOf("Alice","Barb","Carol","Donna",
      "Helen", "Karen","Irene","Janet","Linda","Mary","Nancy",
      "Pam","Ruth","Susan","Tina","Wanda")

  private val callListener = CallListener(
      { call -> loadOneCall(call) },
      { error -> showError(error) })

  init {
    startSequence()
    callsView.textInput.returnAction {
      val call = callsView.textInput.text
      callsView.clearError()
      when (call.toLowerCase().trim()) {
        "undo" -> undoLastCall()
        "reset" -> reset()
        "copy" -> copyCallsToClipboard()
        "paste" -> pasteCallsFromClipboard()
        else -> loadOneCall(call)
      }
      callsView.textInput.text = ""
    }

    seqView.animationView.partListener = { part ->
      callsView.highlightCall(part-1)
    }

  }

  fun listen(on:Boolean) {
    if (on) {
      callListener.initializeSpeechRecognizer()
    } else {
      callListener.pause()
    }
  }

  fun reset() {
    seqView.animationView.doPause()
    callNames.clear()
    callBeats.clear()
    callsView.clear()
    startSequence()
  }

  private fun showError(error:String) {
    callsView.errorText.text = error
    callsView.errorText.show()
    Application.sendMessage(Request.Action.SEQUENCER_ERROR,
        "error" to error)
  }

  fun checkStartingFormation(f:String? = Setting("Starting Formation").s) {
    //  Reset if we have a new formation
    when (f) {
      null, formation -> { }
      else -> {
        formation = f
        reset()
      }
    }
  }

  private fun startSequence() {
    seqView.animationView.setAnimation(TamUtils.getFormation(formation))
    setDancerNames()
    seqView.animationView.readSequencerSettings()
    updateParts()
  }

  private fun setDancerNames() {
    val boys = boyNames.toMutableList()
    val girls = girlNames.toMutableList()
    seqView.animationView.dancers.forEach {
      it.name = if (it.gender == Gender.BOY)
        boys.removeAt(getRandomInt(boys.count()))
      else
        girls.removeAt(getRandomInt(girls.count()))
    }
  }

  private fun insertCall(call:String) {
    if (interpretOneCall(call)) {
      updateParts()
      seqView.animationView.goToPart(callNames.lastIndex)
      seqView.animationView.doPlay()
      seqView.panelLayout.playButton.setImage(PauseShape())
    } else
      callNames.removeAt(callNames.lastIndex)
  }

  private fun interpretOneCall(calltext:String):Boolean {
    //  Add call as entered, in case parsing fails
    val line = callNames.count()
    callNames.add(calltext)
    val avdancers = seqView.animationView.dancers
    val cctx = CallContext(avdancers)
    try {
      val prevbeats = seqView.animationView.movingBeats
      cctx.interpretCall(calltext)
      cctx.performCall()
      cctx.matchStandardFormation()
      for (i in avdancers.indices)
        avdancers[i].path.add(cctx.dancers[i].path)
      seqView.animationView.recalculate()
      val newbeats = seqView.animationView.movingBeats
      if (newbeats > prevbeats) {
        //  Call worked, add it to the list
        callsView.addCall(calltext.capWords(),cctx.level)
        callNames[line] = cctx.callname
        callBeats.add(newbeats - prevbeats)
        callsView.highlightCall(line)
      }
    } catch (err: CallError) {
      showError(err.message ?: "Error in call")
      return false
    }
    return true
  }

  //  Update parts and tics on animation panel
  private fun updateParts() {
    if (callBeats.count() > 1) {
      val partstr = callBeats.dropLast(1).map(Any::toString).reduce { s1, s2 -> "$s1;$s2" }
      seqView.animationView.partsstr = partstr
    } else
      seqView.animationView.partsstr = ""
    seqView.panelLayout.ticView.setTics(seqView.animationView.totalBeats,
        seqView.animationView.partsstr, isCalls = true)
    seqView.beatText.text = seqView.animationView.movingBeats.i.s
    if (callNames.isNotEmpty())
      Application.updateLocation(Request.Action.SEQUENCER,
          "formation" to formation,
          "calls" to callNames.joinToString(";"))
    else
      Application.updateLocation(Request.Action.SEQUENCER,
      "formation" to formation, "calls" to "delete")
  }

  fun undoLastCall() {
    if (callNames.isNotEmpty()) {
      val lastIndex = callNames.count() - 1
      callNames.removeAt(lastIndex)
      callBeats.removeAt(lastIndex)
      val totalBeats = callBeats.sum()
      seqView.animationView.dancers.forEach { d ->
        while (d.path.beats > totalBeats)
          d.path.pop()
      }
      seqView.animationView.recalculate()
      seqView.animationView.doEnd()
      callsView.removeLastCall()
      updateParts()
    }
  }

  fun copyCallsToClipboard() {
    System.copyTextToClipboard(callNames)
    Alert("Sequencer").apply {
      textView("${callNames.count()} Calls copied to clipboard")
      okAction { }
    }
  }

  //  Replace any abbreviations
  private fun String.getAbbrevs() = split(Regex("\\s+"))
      .joinToString(" ") {
    it -> Storage["abbrev "+it.toLowerCase()] ?: it
  }

  fun loadOneCall(call:String) {
    callsView.clearError()
    CallContext.loadCalls(listOf(call.getAbbrevs())) {
      insertCall(call.getAbbrevs())
    }
  }

  //  Build sequence from calls either pasted in or in the URL
  fun loadCalls(calls:List<String>, f:String = formation) {
    if (f.isNotBlank())
      formation = f
    reset()
    CallContext.loadCalls(calls.map { it.getAbbrevs() }) {
      calls.all { call ->
        interpretOneCall(call.getAbbrevs())
      }
      updateParts()
      seqView.animationView.goToPart(-1)
      Application.sendMessage(Request.Action.SEQUENCER_READY)
    }
  }

  fun pasteCallsFromClipboard() {
    System.pasteTextFromClipboard { text ->
      loadCalls(text.split("\n"))
    }
  }

}