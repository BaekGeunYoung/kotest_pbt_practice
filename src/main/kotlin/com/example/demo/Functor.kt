package com.example.demo

import arrow.Kind
import arrow.higherkind
import java.util.*

interface Functor <F> {
    fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>
}

class ForListK private constructor() { companion object }

@Suppress("UNCHECKED_CAST")
fun <A> Kind<ForListK, A>.fix(): ListK<A> = this as ListK<A>

@higherkind
data class ListK<T>(val list: List<T>) : Kind<ForListK, T>, List<T> by list {
    fun <B> map(f: (T) -> B): ListK<B> = ListK(this.list.map(f))
    fun <B> flatMap(f: (T) -> ListK<B>): ListK<B> = ListK(this.list.flatMap(f))
}

object ListFunctor : Functor<ForListK> {
    override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): Kind<ForListK, B> = fix().map(f)
}

class ForOptionK private constructor() { companion object }

fun <A> Kind<ForOptionK, A>.fix(): OptionK<A> = this as OptionK<A>

@higherkind
data class OptionK<T>(val option: Optional<T>) : Kind<ForOptionK, T> {
    fun <B> map(f: (T) -> B): OptionK<B> = OptionK(this.option.map(f))
    fun <B> flatMap(f: (T) -> OptionK<B>): OptionK<B> = OptionK(this.option.flatMap { f(it).option })
}

object OptionFunctor : Functor<ForOptionK> {
    override fun <A, B> Kind<ForOptionK, A>.map(f: (A) -> B): Kind<ForOptionK, B> = fix().map(f)
}

class ForIdK private constructor() { companion object }

fun <A> Kind<ForIdK, A>.fix(): IdK<A> = this as IdK<A>

@higherkind
data class IdK<T>(val value: T) : Kind<ForIdK, T> {
    fun <B> flatMap(f: (T) -> IdK<B>): IdK<B> = f(value)
    fun <B> map(f: (T) -> B): IdK<B> = IdK(f(value))
}

object IdFunctor : Functor<ForIdK> {
    override fun <A, B> Kind<ForIdK, A>.map(f: (A) -> B): Kind<ForIdK, B> = fix().map(f)
}