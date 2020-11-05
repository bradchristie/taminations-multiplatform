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

object LevelObject {

  data class LevelData(val name:String, val dir:String, val selector:String, val color: Color) {
    operator fun compareTo(other: LevelData)
        = data.indexOf(this) - data.indexOf(other)
  }

  private val data = listOf(
      LevelData("Basic and Mainstream", "bms",
          "/calls/call[starts-with(@link,'b') or starts-with(@link,'ms')]", Color.BMS),
      LevelData("Basic 1", "b1", "/calls/call[starts-with(@link,'b1')]", Color.B1),
      LevelData("Basic 2", "b2", "/calls/call[starts-with(@link,'b2')]", Color.B2),
      LevelData("Mainstream", "ms", "/calls/call[starts-with(@link,'ms')]", Color.MS),
      LevelData("Plus", "plus", "/calls/call[starts-with(@link,'plus')]", Color.PLUS),
      LevelData("Advanced", "adv",
          "/calls/call[starts-with(@link,'a')]",
          Color.ADV),
      LevelData("A-1", "a1", "/calls/call[starts-with(@link,'a1')]", Color.A1),
      LevelData("A-2", "a2", "/calls/call[starts-with(@link,'a2')]", Color.A2),
      LevelData("Challenge", "cha", "/calls/call[starts-with(@link,'c')]", Color.CHALLENGE),
      LevelData("C-1", "c1", "/calls/call[starts-with(@link,'c1')]", Color.C1),
      LevelData("C-2", "c2", "/calls/call[starts-with(@link,'c2')]", Color.C2),
      LevelData("C-3A", "c3a", "/calls/call[starts-with(@link,'c3a')]", Color.C3A),
      LevelData("C-3B", "c3b", "/calls/call[starts-with(@link,'c3b')]", Color.C3B),
      LevelData("All Calls", "all", "/calls/call", Color.LIGHTGRAY),
      LevelData("Index of All Calls", "all", "/calls/call", Color.LIGHTGRAY)
  )

  fun find(s:String): LevelData {
    return data.find { it.name.equals(s,ignoreCase = true) ||
        //  following lets us easily find the level of a link
        s.startsWith(it.dir) ||
        it.selector.equals(s,ignoreCase = true) }!!
  }

  operator fun invoke(s:String): LevelData = find(s)

}