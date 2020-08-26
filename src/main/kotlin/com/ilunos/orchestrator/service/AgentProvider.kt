package com.ilunos.orchestrator.service

import com.ilunos.orchestrator.domain.AgentInfo
import com.ilunos.orchestrator.domain.AgentStatus
import com.ilunos.orchestrator.repository.AgentRepository
import javax.inject.Singleton

@Singleton
class AgentProvider(private val agentRepository: AgentRepository) {

    fun register(agentInfo: AgentInfo): AgentStatus {
        if (agentRepository.existsById(agentInfo.id)) return AgentStatus.ALREADY_EXISTING
        if (agentRepository.existsByName(agentInfo.name)) return AgentStatus.ALREADY_EXISTING

        agentInfo.status = AgentStatus.CREATED
        agentRepository.save(agentInfo)

        return AgentStatus.CREATED
    }

    fun getAll(): List<AgentInfo> {
        return agentRepository.findAll().toList()
    }

    fun getAllEnabled(): List<AgentInfo> {
        return agentRepository.findAllEnabled()
    }

    fun get(id: Long): AgentInfo? {
        return agentRepository.findById(id).orElse(null)
    }

    fun update(agentInfo: AgentInfo) {
        agentRepository.update(agentInfo)
    }

    fun delete(id: Long): AgentStatus {
        val agent = get(id) ?: return AgentStatus.NOT_EXISTING

        agentRepository.delete(agent)
        return AgentStatus.DELETED
    }

    fun setUnreachable(agentInfo: AgentInfo) {
        agentInfo.status = AgentStatus.UNREACHABLE
        update(agentInfo)
    }

    fun setReachable(agentInfo: AgentInfo) {
        agentInfo.status = AgentStatus.CONNECTED
        update(agentInfo)
    }
}