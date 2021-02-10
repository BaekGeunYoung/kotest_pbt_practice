package com.example.demo

import arrow.Kind
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll

class MonadTest : StringSpec({
    "monad associative law 1" {
        forAll<List<Int>> { seq ->
            monadAssociativeLaw(
                ListMonad,
                ListK(seq),
                { ListK(listOf(it, it + 1, it + 2)) },
                { ListK(listOf(it, it * 10, it * 20)) }
            )
        }
    }

    "monad associative law 2" {
        forAll<Int> { i ->
            monadAssociativeLaw2(
                ListMonad,
                i,
                { ListK(listOf(it, it + 1, it + 2)) },
                { ListK(listOf(it, it * 10, it * 20)) },
                { ListK(listOf(it, it - 3, it - 7)) }
            )
        }
    }

    "monad identity law 1" {
        forAll<List<Int>> { seq ->
            monadIdentityLaw(ListMonad, ListK(seq))
        }
    }

    "monad identity law 2" {
        forAll<Int> { i ->
            monadIdentityLaw2(ListMonad, i) {
                ListK(listOf(it.toString()))
            }
        }
    }
})

fun <F, A, B, C> monadAssociativeLaw(m: Monad<F>, monad: Kind<F, A>, f: (A) -> Kind<F, B>, g: (B) -> Kind<F, C>): Boolean =
    m.run {
        monad.flatMap(f).flatMap(g) == monad.flatMap { f(it).flatMap(g) }
    }

fun <F, A, B, C, D> monadAssociativeLaw2(m: Monad<F>, element: A, f: (A) -> Kind<F, B>, g: (B) -> Kind<F, C>, h: (C) -> Kind<F, D>): Boolean =
    m.run {
        compose(compose(f, g), h)(element) == compose(f, compose(g, h))(element)
    }

fun <F, A> monadIdentityLaw(m: Monad<F>, monad: Kind<F, A>): Boolean =
    m.run {
        monad.flatMap { unit { it } } == monad
    }

fun <F, A, B> monadIdentityLaw2(m: Monad<F>, element: A, f: (A) -> Kind<F, B>): Boolean =
    m.run {
        compose(f, { unit { it } })(element) == f(element)
    }
