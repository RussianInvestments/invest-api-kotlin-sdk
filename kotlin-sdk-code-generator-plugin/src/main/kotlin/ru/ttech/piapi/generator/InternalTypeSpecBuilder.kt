package ru.ttech.piapi.generator

import com.squareup.kotlinpoet.TypeSpec

interface InternalTypeSpecGenerator {
    fun generate(): TypeSpec
}