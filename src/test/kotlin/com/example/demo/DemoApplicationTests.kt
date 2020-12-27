package com.example.demo

import io.kotest.core.spec.style.StringSpec
import io.kotest.property.forAll

class PropertyExample : StringSpec({
    "String size" {
        forAll<String, String> { a, b ->
            (a + b).length == a.length + b.length
        }

        forAll<String, String> { a, b ->
            a.plus(b) == a + b
        }
    }

    "Sort" {
        forAll<List<Int>> { ints ->
            val sorted = ints.sorted()
            sorted[0] <= sorted[sorted.size - 1]
        }
    }
})
