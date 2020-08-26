package com.ilunos.orchestrator

import com.ilunos.orchestrator.utils.FileUtils
import io.micronaut.runtime.Micronaut.*

fun main(args: Array<String>) {
    if (!FileUtils.exists("config/application.yml")) {
        FileUtils.copyTemplate("templates/application.yml", "config/application.yml")
    }

    build()
            .args(*args)
            .packages("com.ilunos.orchestrator")
            .start()
}

