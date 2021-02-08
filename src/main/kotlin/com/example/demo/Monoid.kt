package com.example.demo

import java.util.Optional

interface Monoid <A> {
    val zero: A
    fun op(a1: A, a2: A): A
}

val intAddition = object: Monoid<Int> {
    override val zero: Int = 0
    override fun op(a1: Int, a2: Int): Int = a1 + a2
}

val intMultiplication = object : Monoid<Int> {
    override val zero: Int = 1
    override fun op(a1: Int, a2: Int): Int = a1 * a2
}

val booleanOr = object : Monoid<Boolean> {
    override val zero: Boolean = false
    override fun op(a1: Boolean, a2: Boolean): Boolean = a1 || a2
}

val booleanAnd = object : Monoid<Boolean> {
    override val zero: Boolean = true
    override fun op(a1: Boolean, a2: Boolean): Boolean = a1 && a2
}

val charsConcatMonoid: Monoid<List<Char>> = object : Monoid<List<Char>> {
    override val zero: List<Char> = listOf()
    override fun op(a1: List<Char>, a2: List<Char>): List<Char> =
        a1.toMutableList().also {
            it.addAll(a2)
        }
}

fun <A> optionMonoid() = object : Monoid<Optional<A>> {
    override val zero: Optional<A> = Optional.empty()
    override fun op(a1: Optional<A>, a2: Optional<A>): Optional<A> = if (a1.isPresent) a1 else a2
}

fun <A> endoMonoid() = object : Monoid<(A) -> A> {
    override val zero: (A) -> A = { it }
    override fun op(a1: (A) -> A, a2: (A) -> A): (A) -> A = {
        a2(a1(it))
    }
}

sealed class WC

data class Stub(val chars: String): WC()

data class Part(val lStub: String, val words: Int, val rStub: String): WC()

val stringConcatMonoid: Monoid<String> = object : Monoid<String> {
    override val zero: String = ""

    override fun op(a1: String, a2: String): String = a1 + a2

}

val wcMonoid: Monoid<WC> = object : Monoid<WC> {
    override val zero: WC = Stub("")

    override fun op(a1: WC, a2: WC): WC {
        when (a1) {
            is Stub -> return when (a2) {
                is Stub -> {
                    Stub(a1.chars + a2.chars)
                }
                is Part -> {
                    Part(a1.chars + a2.lStub, a2.words, a2.rStub)
                }
            }

            is Part -> return when (a2) {
                is Stub -> {
                    Part(a1.lStub, a1.words, a1.rStub + a2.chars)
                }
                is Part -> {
                    val words = a1.words + a2.words
                    if (a1.rStub.isEmpty() && a2.lStub.isEmpty()) Part(a1.lStub, words, a2.rStub)
                    else Part(a1.lStub, words + 1, a2.rStub)
                }
            }
        }
    }
}


fun countWords(str: String): WC {
    if (str.isEmpty()) return wcMonoid.zero
    if (str.length == 1) {
        return if (str.isBlank()) Part("", 0, "")
        else Stub(str)
    }

    val left = countWords(str.substring(0, str.length / 2))
    val right = countWords(str.substring(str.length / 2, str.length))

    return wcMonoid.op(left, right)
}
