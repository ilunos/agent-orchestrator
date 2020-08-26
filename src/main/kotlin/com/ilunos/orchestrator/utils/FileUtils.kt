package com.ilunos.orchestrator.utils

import java.lang.IllegalStateException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

object FileUtils {

    fun exists(path: String): Boolean {
        return try {
            exists(Paths.get(path))
        } catch (e: InvalidPathException) {
            false
        }
    }

    private fun exists(path: Path): Boolean = Files.exists(path)

    fun copyTemplate(resourcePath: String, targetPath: String) {
        val stream = {}.javaClass.classLoader.getResourceAsStream(resourcePath)
                ?: throw IllegalStateException("Unable to find '$resourcePath'!")

        Files.copy(stream, Paths.get(targetPath))
    }
}