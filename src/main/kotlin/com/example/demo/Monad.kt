package com.example.demo

import arrow.Kind
import java.util.Optional

interface Monad<F>: Functor<F> {
    fun <A> unit(a: () -> A): Kind<F, A>
    fun <A, B> Kind<F, A>.flatMap(f: (A) -> Kind<F, B>): Kind<F, B>
    override fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B> = this.flatMap { unit { f(it) } }
    fun <A, B, C> compose(f: (A) -> Kind<F, B>, g: (B) -> Kind<F, C>): (A) -> Kind<F, C> = { f(it).flatMap(g) }
}

object ListMonad : Monad<ForListK> {
    override fun <A> unit(a: () -> A): Kind<ForListK, A> = ListK(listOf(a()))

    override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): Kind<ForListK, B> = this.fix().flatMap { f(it).fix() }
}

object OptionMonad : Monad<ForOptionK> {
    override fun <A> unit(a: () -> A): Kind<ForOptionK, A> = OptionK(Optional.of(a()))

    override fun <A, B> Kind<ForOptionK, A>.flatMap(f: (A) -> Kind<ForOptionK, B>): Kind<ForOptionK, B> = OptionK(this.fix().option.flatMap { f(it).fix().option })
}

object IdMonad : Monad<ForIdK> {
    override fun <A> unit(a: () -> A): Kind<ForIdK, A> = IdK(a())

    override fun <A, B> Kind<ForIdK, A>.flatMap(f: (A) -> Kind<ForIdK, B>): Kind<ForIdK, B> = this.fix().flatMap { f(it).fix() }
}