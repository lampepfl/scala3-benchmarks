package scalaz
package std


private[std] trait Tuple2Functor[A1] extends Traverse[(A1, *)] {
  override final def map[A, B](fa: (A1, A))(f: A => B) =
    (fa._1, f(fa._2))
  override final def traverseImpl[G[_], A, B](fa: (A1, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._2))((fa._1, _))
}

private[std] trait Tuple2BindRec[A1] extends BindRec[(A1, *)] with Tuple2Functor[A1] {
  def _1 : Semigroup[A1]

  override def bind[A, B](fa: (A1, A))(f: A => (A1, B)) = {
    val t = f(fa._2)
    (_1.append(fa._1, t._1), t._2)
  }

  override def tailrecM[A, B](f: A => (A1, A \/ B))(a: A): (A1, B) = {
    @annotation.tailrec
    def go(s1: A1)(z: A): (A1, B) =
      f(z) match {
        case (a1, b0) =>
          val x1 = _1.append(s1, a1)
          b0 match {
            case -\/(a0) => go(x1)(a0)
            case \/-(b1) => (x1, b1)
          }
      }
    val (z1, e) = f(a)
    e match {
      case -\/(a0) => go(z1)(a0)
      case \/-(b) => (z1, b)
    }
  }
}

private[std] abstract class Tuple2Monad[A1] extends Monad[(A1, *)] with Tuple2BindRec[A1] {
  override def _1 : Monoid[A1]
  def point[A](a: => A) = (_1.zero, a)
}


private[std] trait Tuple3Functor[A1, A2] extends Traverse[(A1, A2, *)] {
  override final def map[A, B](fa: (A1, A2, A))(f: A => B) =
    (fa._1, fa._2, f(fa._3))
  override final def traverseImpl[G[_], A, B](fa: (A1, A2, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._3))((fa._1, fa._2, _))
}

private[std] trait Tuple3BindRec[A1, A2] extends BindRec[(A1, A2, *)] with Tuple3Functor[A1, A2] {
  def _1 : Semigroup[A1]; def _2 : Semigroup[A2]

  override def bind[A, B](fa: (A1, A2, A))(f: A => (A1, A2, B)) = {
    val t = f(fa._3)
    (_1.append(fa._1, t._1), _2.append(fa._2, t._2), t._3)
  }

  override def tailrecM[A, B](f: A => (A1, A2, A \/ B))(a: A): (A1, A2, B) = {
    @annotation.tailrec
    def go(s1: A1, s2: A2)(z: A): (A1, A2, B) =
      f(z) match {
        case (a1, a2, b0) =>
          val x1 = _1.append(s1, a1); val x2 = _2.append(s2, a2)
          b0 match {
            case -\/(a0) => go(x1, x2)(a0)
            case \/-(b1) => (x1, x2, b1)
          }
      }
    val (z1, z2, e) = f(a)
    e match {
      case -\/(a0) => go(z1, z2)(a0)
      case \/-(b) => (z1, z2, b)
    }
  }
}

private[std] abstract class Tuple3Monad[A1, A2] extends Monad[(A1, A2, *)] with Tuple3BindRec[A1, A2] {
  override def _1 : Monoid[A1]; override def _2 : Monoid[A2]
  def point[A](a: => A) = (_1.zero, _2.zero, a)
}


private[std] trait Tuple4Functor[A1, A2, A3] extends Traverse[(A1, A2, A3, *)] {
  override final def map[A, B](fa: (A1, A2, A3, A))(f: A => B) =
    (fa._1, fa._2, fa._3, f(fa._4))
  override final def traverseImpl[G[_], A, B](fa: (A1, A2, A3, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._4))((fa._1, fa._2, fa._3, _))
}

private[std] trait Tuple4BindRec[A1, A2, A3] extends BindRec[(A1, A2, A3, *)] with Tuple4Functor[A1, A2, A3] {
  def _1 : Semigroup[A1]; def _2 : Semigroup[A2]; def _3 : Semigroup[A3]

  override def bind[A, B](fa: (A1, A2, A3, A))(f: A => (A1, A2, A3, B)) = {
    val t = f(fa._4)
    (_1.append(fa._1, t._1), _2.append(fa._2, t._2), _3.append(fa._3, t._3), t._4)
  }

  override def tailrecM[A, B](f: A => (A1, A2, A3, A \/ B))(a: A): (A1, A2, A3, B) = {
    @annotation.tailrec
    def go(s1: A1, s2: A2, s3: A3)(z: A): (A1, A2, A3, B) =
      f(z) match {
        case (a1, a2, a3, b0) =>
          val x1 = _1.append(s1, a1); val x2 = _2.append(s2, a2); val x3 = _3.append(s3, a3)
          b0 match {
            case -\/(a0) => go(x1, x2, x3)(a0)
            case \/-(b1) => (x1, x2, x3, b1)
          }
      }
    val (z1, z2, z3, e) = f(a)
    e match {
      case -\/(a0) => go(z1, z2, z3)(a0)
      case \/-(b) => (z1, z2, z3, b)
    }
  }
}

private[std] abstract class Tuple4Monad[A1, A2, A3] extends Monad[(A1, A2, A3, *)] with Tuple4BindRec[A1, A2, A3] {
  override def _1 : Monoid[A1]; override def _2 : Monoid[A2]; override def _3 : Monoid[A3]
  def point[A](a: => A) = (_1.zero, _2.zero, _3.zero, a)
}


private[std] trait Tuple5Functor[A1, A2, A3, A4] extends Traverse[(A1, A2, A3, A4, *)] {
  override final def map[A, B](fa: (A1, A2, A3, A4, A))(f: A => B) =
    (fa._1, fa._2, fa._3, fa._4, f(fa._5))
  override final def traverseImpl[G[_], A, B](fa: (A1, A2, A3, A4, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._5))((fa._1, fa._2, fa._3, fa._4, _))
}

private[std] trait Tuple5BindRec[A1, A2, A3, A4] extends BindRec[(A1, A2, A3, A4, *)] with Tuple5Functor[A1, A2, A3, A4] {
  def _1 : Semigroup[A1]; def _2 : Semigroup[A2]; def _3 : Semigroup[A3]; def _4 : Semigroup[A4]

  override def bind[A, B](fa: (A1, A2, A3, A4, A))(f: A => (A1, A2, A3, A4, B)) = {
    val t = f(fa._5)
    (_1.append(fa._1, t._1), _2.append(fa._2, t._2), _3.append(fa._3, t._3), _4.append(fa._4, t._4), t._5)
  }

  override def tailrecM[A, B](f: A => (A1, A2, A3, A4, A \/ B))(a: A): (A1, A2, A3, A4, B) = {
    @annotation.tailrec
    def go(s1: A1, s2: A2, s3: A3, s4: A4)(z: A): (A1, A2, A3, A4, B) =
      f(z) match {
        case (a1, a2, a3, a4, b0) =>
          val x1 = _1.append(s1, a1); val x2 = _2.append(s2, a2); val x3 = _3.append(s3, a3); val x4 = _4.append(s4, a4)
          b0 match {
            case -\/(a0) => go(x1, x2, x3, x4)(a0)
            case \/-(b1) => (x1, x2, x3, x4, b1)
          }
      }
    val (z1, z2, z3, z4, e) = f(a)
    e match {
      case -\/(a0) => go(z1, z2, z3, z4)(a0)
      case \/-(b) => (z1, z2, z3, z4, b)
    }
  }
}

private[std] abstract class Tuple5Monad[A1, A2, A3, A4] extends Monad[(A1, A2, A3, A4, *)] with Tuple5BindRec[A1, A2, A3, A4] {
  override def _1 : Monoid[A1]; override def _2 : Monoid[A2]; override def _3 : Monoid[A3]; override def _4 : Monoid[A4]
  def point[A](a: => A) = (_1.zero, _2.zero, _3.zero, _4.zero, a)
}


private[std] trait Tuple6Functor[A1, A2, A3, A4, A5] extends Traverse[(A1, A2, A3, A4, A5, *)] {
  override final def map[A, B](fa: (A1, A2, A3, A4, A5, A))(f: A => B) =
    (fa._1, fa._2, fa._3, fa._4, fa._5, f(fa._6))
  override final def traverseImpl[G[_], A, B](fa: (A1, A2, A3, A4, A5, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._6))((fa._1, fa._2, fa._3, fa._4, fa._5, _))
}

private[std] trait Tuple6BindRec[A1, A2, A3, A4, A5] extends BindRec[(A1, A2, A3, A4, A5, *)] with Tuple6Functor[A1, A2, A3, A4, A5] {
  def _1 : Semigroup[A1]; def _2 : Semigroup[A2]; def _3 : Semigroup[A3]; def _4 : Semigroup[A4]; def _5 : Semigroup[A5]

  override def bind[A, B](fa: (A1, A2, A3, A4, A5, A))(f: A => (A1, A2, A3, A4, A5, B)) = {
    val t = f(fa._6)
    (_1.append(fa._1, t._1), _2.append(fa._2, t._2), _3.append(fa._3, t._3), _4.append(fa._4, t._4), _5.append(fa._5, t._5), t._6)
  }

  override def tailrecM[A, B](f: A => (A1, A2, A3, A4, A5, A \/ B))(a: A): (A1, A2, A3, A4, A5, B) = {
    @annotation.tailrec
    def go(s1: A1, s2: A2, s3: A3, s4: A4, s5: A5)(z: A): (A1, A2, A3, A4, A5, B) =
      f(z) match {
        case (a1, a2, a3, a4, a5, b0) =>
          val x1 = _1.append(s1, a1); val x2 = _2.append(s2, a2); val x3 = _3.append(s3, a3); val x4 = _4.append(s4, a4); val x5 = _5.append(s5, a5)
          b0 match {
            case -\/(a0) => go(x1, x2, x3, x4, x5)(a0)
            case \/-(b1) => (x1, x2, x3, x4, x5, b1)
          }
      }
    val (z1, z2, z3, z4, z5, e) = f(a)
    e match {
      case -\/(a0) => go(z1, z2, z3, z4, z5)(a0)
      case \/-(b) => (z1, z2, z3, z4, z5, b)
    }
  }
}

private[std] abstract class Tuple6Monad[A1, A2, A3, A4, A5] extends Monad[(A1, A2, A3, A4, A5, *)] with Tuple6BindRec[A1, A2, A3, A4, A5] {
  override def _1 : Monoid[A1]; override def _2 : Monoid[A2]; override def _3 : Monoid[A3]; override def _4 : Monoid[A4]; override def _5 : Monoid[A5]
  def point[A](a: => A) = (_1.zero, _2.zero, _3.zero, _4.zero, _5.zero, a)
}


private[std] trait Tuple7Functor[A1, A2, A3, A4, A5, A6] extends Traverse[(A1, A2, A3, A4, A5, A6, *)] {
  override final def map[A, B](fa: (A1, A2, A3, A4, A5, A6, A))(f: A => B) =
    (fa._1, fa._2, fa._3, fa._4, fa._5, fa._6, f(fa._7))
  override final def traverseImpl[G[_], A, B](fa: (A1, A2, A3, A4, A5, A6, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._7))((fa._1, fa._2, fa._3, fa._4, fa._5, fa._6, _))
}

private[std] trait Tuple7BindRec[A1, A2, A3, A4, A5, A6] extends BindRec[(A1, A2, A3, A4, A5, A6, *)] with Tuple7Functor[A1, A2, A3, A4, A5, A6] {
  def _1 : Semigroup[A1]; def _2 : Semigroup[A2]; def _3 : Semigroup[A3]; def _4 : Semigroup[A4]; def _5 : Semigroup[A5]; def _6 : Semigroup[A6]

  override def bind[A, B](fa: (A1, A2, A3, A4, A5, A6, A))(f: A => (A1, A2, A3, A4, A5, A6, B)) = {
    val t = f(fa._7)
    (_1.append(fa._1, t._1), _2.append(fa._2, t._2), _3.append(fa._3, t._3), _4.append(fa._4, t._4), _5.append(fa._5, t._5), _6.append(fa._6, t._6), t._7)
  }

  override def tailrecM[A, B](f: A => (A1, A2, A3, A4, A5, A6, A \/ B))(a: A): (A1, A2, A3, A4, A5, A6, B) = {
    @annotation.tailrec
    def go(s1: A1, s2: A2, s3: A3, s4: A4, s5: A5, s6: A6)(z: A): (A1, A2, A3, A4, A5, A6, B) =
      f(z) match {
        case (a1, a2, a3, a4, a5, a6, b0) =>
          val x1 = _1.append(s1, a1); val x2 = _2.append(s2, a2); val x3 = _3.append(s3, a3); val x4 = _4.append(s4, a4); val x5 = _5.append(s5, a5); val x6 = _6.append(s6, a6)
          b0 match {
            case -\/(a0) => go(x1, x2, x3, x4, x5, x6)(a0)
            case \/-(b1) => (x1, x2, x3, x4, x5, x6, b1)
          }
      }
    val (z1, z2, z3, z4, z5, z6, e) = f(a)
    e match {
      case -\/(a0) => go(z1, z2, z3, z4, z5, z6)(a0)
      case \/-(b) => (z1, z2, z3, z4, z5, z6, b)
    }
  }
}

private[std] abstract class Tuple7Monad[A1, A2, A3, A4, A5, A6] extends Monad[(A1, A2, A3, A4, A5, A6, *)] with Tuple7BindRec[A1, A2, A3, A4, A5, A6] {
  override def _1 : Monoid[A1]; override def _2 : Monoid[A2]; override def _3 : Monoid[A3]; override def _4 : Monoid[A4]; override def _5 : Monoid[A5]; override def _6 : Monoid[A6]
  def point[A](a: => A) = (_1.zero, _2.zero, _3.zero, _4.zero, _5.zero, _6.zero, a)
}


private[std] trait Tuple8Functor[A1, A2, A3, A4, A5, A6, A7] extends Traverse[(A1, A2, A3, A4, A5, A6, A7, *)] {
  override final def map[A, B](fa: (A1, A2, A3, A4, A5, A6, A7, A))(f: A => B) =
    (fa._1, fa._2, fa._3, fa._4, fa._5, fa._6, fa._7, f(fa._8))
  override final def traverseImpl[G[_], A, B](fa: (A1, A2, A3, A4, A5, A6, A7, A))(f: A => G[B])(implicit G: Applicative[G]) =
    G.map(f(fa._8))((fa._1, fa._2, fa._3, fa._4, fa._5, fa._6, fa._7, _))
}

private[std] trait Tuple8BindRec[A1, A2, A3, A4, A5, A6, A7] extends BindRec[(A1, A2, A3, A4, A5, A6, A7, *)] with Tuple8Functor[A1, A2, A3, A4, A5, A6, A7] {
  def _1 : Semigroup[A1]; def _2 : Semigroup[A2]; def _3 : Semigroup[A3]; def _4 : Semigroup[A4]; def _5 : Semigroup[A5]; def _6 : Semigroup[A6]; def _7 : Semigroup[A7]

  override def bind[A, B](fa: (A1, A2, A3, A4, A5, A6, A7, A))(f: A => (A1, A2, A3, A4, A5, A6, A7, B)) = {
    val t = f(fa._8)
    (_1.append(fa._1, t._1), _2.append(fa._2, t._2), _3.append(fa._3, t._3), _4.append(fa._4, t._4), _5.append(fa._5, t._5), _6.append(fa._6, t._6), _7.append(fa._7, t._7), t._8)
  }

  override def tailrecM[A, B](f: A => (A1, A2, A3, A4, A5, A6, A7, A \/ B))(a: A): (A1, A2, A3, A4, A5, A6, A7, B) = {
    @annotation.tailrec
    def go(s1: A1, s2: A2, s3: A3, s4: A4, s5: A5, s6: A6, s7: A7)(z: A): (A1, A2, A3, A4, A5, A6, A7, B) =
      f(z) match {
        case (a1, a2, a3, a4, a5, a6, a7, b0) =>
          val x1 = _1.append(s1, a1); val x2 = _2.append(s2, a2); val x3 = _3.append(s3, a3); val x4 = _4.append(s4, a4); val x5 = _5.append(s5, a5); val x6 = _6.append(s6, a6); val x7 = _7.append(s7, a7)
          b0 match {
            case -\/(a0) => go(x1, x2, x3, x4, x5, x6, x7)(a0)
            case \/-(b1) => (x1, x2, x3, x4, x5, x6, x7, b1)
          }
      }
    val (z1, z2, z3, z4, z5, z6, z7, e) = f(a)
    e match {
      case -\/(a0) => go(z1, z2, z3, z4, z5, z6, z7)(a0)
      case \/-(b) => (z1, z2, z3, z4, z5, z6, z7, b)
    }
  }
}

private[std] abstract class Tuple8Monad[A1, A2, A3, A4, A5, A6, A7] extends Monad[(A1, A2, A3, A4, A5, A6, A7, *)] with Tuple8BindRec[A1, A2, A3, A4, A5, A6, A7] {
  override def _1 : Monoid[A1]; override def _2 : Monoid[A2]; override def _3 : Monoid[A3]; override def _4 : Monoid[A4]; override def _5 : Monoid[A5]; override def _6 : Monoid[A6]; override def _7 : Monoid[A7]
  def point[A](a: => A) = (_1.zero, _2.zero, _3.zero, _4.zero, _5.zero, _6.zero, _7.zero, a)
}
