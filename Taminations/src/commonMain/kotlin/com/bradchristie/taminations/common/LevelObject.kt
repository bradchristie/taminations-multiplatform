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

import com.bradchristie.taminations.platform.TamDocument

object LevelObject {

  data class LevelData(val name:String, val dir:String, val selector:String, val doc: TamDocument, val color: Color) {
    operator fun compareTo(other: LevelData)
        = data.indexOf(this) - data.indexOf(other)
  }

  private val data = listOf(
      LevelData("Basic and Mainstream", "bms", "/calls/call[@level='Basic and Mainstream' and @sublevel!='Styling']", TamUtils.calldoc, Color.BMS),
      LevelData("Basic 1", "b1", "/calls/call[@sublevel='Basic 1']", TamUtils.calldoc, Color.B1),
      LevelData("Basic 2", "b2", "/calls/call[@sublevel='Basic 2']", TamUtils.calldoc, Color.B2),
      LevelData("Mainstream", "ms", "/calls/call[@sublevel='Mainstream']", TamUtils.calldoc, Color.MS),
      LevelData("Plus", "plus", "/calls/call[@level=\"Plus\"]", TamUtils.calldoc, Color.PLUS),
      LevelData("Advanced", "adv", "/calls/call[@level='Advanced']", TamUtils.calldoc, Color.ADV),
      LevelData("A-1", "a1", "/calls/call[@sublevel='A-1']", TamUtils.calldoc, Color.A1),
      LevelData("A-2", "a2", "/calls/call[@sublevel='A-2']", TamUtils.calldoc, Color.A2),
      LevelData("Challenge", "cha", "/calls/call[@level='Challenge']", TamUtils.calldoc, Color.CHALLENGE),
      LevelData("C-1", "c1", "/calls/call[@sublevel='C-1']", TamUtils.calldoc, Color.C1),
      LevelData("C-2", "c2", "/calls/call[@sublevel='C-2']", TamUtils.calldoc, Color.C2),
      LevelData("C-3A", "c3a", "/calls/call[@sublevel='C-3A']", TamUtils.calldoc, Color.C3A),
      LevelData("C-3B", "c3b", "/calls/call[@sublevel='C-3B']", TamUtils.calldoc, Color.C3B),
      LevelData("All Calls", "all", "/calls/call", TamUtils.indexdoc, Color.LIGHTGRAY),
      LevelData("Index of All Calls", "all", "/calls/call", TamUtils.indexdoc, Color.LIGHTGRAY)
  )

  fun find(s:String): LevelData {
    return data.find { it.name.equals(s,ignoreCase = true) ||
        it.dir.equals(s,ignoreCase = true) ||
        it.selector.equals(s,ignoreCase = true) }!!
  }

  operator fun invoke(s:String): LevelData = find(s)

}