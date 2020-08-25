package com.ilunos.orchestrator.controller

import com.ilunos.orchestrator.domain.AgentInfo
import com.ilunos.orchestrator.domain.AgentRegisterInfo
import com.ilunos.orchestrator.domain.AgentRegisterResponse
import com.ilunos.orchestrator.domain.AgentStatus
import com.ilunos.orchestrator.exception.AgentException
import com.ilunos.orchestrator.exception.AgentNotFoundException
import com.ilunos.orchestrator.service.AgentProvider
import com.ilunos.orchestrator.service.HeartbeatService
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.hateoas.JsonError

@Controller("/agent")
class AgentController(private val provider: AgentProvider, private val heartbeat: HeartbeatService) {

    @Get
    fun list(): List<AgentInfo> {
        return provider.getAll()
    }

    @Get("/{id}")
    fun get(id: Long): AgentInfo {
        return provider.get(id) ?: throw AgentNotFoundException(id)
    }

    @Put
    fun register(@Body agent: AgentRegisterInfo): AgentRegisterResponse {
        return AgentRegisterResponse(provider.register(agent.toAgentInfo()))
    }

    @Get("/{id}/enable")
    fun enable(id: Long): AgentInfo {
        val agent = provider.get(id) ?: throw AgentNotFoundException(id)

        agent.status = AgentStatus.CONNECTING
        agent.enabled = true
        provider.update(agent)

        heartbeat.checkHeartbeat(agent).subscribe { provider.update(it) }
        return agent
    }

    @Get("/{id}/disable")
    fun disable(id: Long): AgentInfo {
        val agent = provider.get(id) ?: throw AgentNotFoundException(id)

        agent.status = AgentStatus.DISCONNECTED
        agent.enabled = false
        provider.update(agent)

        return agent
    }

    @Get("/{id}/refresh")
    fun refresh(id: Long): AgentInfo {
        val agent = provider.get(id) ?: throw AgentNotFoundException(id)

        agent.status = AgentStatus.CONNECTING
        provider.update(agent)

        heartbeat.checkHeartbeat(agent).subscribe { provider.update(it) }
        return agent
    }

    @Error(AgentException::class, global = true)
    fun agentError(exception: AgentException): HttpResponse<Any> {
        return HttpResponse.notFound(JsonError(exception.message))
    }
}