package com.bradchristie.taminations.platform

abstract class CachingAdapter {
  abstract fun numberOfItems(): Int
  abstract fun getItem(i:Int): View
}

expect class CachingLinearLayout(adapter: CachingAdapter) : ViewGroup {

}