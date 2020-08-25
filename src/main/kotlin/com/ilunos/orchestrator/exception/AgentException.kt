package com.ilunos.orchestrator.exception

open class AgentException : Exception {
    constructor(message: String, throwable: Throwable) : super(message, throwable)
    constructor(message: String) : super(message)
}

class IllegalAgentId(id: String) : AgentException("Agent $id is not a valid Agent-Id!")
class AgentNotFoundException(id: Long) : AgentException("Agent $id is not registered to this system!")
class AgentNotEnabledException(id: Long) : AgentException("Agent $id is not Enabled!")
class AgentNotConnectedException(id: Long) : AgentException("Agent $id is not Connected!")
class AgentUnreachable(id: Long, throwable: Throwable) : AgentException("Unable to connect to Agent $id!", throwable)
