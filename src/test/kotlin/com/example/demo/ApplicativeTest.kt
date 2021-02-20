package com.example.demo

import arrow.Kind
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.PropertyContext
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.forAll

class ApplicativeTest : StringSpec({
    "left identity law" {
        forAll(nelArb) { seq ->
            leftIdLaw(ListApplicative, ListK(seq))
        }
    }

    "right identity law" {
        forAll(nelArb) { seq ->
            rightIdLaw(ListApplicative, ListK(seq))
        }
    }

    "associative law" {
        forAll(nelArb, nelArb, nelArb) { a, b, c ->
            assocLaw(ListApplicative, ListK(a), ListK(b), ListK(c))
        }
    }

    "naturality law" {
        forAll(nelArb, nelArb) { a, b ->
            val f: (Int) -> String = { it.toString() }
            val g: (Int) -> Int = { it * it }
            naturalityLaw(ListApplicative, ListK(a), ListK(b), f, g)
        }
    }
})

val nelArb = Arb.list(Arb.int(), 1..Int.MAX_VALUE)

fun <F, A> leftIdLaw(ap: Applicative<F>, fa: Kind<F, A>): Boolean =
    ap.run {
        map2(unit { }, fa) { _, a -> a } == fa
    }

fun <F, A> rightIdLaw(ap: Applicative<F>, fa: Kind<F, A>): Boolean =
    ap.run {
        map2(fa, unit { }) { a, _ -> a } == fa
    }

fun <A, B, C> assoc(p: Pair<A, Pair<B, C>>): Pair<Pair<A, B>, C> = (p.first to p.second.first) to p.second.second

fun <F, A, B, C> assocLaw(ap: Applicative<F>, fa: Kind<F, A>, fb: Kind<F, B>, fc: Kind<F, C>): Boolean =
    ap.run {
        product(product(fa, fb), fc) == product(fa, product(fb, fc)).map { assoc(it) }
    }

fun <I, O, I2, O2> productF(f: (I) -> O, g: (I2) -> O2): (I, I2) -> Pair<O, O2> = { i, i2 -> f(i) to g(i2) }

fun <F, A, B, C, D> naturalityLaw(ap: Applicative<F>, fa: Kind<F, A>, fb: Kind<F, B>, f: (A) -> C, g: (B) -> D): Boolean =
    ap.run {
        map2(fa, fb) { a, b -> productF(f, g)(a, b) } == product(fa.map(f), fb.map(g))
    }
