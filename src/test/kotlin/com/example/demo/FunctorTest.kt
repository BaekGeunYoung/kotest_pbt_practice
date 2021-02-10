package com.example.demo

import arrow.Kind
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll
import java.util.Optional

class FunctorTest : StringSpec({
    "functor identity law - int list" {
        forAll<List<Int>> { seq ->
            functorIdentityLaw(ListFunctor, ListK(seq))
        }
    }

    "functor identity law - string list" {
        forAll<List<String>> { seq ->
            functorIdentityLaw(ListFunctor, ListK(seq))
        }
    }

    "functor identity law - int option" {
        forAll<Int> { i ->
            functorIdentityLaw(OptionFunctor, OptionK(Optional.of(i)))
        }
    }
    "functor identity law - string option" {
        forAll<Int> { i ->
            functorIdentityLaw(OptionFunctor, OptionK(Optional.of(i)))
        }
    }
    "functor identity law - int id" {
        forAll<String> { i ->
            functorIdentityLaw(IdFunctor, IdK(i))
        }
    }
    "functor identity law - string id" {
        forAll<String> { i ->
            functorIdentityLaw(IdFunctor, IdK(i))
        }
    }
})

fun <F, A> functorIdentityLaw(f: Functor<F>, functor: Kind<F, A>): Boolean =
    f.run {
        functor.map { it } == functor
    }
