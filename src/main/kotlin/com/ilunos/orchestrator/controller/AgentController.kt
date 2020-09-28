package com.ilunos.orchestrator.controller

import com.ilunos.orchestrator.domain.Agent
import com.ilunos.orchestrator.domain.AgentConnectRequest
import com.ilunos.orchestrator.domain.AgentConnectResponse
import com.ilunos.orchestrator.exception.AgentException
import com.ilunos.orchestrator.exception.AgentNotFoundException
import com.ilunos.orchestrator.service.AgentProvider
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.server.util.HttpClientAddressResolver
import io.micronaut.security.annotation.Secured

@Controller("/agents")
class AgentController(
        private val provider: AgentProvider,
        private val resolver: HttpClientAddressResolver
) {

    @Put
    @Secured("AGENT_ORCHESTRATOR_REGISTER")
    fun connect(@Body acr: AgentConnectRequest, request: HttpRequest<AgentConnectRequest>): AgentConnectResponse {
        val ip = resolver.resolve(request) ?: throw IllegalAccessException("Unable to determinate Request IP!")

        // Provided Token assume agent is existing
        var id = provider.findByToken(acr.token)
        if (id != -1L) {
            return provider.update(id, acr, ip)
        }

        // Agent did not provide token but Name & IP matches, this could be that the agent failed to write the token
        // but block the request anyways due to security reasons
        id = provider.findByNameAndIp(acr.name, ip)
        if (id != -1L) {
            return AgentConnectResponse.fromError("InvalidOrMissingToken")
        }

        // Agent did not provide token but Name & Hostname match, handle like above
        id = provider.findByNameAndHostname(acr.name, acr.hostname)
        if (id != -1L) {
            return AgentConnectResponse.fromError("InvalidOrMissingToken")
        }

        // Still no match, at this point we can assume its a new agent that wants to register
        return provider.create(acr, ip)
    }

    @Get
    @Secured("AGENT_ORCHESTRATOR_VIEW")
    fun list(): List<Agent> {
        return provider.getAll()
    }

    @Get("/{id}")
    @Secured("AGENT_ORCHESTRATOR_VIEW")
    fun get(id: Long): Agent {
        return provider.get(id) ?: throw AgentNotFoundException(id)
    }

    @Delete("{id}")
    @Secured("AGENT_ORCHESTRATOR_EDIT")
    fun delete(id: Long): Agent {
        return provider.delete(id) ?: throw AgentNotFoundException(id)
    }

    @Get("/{id}/enable")
    @Secured("AGENT_ORCHESTRATOR_EDIT")
    fun enable(id: Long): Agent {
        return provider.enable(id) ?: throw AgentNotFoundException(id)
    }

    @Get("/{id}/disable")
    @Secured("AGENT_ORCHESTRATOR_EDIT")
    fun disable(id: Long): Agent {
        return provider.disable(id) ?: throw AgentNotFoundException(id)
    }

    @Get("/{id}/authorize")
    @Secured("AGENT_ORCHESTRATOR_EDIT")
    fun authorize(id: Long): Agent {
        return provider.authorize(id) ?: throw AgentNotFoundException(id)
    }

    @Get("/{id}/unauthorize")
    @Secured("AGENT_ORCHESTRATOR_EDIT")
    fun unauthorize(id: Long): Agent {
        return provider.unauthorize(id) ?: throw AgentNotFoundException(id)
    }

    @Error(AgentException::class, global = true)
    fun agentError(exception: AgentException): HttpResponse<Any> {
        return HttpResponse.notFound(JsonError(exception.message))
    }
}