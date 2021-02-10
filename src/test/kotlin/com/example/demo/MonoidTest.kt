package com.example.demo

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.PropertyContext
import io.kotest.property.forAll

class MonoidTest : StringSpec({
    "monoid identity law - intAddition" {
        intAddition.monoidIdentityLawProp()
    }

    "monoid identity law - idMultiplication" {
        intMultiplication.monoidIdentityLawProp()
    }

    "monoid identity law - booleanAnd" {
        booleanAnd.monoidIdentityLawProp()
    }

    "monoid identity law - booleanOr" {
        booleanOr.monoidIdentityLawProp()
    }

    "monoid associative law - intAddition" {
        intAddition.monoidAssociativeLawProp()
    }

    "monoid associative law - idMultiplication" {
        intMultiplication.monoidAssociativeLawProp()
    }

    "monoid associative law - booleanAnd" {
        booleanAnd.monoidAssociativeLawProp()
    }

    "monoid associative law - booleanOr" {
        booleanOr.monoidAssociativeLawProp()
    }

    "monoid homomorphism test 1" {
        forAll<List<String>, List<String>> { x, y ->
            val xs = x.toString()
            val ys = y.toString()
            /**
             * monoid 준동형사상
             * 모노이드 M과 N 사이의 모노이드 준동형사상 f는 모든 x, y 값에 대해 다음과 같은 일반 법칙을 따른다:
             * M.op(f(x), f(y)) == f(N.op(x, y))
             */
            wcMonoid.op(countWords(xs), countWords(ys)) == countWords(stringConcatMonoid.op(xs, ys))
        }
    }

    "monoid homomorhpism test 2" {
        val f : (List<Char>) -> String = {
            it.foldRight("") { a, b ->
                a + b
            }
        }

        forAll<List<Char>, List<Char>> { x, y ->
            f(charsConcatMonoid.op(x, y)) == stringConcatMonoid.op(f(x), f(y))
        }
    }

    "monoid isomorphism test" {
        val g : (String) -> List<Char> = {
            it.toCharArray().toList()
        }


        forAll<String, String> { x, y ->
            g(stringConcatMonoid.op(x, y)) == charsConcatMonoid.op(g(x), g(y))
        }
    }
})

suspend inline fun <reified A> Monoid<A>.monoidIdentityLawProp(): PropertyContext =
    forAll<A> { a ->
        monoidIdentityLaw(this@monoidIdentityLawProp, a)
    }

suspend inline fun <reified A> Monoid<A>.monoidAssociativeLawProp(): PropertyContext =
    forAll<A, A, A> { a, b, c ->
        monoidAssociativeLaw(this@monoidAssociativeLawProp, Triple(a, b, c))
    }


fun <A> monoidIdentityLaw(m: Monoid<A>, element: A): Boolean =
    m.op(element, m.zero) == element && m.op(m.zero, element) == element

fun <A> monoidAssociativeLaw(m: Monoid<A>, elements: Triple<A, A, A>): Boolean =
    m.op(m.op(elements.first, elements.second), elements.third) == m.op(elements.first, m.op(elements.second, elements.third))
