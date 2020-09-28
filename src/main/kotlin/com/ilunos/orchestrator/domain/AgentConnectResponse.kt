package com.ilunos.orchestrator.domain

data class AgentConnectResponse(
        val token: String?,
        val connected: Boolean,
        val errorMessage: String?
) {

    companion object {
        fun fromError(message: String): AgentConnectResponse = AgentConnectResponse(null, false, message)
        fun formSuccess(token: String?): AgentConnectResponse = AgentConnectResponse(token, true, null)
    }
}