package com.ilunos.orchestrator.domain

import java.net.URL

data class AgentRegisterInfo(val name: String, val url: URL) {

    fun toAgentInfo(): AgentInfo {
        return AgentInfo(name, url)
    }
}