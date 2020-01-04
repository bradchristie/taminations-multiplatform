package com.bradchristie.taminations.platform
/*

  Taminations Square Dance Animations for Web Browsers
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

import org.w3c.dom.*
import kotlin.browser.document
abstract external class TypeError : Throwable

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
    val mydoc = e.ownerDocument.unsafeCast<XMLDocumentWithXPath>()
    //  Internet Explorer has poor support for XML, it will fail here
    return try {
      mydoc.evaluate(expr, e, null, 0, null).toList()
      //  .. with a TypeError
    } catch (e:TypeError) {
      document.location!!.href = "notsupported.html"
      listOf()
    }
  }

}

actual class TamDocument(private val doc:XMLDocument) {

  actual fun evalXPath(expr: String): List<TamElement> {
    val mydoc = doc.unsafeCast<XMLDocumentWithXPath>()
    //  Internet Explorer has poor support for XML, it will fail here
    return try {
      mydoc.evaluate(expr, doc, null, 0, null).toList()
      //  .. with a TypeError
    } catch (e:TypeError) {
      document.location!!.href = "notsupported.html"
      listOf()
    }
  }

}

//  Convenience methods to evaluate a XPath expression
open external class XPathResult {
  fun iterateNext() : Element?
}
fun XPathResult.toList():List<TamElement> {
  val list = ArrayList<TamElement>()
  var e = this.iterateNext()
  while (e != null) {
    list.add(TamElement(e))
    e = this.iterateNext()
  }
  return list
}
open external class XMLDocumentWithXPath : XMLDocument {
  fun evaluate(xpathExpression:String, contextNode:Node,
               namespaceResolver:Any?, resultType:Int, result:Any?) : XPathResult
}

//  Misc DOM methods
fun Document.createHTMLElement(tag:String, init: HTMLElement.()->Unit = { }): HTMLElement =
    (createElement(tag) as HTMLElement).apply(init)

@Suppress("UNCHECKED_CAST")
fun <T> Document.create(tag:String, init:T.()->Unit):T
    = (createElement(tag) as T).apply(init)

fun HTMLElement.appendHTMLElement(tag:String, init: HTMLElement.()->Unit = { }): HTMLElement =
    ownerDocument!!.createHTMLElement(tag,init).also { appendChild(it) }
