package com.ilunos.orchestrator

import com.ilunos.common.config.ConfigUtils
import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    ConfigUtils.initialize()

    build()
            .args(*args)
            .packages("com.ilunos.orchestrator")
            .start()
}

