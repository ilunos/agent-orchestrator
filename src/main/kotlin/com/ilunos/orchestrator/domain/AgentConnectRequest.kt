package com.ilunos.orchestrator.domain

data class AgentConnectRequest(
        val name: String,
        val token: String?,
        val hostname: String,
        val port: Int
) {
}