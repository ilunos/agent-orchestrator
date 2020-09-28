package com.ilunos.orchestrator.repository

import com.ilunos.orchestrator.domain.Agent
import io.micronaut.data.annotation.Repository
import io.micronaut.data.repository.CrudRepository

@Repository
interface AgentRepository : CrudRepository<Agent, Int> {


}