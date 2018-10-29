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

import com.bradchristie.taminations.platform.System
import kotlin.math.*

//  Extension of function "all" to "allIndexed" like mapIndexed
fun <T> Iterable <T>.allIndexed(test:(Int,T) -> Boolean):Boolean {
  var i = 0
  return all { test(i++,it) }
}

var seed = System.currentTime()
fun random():Double {
  seed = (seed * 25214903917 + 11) and ((1L shl 48)-1)
  return (seed shr 15).toDouble() / (1L shl 33).toDouble()
}

fun getRandomInt(max:Int):Int {
  return (random() * max).floor.i
}//  Shuffle methods
//  Shuffle an array in place
fun <T> Array<T>.shuffle() {
  for (i in count()-1 downTo 1) {
    val j = getRandomInt(i + 1)
    val t = this[j]
    this[j] = this[i]
    this[i] = t
  }
}
//  Map a list to a shuffled list
fun <T> Iterable <T>.shuffle():Iterable<T> {
  val a = Array(count()) { i -> i }
  a.shuffle()
  return mapIndexed { i, _ -> elementAt(a[i]) }
}

//  Apply code if a condition is true
fun <T> T.ifdo(e:Boolean, block: (T) -> T): T = if (e) block(this) else this

//  Returns an array of strings, starting with the entire string,
//  and each subsequent string chopping one word off the end
fun String.chopped():List<String> {
  val ss = mutableListOf<String>()
  return this.split(Regex("\\s+")).map {
    ss.add(it)
    ss.reduce { a,b -> "$a $b" }
  }.reversed()
}

//  Return an array of strings, each removing one word from the start
fun String.diced():List<String> {
  val ss = mutableListOf<String>()
  return this.split(Regex("\\s+")).reversed().map {
    ss.add(0,it)
    ss.reduce { a,b -> "$a $b" }
  }.reversed()
}

//  Return all combinations of words from a string
fun String.minced():List<String> = this.chopped().flatMap { it.diced() }

//  Return string with (almost) every word capitalized
fun String.capWords():String =
    this.split(Regex("\\s+")).joinToString(" ") {
      it.toLowerCase().capitalize()
    }
        .replace(Regex("\\bAnd\\b"),"and")
        .replace(Regex("\\bA\\b"),"a")
        .replace(Regex("\\bAn\\b"),"an")
        .replace(Regex("\\bAt\\b"),"at")
        .replace(Regex("\\bTo\\b"),"to")
        .replace(Regex("\\bThe\\b"),"the")

//  Quote a string and escape any internal quotes
fun String.quote():String = "\"${replace("\"","&quot;")}\""


//  Math extensions
//  One-letter conversions
val Float.i:Int get() = this.toInt()
val Float.d:Double get() = this.toDouble()
val Double.i:Int get() = this.toInt()
val Double.f:Float get() = this.toFloat()
val Double.s:String get() = this.toString()
val Int.f:Float get() = this.toFloat()
val Int.d:Double get() = this.toDouble()
val Int.s:String get() = this.toString()
val Long.i:Int get() = this.toInt()
val Long.d:Double get() = this.toDouble()
val Long.s:String get() = this.toString()
val String.i:Int get() = this.toInt()
val String.f:Float get() = this.toFloat()
val String.d:Double get() = this.toDouble()
val String.r:Regex get() = this.toRegex()
val String.ri:Regex get() = this.toRegex(kotlin.text.RegexOption.IGNORE_CASE)
val String.w get() = this.replace(Regex("\\W"),"")
val String.lc get() = this.toLowerCase()

//  Other functions
val Double.sign:Int get() = if (this < 0.0) -1 else if (this > 0.0) 1 else 0
val Double.floor:Double get() = floor(this)
val Double.ceil:Double get() = ceil(this)
val Double.round:Double get() = round(this)
val Double.abs:Double get() = abs(this)
val Float.abs:Float get() = abs(this)
val Double.sqrt:Double get() = sqrt(this)
val Double.sq:Double get() = this * this
val Double.sin:Double get() = sin(this)
val Double.cos:Double get() = cos(this)
val Double.toDegrees:Double get() = this * 180 / PI
val Double.toRadians:Double get() = this * PI / 180
fun Double.isApprox(y:Double,delta:Double=0.1):Boolean = (this-y).abs < delta
fun Double.isApproxInt(delta:Double=0.1):Boolean = (this-this.round).abs < delta
fun Double.angleDiff(a2:Double):Double =
    ((((this-a2) % (PI*2)) + (PI*3)) % (PI*2)) - PI
fun Double.angleEquals(a2:Double) = this.angleDiff(a2).isApprox(0.0)
//  These can be used as functions or operators: a.min(b) or a min b
infix fun Int.min(i:Int) = min(this,i)
infix fun Int.max(i:Int) = max(this,i)
infix fun Float.max(x:Float) = max(this,x)
val Int.abs:Int get() = abs(this)
// not used infix fun Float.min(x:Float) = min(this,x)
infix fun Double.max(x:Double) = max(this,x)
infix fun Double.min(x:Double) = min(this,x)

fun Double.IEEErem(p:Double):Double {
  val r = (this % p).abs
  return if (r.isNaN() || r==p || r <= p.abs/2.0)
    r
  else
    this.sign * (r - p)
}



