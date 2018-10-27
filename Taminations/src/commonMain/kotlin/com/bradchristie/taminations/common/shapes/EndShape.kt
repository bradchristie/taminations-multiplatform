package com.bradchristie.taminations.common.shapes

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

class EndShape : Shape() {

  override fun draw(ctx: DrawingContext) {
    val h = height / 2.0
    ctx.translate(width / 2.0, height / 2.0)
    val path = DrawingPath()
    path.moveTo(-h * 0.6, -h / 3.0)
    path.lineTo(0.0, 0.0)
    path.lineTo(-h * 0.6, h / 3.0)
    path.moveTo(0.0, -h / 3.0)
    path.lineTo(h * 0.6, 0.0)
    path.lineTo(0.0, h / 3.0)
    ctx.drawPath(path,DrawingStyle(lineWidth = 2.0))
  }

}