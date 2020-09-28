package com.ilunos.orchestrator.domain

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Table
@Entity
data class Agent(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long,

        @JsonIgnore
        var token: String?,

        var name: String,
        var hostname: String,
        var ip: String,
        var port: Int,

        @Transient
        var connected: Boolean,

        var authorized: Boolean,
        var enabled: Boolean,
        var lastConnected: Long
) {

    constructor(
            token: String,
            name: String,
            hostname: String,
            ip: String,
            port: Int,
            connected: Boolean,
            authorized: Boolean,
            enabled: Boolean,
            lastConnected: Long
    ) : this(
            -1,
            token,
            name,
            hostname,
            ip,
            port,
            connected,
            authorized,
            enabled,
            lastConnected
    )

    constructor() : this(
            -1L,
            null,
            "INVALID",
            "INVALID-HOST",
            "0.0.0.0",
            -1,
            false,
            false,
            false,
            -1
    )

    @Transient
    @JsonIgnore
    val active = connected && authorized && enabled


    override fun toString(): String {
        return "ID: $id ($name)"
    }
}