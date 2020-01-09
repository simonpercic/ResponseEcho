package com.github.simonpercic.responseecho

import com.github.simonpercic.oklog.shared.data.HeaderData

fun List<HeaderData>.getMap() : Map<String,String> = this.associate { Pair(it.name,it.value) }