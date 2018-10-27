package com.bradchristie.taminations.platform
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

import com.bradchristie.taminations.common.d

fun Animation(duration:Long,
              changer:(time:Long,fraction:Double)->Unit) : Promise<String> {
  val helper = AnimationHelper(duration, changer)
  return Promise(helper.executor)
}

//  TODO maybe using FutureTask ?
class Promise<out T>(executor: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit) {

}


class AnimationHelper(private val duration:Long,
                      private val changer:(time: Long, fraction: Double)->Unit) {

  private var startTime = 0L
  private lateinit var resolver:(String)->Unit
  private lateinit var rejecter:(Throwable)->Unit  // never used
  val executor:(resolve:(String)->Unit,reject:(Throwable)->Unit)->Unit =
      {  resolve,reject ->
        resolver = resolve
        rejecter = reject
      }

  init {
    System.later { step(System.currentTime()) }
  }

  fun step(timestamp:Long) {
    if (startTime == 0L) {
      startTime = timestamp
    }
    changer(timestamp-startTime,(timestamp-startTime).d/duration.d)
    if (timestamp - startTime < duration)
      System.later { step(System.currentTime()) }
    else {
      resolver("We are done!")
    }
  }



}