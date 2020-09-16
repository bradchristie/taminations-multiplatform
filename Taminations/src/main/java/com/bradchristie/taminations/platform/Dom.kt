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

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

actual class TamElement(private val e: Element) {

  actual val tag: String = e.tagName
  actual val textContent = e.textContent ?: ""
  actual fun hasAttribute(tag: String): Boolean = e.hasAttribute(tag)
  actual fun getAttribute(tag: String): String? = e.getAttribute(tag)
  actual fun children(tag:String):List<TamElement> {
    val nodes = e.getElementsByTagName(tag)
    return (0 until nodes.length).map { TamElement(nodes.item(it) as Element) }
  }
  actual fun evalXPath(expr: String): List<TamElement> {
    val xpath = XPathFactory.newInstance().newXPath()
    val nodes = xpath.evaluate(expr,e, XPathConstants.NODESET) as NodeList
    return (0 until nodes.length).map { TamElement(nodes.item(it) as Element) }
  }
}

actual class TamDocument(private val doc: Document) {

  actual fun evalXPath(expr: String): List<TamElement>
      = TamElement(doc.documentElement).evalXPath(expr)

}
