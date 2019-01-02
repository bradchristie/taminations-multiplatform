package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import com.bradchristie.taminations.common.Request

expect abstract class Page() {

  companion object {
    //  Animate a transition between two pages
    fun animate(currentPage: Page?, nextPage: Page, code: () -> Unit)
  }

  //  Every page has a base view that it needs to build on init
  abstract val view: View

  //  If it cannot handle the given request, it returns false
  //  Default is to handle the single action registered with onAction
  //  A page can override this method for more complex cases
  open fun doRequest(request: Request): Boolean

  fun doRequest(action: Request.Action, vararg pairs:Pair<String,String>)
  fun doRequest(action: Request.Action, from: Request)

  //  Almost always a simple page handles just one request action
  //  So these are convenience methods for that case
  protected fun onAction(action: Request.Action, code: (Request) -> Unit)

  open fun canDoAction(action: Request.Action) : Boolean

  //  Other requests are sent as "messages"
  //  A page can register to process a message with the onMessage function
  protected fun onMessage(message: Request.Action, code: (Request) -> Unit)

  //  Anybody can send a message to a page or the application
  open fun sendMessage(message: Request)
  fun sendMessage(message: Request.Action, vararg params:Pair<String,String>)

}
