package com.ilunos.orchestrator.repository

import com.ilunos.orchestrator.domain.AgentInfo
import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AgentRepository : CrudRepository<AgentInfo, Long> {

    fun findByName(name: String): AgentInfo

    fun existsByName(name: String): Boolean

    @Query("SELECT * FROM agent WHERE enabled = true", nativeQuery = true)
    fun findAllEnabled(): List<AgentInfo>
}