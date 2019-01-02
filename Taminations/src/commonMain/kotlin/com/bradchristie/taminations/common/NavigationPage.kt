package com.bradchristie.taminations.common
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

import com.bradchristie.taminations.platform.Page
import com.bradchristie.taminations.platform.StackLayout

//  NavigationPage has a view that holds other pages
//  This class defines the necessary content and handles
//  switching the view based on an Request
abstract class NavigationPage : Page() {

  abstract val pages:List<Page>
  private var currentPage: Page? = null
  //  The view holds just one child view at a time, which
  //  is the view of the selected page
  override val view = StackLayout()

  //  Finds the first page that can handle an Request
  //  and loads it into a Content
  override fun doRequest(request: Request): Boolean {
    //  First see if the currently displayed page can handle the Request
    val nextPage = if (currentPage?.canDoAction(request.action) == true)
      currentPage
    else
    //  Nope, check all other pages to see who wants to handle it
      pages.firstOrNull { it.canDoAction(request.action) }
    if (nextPage != null) {
      nextPage.doRequest(request)
      //  Default is to only animate page changes
      //  If the page wants to animate itself it can do it
      //  when processing the request
      if (currentPage != nextPage) {
        animate(currentPage, nextPage) {
          view.clear()
          view.appendView(nextPage.view) {
            fillParent()
          }
        }
        currentPage = nextPage
      }
      return true
    } else {
      //  Could not find page the easy way, just push the request
      //  (don't think this ever happens)
      return pages.firstOrNull {
        it.doRequest(request)
      }?.also {
        view.clear()
        view.appendView(it.view) {
          fillParent()
        }
        currentPage = it
      } != null
    }
  }

  override fun canDoAction(action: Request.Action): Boolean =
      pages.any { it.canDoAction(action) }

  override fun sendMessage(message: Request) {
    currentPage?.sendMessage(message)
  }

}