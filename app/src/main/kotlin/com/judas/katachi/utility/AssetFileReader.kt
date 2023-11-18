package com.judas.katachi.utility

import android.content.res.AssetManager

fun String.readFileFrom(assets: AssetManager): String =
    assets.open(this).bufferedReader().use { it.readText() }
