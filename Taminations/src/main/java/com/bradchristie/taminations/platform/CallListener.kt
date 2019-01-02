@file:Suppress("DEPRECATION")
package com.bradchristie.taminations.platform
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
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.bradchristie.taminations.Taminations
import edu.cmu.pocketsphinx.Assets
import edu.cmu.pocketsphinx.Hypothesis
import edu.cmu.pocketsphinx.RecognitionListener
import edu.cmu.pocketsphinx.SpeechRecognizer
import edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup
import java.io.File
import java.lang.Exception

actual class CallListener actual constructor(private val callHandler:(String)->Unit,                                             private val errorHandler:(String)->Unit) : RecognitionListener
{
  //  Make sure we only create one speech recognizer
  actual companion object {
    actual val available = true
    private var sr: SpeechRecognizer? = null
    private val assets = Assets(Taminations.context)
    private val assetsDir: File = assets.syncAssets()
    private const val CALLSEARCH = "call"
  }

  @SuppressLint("ValidFragment")
  class ExternalStoragePermissionDialog(private val onReturn:()->Unit) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
      val builder = AlertDialog.Builder(activity)
      builder.setTitle("Requesting Access to External Storage")
          .setMessage("Taminations needs to save your audio to a temporary file so the speech recognizer can process it.")
          .setPositiveButton("I understand") { _, _ -> onReturn() }
      return builder.create()
    }
  }

  //  Need to check permissions before user can record audio
  private fun checkPermissions(onSuccess:()->Unit) {
    Taminations.onPermissionsSuccess = onSuccess
    if (ContextCompat.checkSelfPermission(Taminations.context, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {        // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(Taminations.context, WRITE_EXTERNAL_STORAGE)) {
        //  Explain why we need to access external storage
        ExternalStoragePermissionDialog {
          //  And now request it
          ActivityCompat.requestPermissions(Taminations.context, arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE), 0)
        }
      } else  //  Can't explain but ask for it anyway
        ActivityCompat.requestPermissions(Taminations.context, arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE), 0)
    } else if (ContextCompat.checkSelfPermission(Taminations.context, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
      //  Because user tapped the mike, no need to explain why
      ActivityCompat.requestPermissions(Taminations.context, arrayOf(RECORD_AUDIO, WRITE_EXTERNAL_STORAGE), 0)
    } else
      //  Already have required permissions
      onSuccess()
  }

  actual fun initializeSpeechRecognizer():Boolean {
    if (sr != null)
      listen()
    else try {
      checkPermissions {
        sr = defaultSetup()
            .setAcousticModel(File(assetsDir, "en-us-ptm"))
            .setDictionary(File(assetsDir, "1958.dic"))
            // Threshold to tune for keyphrase to balance between false alarms and misses
            .setKeywordThreshold(1e-45f)
            // Use context-independent phonetic search,
            // context-dependent is too slow for mobile
            .setBoolean("-allphone_ci", true)
            .recognizer
        // Create language model search
        val languageModel = File(assetsDir, "1958.lm")
        sr!!.addNgramSearch(CALLSEARCH, languageModel)
        sr!!.addListener(this)
        listen()
      }
    } catch (ex:Exception) {
      errorHandler(ex.message?:"Error initializing speech recognizer")
      return false
    }
    return true
  }

  private fun listen() {
    sr?.startListening(CALLSEARCH)
  }

  override fun onResult(hypothesis: Hypothesis?)  {
    if (hypothesis != null) {
      //  Get the call and pass it to the sequencer
      val calltext = hypothesis.hypstr
      if (calltext.isNotBlank())
        callHandler(calltext)
      sr?.startListening(CALLSEARCH)
    }
  }

  actual fun pause() { sr?.cancel() }
  //  not used  fun destroy() = sr?.shutdown()

  override fun onEndOfSpeech() {
    sr?.stop()
  }

  override fun onPartialResult(p0: Hypothesis?) { }
  override fun onTimeout() { }
  override fun onBeginningOfSpeech() { }
  override fun onError(p0: Exception?) { }

}