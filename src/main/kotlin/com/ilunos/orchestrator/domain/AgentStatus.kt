package com.ilunos.orchestrator.domain

enum class AgentStatus {
    UNKNOWN,
    CREATED,
    CONNECTING,
    CONNECTED,
    UNREACHABLE,
    ALREADY_EXISTING,
    NOT_EXISTING,
    DISCONNECTED,
    DELETED
}