package com.example.medicalrep.ui.utils

/** The set without [item] when it is in it, otherwise with it. */
fun <T> Set<T>.toggle(item: T): Set<T> = if (item in this) this - item else this + item
