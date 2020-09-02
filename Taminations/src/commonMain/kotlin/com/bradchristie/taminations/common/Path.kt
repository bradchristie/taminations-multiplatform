package com.bradchristie.taminations.common
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

class Path(moves:List<Movement> = listOf()) {

  var movelist = moves.map { it }
  private var transformlist = listOf<Matrix>()

  fun recalculate() {
    var tx = Matrix()
    transformlist = movelist.map {
      tx = tx.preConcatenate(it.translate())
      tx = tx.preConcatenate(it.rotate())
      Matrix(tx)
    }
  }

  init {
    recalculate()
  }

  constructor(m: Movement) : this(listOf(m))
  constructor(p: Path) : this(p.movelist)

  fun copy(): Path = Path(movelist)

  fun clear() {
    movelist = listOf()
    transformlist = listOf()
  }

  fun add(p: Path): Path {
    movelist = movelist + p.movelist
    recalculate()
    return this
  }
  operator fun plus(p: Path) = add(p)
  fun add(m: Movement): Path {
    movelist = movelist + m
    recalculate()
    return this
  }

  fun pop(): Movement {
    val m = movelist.last()
    movelist = movelist.dropLast(1)
    recalculate()
    return m
  }

  fun shift() : Movement? {
    val m = movelist.firstOrNull()
    movelist = movelist.drop(1)
    recalculate()
    return m
  }

  fun reflect(): Path {
    movelist = movelist.map { it.reflect() }
    recalculate()
    return this
  }

  val beats:Double get() = movelist.map { it.beats }.sum()

  fun changebeats(newbeats:Double): Path {
    val factor = newbeats / beats
    movelist = movelist.map { it.time(it.beats*factor) }
    //  no need to recalculate, transformlist doesn't depend on beats
    return this
  }

  fun changehands(hands:Int): Path {
    movelist = movelist.map { it.useHands(hands) }
    return this
  }

  fun addhands(hands:Int): Path {
    movelist = movelist.map { it.useHands(it.hands or hands) }
    return this
  }

  fun scale(x:Double, y:Double): Path {
    movelist = movelist.map { it.scale(x,y) }
    recalculate()
    return this
  }

  //  This likely will not work well for paths with >1 movement
  //  Instead use skewFirst or skewFromEnd
  fun skew(x:Double, y:Double): Path {
    if (movelist.isNotEmpty()) {
      //  Apply the skew to just the last movement
      movelist = movelist.dropLast(1) + movelist.last().skew(x,y)
      recalculate()
    }
    return this
  }

  //  Shift path based on adjustment to final position
  //  This should work well with any number of movements in the path
  fun skewFromEnd(x:Double, y:Double): Path {
    if (movelist.isNotEmpty()) {
      movelist = movelist.dropLast(1) + movelist.last().skewFromEnd(x,y)
      recalculate()
    }
    return this
  }

  fun skewFirst(x:Double, y:Double): Path {
    if (movelist.isNotEmpty()) {
      movelist = listOf(movelist.first().skew(x,y)) + movelist.drop(1)
      recalculate()
    }
    return this
  }

  fun notFromCall() : Path {
    movelist = movelist.map { m -> m.notFromCall() }
    return this
  }

  /**
   * Return a transform for a specific point of time
   */
  fun animate(b:Double): Matrix {
    var bv = b
    var tx = Matrix()
    //  Apply all completed movements
    var m: Movement? = null
    for (i in movelist.indices) {
      m = movelist[i]
      if (bv >= m.beats) {
        tx = transformlist[i]
        bv -= m.beats
        m = null
      } else {
        break
      }
    }
    //  Apply movement in progress
    if (m != null) {
      tx *= m.translate(bv) * m.rotate(bv)
    }
    return tx
  }

  /**
   * Return the current hand at a specific point in time
   */
  fun hands(b:Double):Int {
    return if (b < 0 || b > beats)
      Hands.BOTHHANDS
    else {
      var bv = b
      movelist.fold(Hands.BOTHHANDS) {
        h, m -> if (bv < 0) h else { bv -= m.beats; m.hands }
      }
    }
  }

}
