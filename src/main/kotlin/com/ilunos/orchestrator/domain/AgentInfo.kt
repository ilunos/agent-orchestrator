@file:Suppress("JpaDataSourceORMInspection")

package com.ilunos.orchestrator.domain

import java.net.InetAddress
import java.net.URL
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "agent")
data class AgentInfo(

        @Id
        @GeneratedValue
        var id: Long,
        var name: String,
        var url: URL,
        var enabled: Boolean = false,
        var status: AgentStatus = AgentStatus.UNKNOWN,
        var lastCommunication: Long = -1

) {
    constructor(name: String, url: URL) : this(-1, name, url)

    constructor() : this(-1,
            InetAddress.getLocalHost().hostName,
            URL("http://localhost:8080")
    )
}