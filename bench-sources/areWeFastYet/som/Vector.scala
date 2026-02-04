package som

import java.util.Arrays

class Vector[E <: AnyRef](initialSize: Int) {
  def this() = this(50)
  var storage: Array[AnyRef] = new Array[AnyRef](initialSize)
  var firstIdx: Int          = 0
  var lastIdx: Int           = 0

  def at(idx: Int): E = {
    if (idx >= storage.length) {
      return null.asInstanceOf[E]
    }
    storage(idx).asInstanceOf[E]
  }

  def atPut(idx: Int, value: E): Unit = {
    if (idx >= storage.length) {
      var newLength = storage.length
      while (newLength <= idx) {
        newLength *= 2
      }
      storage = Arrays.copyOf(storage, newLength)
    }
    storage(idx) = value
    if (lastIdx < idx + 1) {
      lastIdx = idx + 1
    }
  }

  def append(elem: E): Unit = {
    if (lastIdx >= storage.length) {
      // Need to expand capacity first
      storage = Arrays.copyOf(storage, 2 * storage.length)
    }

    storage(lastIdx) = elem
    lastIdx += 1
  }

  def isEmpty(): Boolean = lastIdx == firstIdx

  def forEach(f: E => Unit): Unit =
    (firstIdx until lastIdx).foreach { i =>
      f(storage(i).asInstanceOf[E])
    }

  def hasSome(f: E => Boolean): Boolean = {
    var i = firstIdx
    while (i < lastIdx) {
      if (f(storage(i).asInstanceOf[E])) return true
      i += 1
    }
    false
  }

  def getOne(f: E => Boolean): E = {
    var i = firstIdx
    while (i < lastIdx) {
      val e = storage(i).asInstanceOf[E]
      if (f(e)) return e
      i += 1
    }
    null.asInstanceOf[E]
  }

  def first(): E = {
    if (isEmpty()) {
      return null.asInstanceOf[E]
    }
    return storage(firstIdx).asInstanceOf[E]
  }

  def removeFirst(): E = {
    if (isEmpty()) {
      return null.asInstanceOf[E]
    }
    firstIdx += 1
    return storage(firstIdx - 1).asInstanceOf[E]
  }

  def remove(obj: E): Boolean = {
    val newArray = new Array[AnyRef](capacity())
    val newLast  = Array(0)
    val found    = Array(false)

    forEach { it =>
      if (it == obj) {
        found(0) = true
      } else {
        newArray(newLast(0)) = it
        newLast(0) += 1
      }
    }

    storage = newArray
    lastIdx = newLast(0)
    firstIdx = 0
    return found(0)
  }

  def removeAll(): Unit = {
    firstIdx = 0
    lastIdx = 0
    storage = new Array[AnyRef](storage.length)
  }

  def size(): Int = lastIdx - firstIdx

  def capacity(): Int = storage.length
}

object Vector {
  def `with`[E <: AnyRef](elem: E): Vector[E] = {
    val v = new Vector[E](1)
    v.append(elem)
    v
  }
}
