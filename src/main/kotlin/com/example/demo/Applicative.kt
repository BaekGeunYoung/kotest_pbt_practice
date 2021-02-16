package com.example.demo

import arrow.Kind

interface Applicative<F> : Functor<F> {
    fun <A> unit(a: () -> A): Kind<F, A>
    fun <A, B, C> map2(fa: Kind<F, A>, fb: Kind<F, B>, f: (A, B) -> C): Kind<F, C>
    fun <A, B> apply(fab: Kind<F, (A) -> B>, fa: Kind<F, A>): Kind<F, B> = map2(fab, fa) { ab, a -> ab(a) }
    override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = map2(this, unit { }) { a, _ -> f(a) }

    fun <A, B> product(fa: Kind<F, A>, fb: Kind<F, B>): Kind<F, Pair<A, B>> = map2(fa, fb) { a, b -> a to b }
}

object ListApplicative: Applicative<ForListK> {
    override fun <A> unit(a: () -> A): Kind<ForListK, A> = ListK(listOf(a()))

    override fun <A, B, C> map2(fa: Kind<ForListK, A>, fb: Kind<ForListK, B>, f: (A, B) -> C): Kind<ForListK, C> {
        val a = fa.fix().list
        val b = fb.fix().list

        val longerList = if (a.size < b.size) b else a
        val longerSize = if (a.size < b.size) b.size else a.size

        if (longerList == a) {
            val mutable = b.toMutableList()
            val firstElement = b[0]
            (0 until longerSize - b.size).forEach {
                mutable.add(firstElement)
            }

            val calibrated: List<B> = mutable

            return ListK(
                a.zip(calibrated).map {
                    f(it.first, it.second)
                }
            )
        } else {
            val mutable = a.toMutableList()
            val firstElement = a[0]
            (0 until longerSize - a.size).forEach {
                mutable.add(firstElement)
            }

            val calibrated: List<A> = mutable

            return ListK(
                calibrated.zip(b).map {
                    f(it.first, it.second)
                }
            )
        }
    }

}
