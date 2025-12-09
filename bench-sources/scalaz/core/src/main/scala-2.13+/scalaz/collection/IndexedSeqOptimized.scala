package scalaz.collection

import scala.collection.mutable.Builder
import scala.annotation.unchecked.uncheckedVariance

private[scalaz] trait IndexedSeqOptimized[+A, +Repr] extends Any {
  protected[this] def newBuilder: Builder[A @uncheckedVariance, Repr]
}
