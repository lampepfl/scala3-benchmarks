package scalaz
package syntax
package std

import scala.collection.immutable.IndexedSeq


final class Tuple2Ops[A, B](val value: (A, B)) extends AnyVal {
  def fold[Z](f: => (A, B) => Z): Z = {import value._; f(_1, _2)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple2[Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2)}
  def mapElements[AA, BB](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _): (AA, BB) = (_1(value._1), _2(value._2))
}

final class Tuple3Ops[A, B, C](val value: (A, B, C)) extends AnyVal {
  def fold[Z](f: => (A, B, C) => Z): Z = {import value._; f(_1, _2, _3)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple3[Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3)}
  def mapElements[AA, BB, CC](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _): (AA, BB, CC) = (_1(value._1), _2(value._2), _3(value._3))
}

final class Tuple4Ops[A, B, C, D](val value: (A, B, C, D)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D) => Z): Z = {import value._; f(_1, _2, _3, _4)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple4[Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4)}
  def mapElements[AA, BB, CC, DD](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _): (AA, BB, CC, DD) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4))
}

final class Tuple5Ops[A, B, C, D, E](val value: (A, B, C, D, E)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E) => Z): Z = {import value._; f(_1, _2, _3, _4, _5)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple5[Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5)}
  def mapElements[AA, BB, CC, DD, EE](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _): (AA, BB, CC, DD, EE) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5))
}

final class Tuple6Ops[A, B, C, D, E, F](val value: (A, B, C, D, E, F)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple6[Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6)}
  def mapElements[AA, BB, CC, DD, EE, FF](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _): (AA, BB, CC, DD, EE, FF) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6))
}

final class Tuple7Ops[A, B, C, D, E, F, G](val value: (A, B, C, D, E, F, G)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F, G) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6, _7)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple7[Z, Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6, _7)}
  def mapElements[AA, BB, CC, DD, EE, FF, GG](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _, _7: (G => GG) = identity[G] _): (AA, BB, CC, DD, EE, FF, GG) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6), _7(value._7))
}

final class Tuple8Ops[A, B, C, D, E, F, G, H](val value: (A, B, C, D, E, F, G, H)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F, G, H) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6, _7, _8)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple8[Z, Z, Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6, _7, _8)}
  def mapElements[AA, BB, CC, DD, EE, FF, GG, HH](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _, _7: (G => GG) = identity[G] _, _8: (H => HH) = identity[H] _): (AA, BB, CC, DD, EE, FF, GG, HH) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6), _7(value._7), _8(value._8))
}

final class Tuple9Ops[A, B, C, D, E, F, G, H, I](val value: (A, B, C, D, E, F, G, H, I)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F, G, H, I) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6, _7, _8, _9)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple9[Z, Z, Z, Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6, _7, _8, _9)}
  def mapElements[AA, BB, CC, DD, EE, FF, GG, HH, II](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _, _7: (G => GG) = identity[G] _, _8: (H => HH) = identity[H] _, _9: (I => II) = identity[I] _): (AA, BB, CC, DD, EE, FF, GG, HH, II) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6), _7(value._7), _8(value._8), _9(value._9))
}

final class Tuple10Ops[A, B, C, D, E, F, G, H, I, J](val value: (A, B, C, D, E, F, G, H, I, J)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F, G, H, I, J) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple10[Z, Z, Z, Z, Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10)}
  def mapElements[AA, BB, CC, DD, EE, FF, GG, HH, II, JJ](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _, _7: (G => GG) = identity[G] _, _8: (H => HH) = identity[H] _, _9: (I => II) = identity[I] _, _10: (J => JJ) = identity[J] _): (AA, BB, CC, DD, EE, FF, GG, HH, II, JJ) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6), _7(value._7), _8(value._8), _9(value._9), _10(value._10))
}

final class Tuple11Ops[A, B, C, D, E, F, G, H, I, J, K](val value: (A, B, C, D, E, F, G, H, I, J, K)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F, G, H, I, J, K) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple11[Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11)}
  def mapElements[AA, BB, CC, DD, EE, FF, GG, HH, II, JJ, KK](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _, _7: (G => GG) = identity[G] _, _8: (H => HH) = identity[H] _, _9: (I => II) = identity[I] _, _10: (J => JJ) = identity[J] _, _11: (K => KK) = identity[K] _): (AA, BB, CC, DD, EE, FF, GG, HH, II, JJ, KK) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6), _7(value._7), _8(value._8), _9(value._9), _10(value._10), _11(value._11))
}

final class Tuple12Ops[A, B, C, D, E, F, G, H, I, J, K, L](val value: (A, B, C, D, E, F, G, H, I, J, K, L)) extends AnyVal {
  def fold[Z](f: => (A, B, C, D, E, F, G, H, I, J, K, L) => Z): Z = {import value._; f(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)}
  def toIndexedSeq[Z](implicit ev: value.type <:< Tuple12[Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z, Z]): IndexedSeq[Z] = {val zs = ev(value); import zs._; IndexedSeq(_1, _2, _3, _4, _5, _6, _7, _8, _9, _10, _11, _12)}
  def mapElements[AA, BB, CC, DD, EE, FF, GG, HH, II, JJ, KK, LL](_1: (A => AA) = identity[A] _, _2: (B => BB) = identity[B] _, _3: (C => CC) = identity[C] _, _4: (D => DD) = identity[D] _, _5: (E => EE) = identity[E] _, _6: (F => FF) = identity[F] _, _7: (G => GG) = identity[G] _, _8: (H => HH) = identity[H] _, _9: (I => II) = identity[I] _, _10: (J => JJ) = identity[J] _, _11: (K => KK) = identity[K] _, _12: (L => LL) = identity[L] _): (AA, BB, CC, DD, EE, FF, GG, HH, II, JJ, KK, LL) = (_1(value._1), _2(value._2), _3(value._3), _4(value._4), _5(value._5), _6(value._6), _7(value._7), _8(value._8), _9(value._9), _10(value._10), _11(value._11), _12(value._12))
}

trait ToTupleOps {
  implicit def ToTuple2Ops[A, B](t: (A, B)): Tuple2Ops[A, B] = new Tuple2Ops(t)

  implicit def ToTuple3Ops[A, B, C](t: (A, B, C)): Tuple3Ops[A, B, C] = new Tuple3Ops(t)

  implicit def ToTuple4Ops[A, B, C, D](t: (A, B, C, D)): Tuple4Ops[A, B, C, D] = new Tuple4Ops(t)

  implicit def ToTuple5Ops[A, B, C, D, E](t: (A, B, C, D, E)): Tuple5Ops[A, B, C, D, E] = new Tuple5Ops(t)

  implicit def ToTuple6Ops[A, B, C, D, E, F](t: (A, B, C, D, E, F)): Tuple6Ops[A, B, C, D, E, F] = new Tuple6Ops(t)

  implicit def ToTuple7Ops[A, B, C, D, E, F, G](t: (A, B, C, D, E, F, G)): Tuple7Ops[A, B, C, D, E, F, G] = new Tuple7Ops(t)

  implicit def ToTuple8Ops[A, B, C, D, E, F, G, H](t: (A, B, C, D, E, F, G, H)): Tuple8Ops[A, B, C, D, E, F, G, H] = new Tuple8Ops(t)

  implicit def ToTuple9Ops[A, B, C, D, E, F, G, H, I](t: (A, B, C, D, E, F, G, H, I)): Tuple9Ops[A, B, C, D, E, F, G, H, I] = new Tuple9Ops(t)

  implicit def ToTuple10Ops[A, B, C, D, E, F, G, H, I, J](t: (A, B, C, D, E, F, G, H, I, J)): Tuple10Ops[A, B, C, D, E, F, G, H, I, J] = new Tuple10Ops(t)

  implicit def ToTuple11Ops[A, B, C, D, E, F, G, H, I, J, K](t: (A, B, C, D, E, F, G, H, I, J, K)): Tuple11Ops[A, B, C, D, E, F, G, H, I, J, K] = new Tuple11Ops(t)

  implicit def ToTuple12Ops[A, B, C, D, E, F, G, H, I, J, K, L](t: (A, B, C, D, E, F, G, H, I, J, K, L)): Tuple12Ops[A, B, C, D, E, F, G, H, I, J, K, L] = new Tuple12Ops(t)
}