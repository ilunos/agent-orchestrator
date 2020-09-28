package com.ilunos.orchestrator.service

import com.ilunos.orchestrator.domain.Agent
import com.ilunos.orchestrator.domain.AgentConnectRequest
import com.ilunos.orchestrator.domain.AgentConnectResponse
import com.ilunos.orchestrator.repository.AgentRepository
import javax.inject.Singleton

@Singleton
class AgentProvider(private val repository: AgentRepository) {

    private val agents: MutableList<Agent> = repository.findAll().toMutableList()

    fun create(acr: AgentConnectRequest, ip: String): AgentConnectResponse {
        val agent = repository.save(Agent(
                token = generateRandomToken(),
                name = acr.name,
                hostname = acr.hostname,
                ip = ip,
                port = acr.port,
                connected = true,
                authorized = false,
                enabled = false,
                lastConnected = System.currentTimeMillis()
        ))

        agents.add(agent)
        return AgentConnectResponse.formSuccess(agent.token)
    }

    fun findByToken(token: String?) = agents.firstOrNull { it.token == token }?.id ?: -1
    fun findByNameAndIp(name: String, ip: String) = agents.firstOrNull { it.name == name && it.ip == ip }?.id ?: -1
    fun findByNameAndHostname(name: String, hostname: String) = agents.firstOrNull { it.name == name && it.hostname == hostname }?.id ?: -1

    fun get(id: Long): Agent? = agents.firstOrNull { it.id == id }
    fun getAll(): List<Agent> = agents

    fun authorize(id: Long): Agent? {
        val agent = get(id) ?: return null

        agent.authorized = true
        repository.save(agent)
        return agent
    }

    fun unauthorize(id: Long): Agent? {
        val agent = get(id) ?: return null

        agent.authorized = false
        repository.save(agent)
        return agent
    }

    fun enable(id: Long): Agent? {
        val agent = get(id) ?: return null

        agent.enabled = true
        repository.save(agent)
        return agent
    }

    fun disable(id: Long): Agent? {
        val agent = get(id) ?: return null

        agent.enabled = false
        repository.save(agent)
        return agent
    }

    fun setConnected(id: Long): Agent? {
        val agent = get(id) ?: return null

        agent.connected = true
        agent.lastConnected = System.currentTimeMillis()
        return agent
    }

    fun setDisconnected(id: Long): Agent? {
        val agent = get(id) ?: return null

        agent.connected = false
        return agent
    }

    fun update(id: Long , acr: AgentConnectRequest, ipAddress: String): AgentConnectResponse {
        val agent = get(id) ?: return AgentConnectResponse.fromError("UnknownAgent")

        agent.name =  acr.name
        agent.hostname = acr.hostname
        agent.ip = ipAddress
        agent.port = acr.port
        setConnected(id)

        repository.update(agent)
        return AgentConnectResponse.formSuccess(agent.token)
    }

    fun delete(id: Long): Agent? {
        val agent = get(id) ?: return null

        agents.remove(agent)
        repository.delete(agent)
        return agent.copy()
    }

    private fun generateRandomToken(): String {
        return (1..20).map { passwordChars.random() }.joinToString("")
    }

    companion object {
        val passwordChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + '-' + '_'
    }
}