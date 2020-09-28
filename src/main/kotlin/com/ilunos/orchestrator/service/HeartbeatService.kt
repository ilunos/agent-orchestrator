package com.ilunos.orchestrator.service

import com.ilunos.orchestrator.domain.Agent
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.netty.DefaultHttpClient
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.Flowable
import org.slf4j.LoggerFactory
import java.net.URL
import javax.inject.Singleton

@Singleton
open class HeartbeatService(private val provider: AgentProvider) {

    private val logger = LoggerFactory.getLogger(HeartbeatService::class.java)

    @Async
    @Scheduled(fixedRate = "1m", initialDelay = "2s")
    open fun checkHeartbeats() {
        val agents = provider.getAll()
        if (agents.isEmpty()) {
            logger.trace("No Agents registered skipping heartbeat check")
            return
        }

        logger.debug("Starting Heartbeat check for ${agents.size} agent/s")
        Flowable.fromIterable(agents)
                .flatMap { checkHeartbeat(it) }
                .collect({ mutableListOf<Agent>() }, { t1, t2 -> if (t2.connected) t1.add(t2) })
                .subscribe { t1, t2 ->
                    logger.debug("Completed Heartbeat check. Result: ${t1.size}/${agents.size} agent/s available.")
                }
    }

    fun checkHeartbeat(agent: Agent): Flowable<Agent> {
        logger.trace("Starting Heartbeat check for $agent")

        val httpClient = DefaultHttpClient(URL("http://${agent.ip}:${agent.port}")) // TODO: Investigate Warning

        return httpClient.exchange("/heartbeat").map {
            if (it.status == HttpStatus.OK)
                provider.setConnected(agent.id)

            agent
        }.onErrorReturn {
            provider.setDisconnected(agent.id)
            agent
        }.doFinally {
            logger.trace("Completed Heartbeat check for $agent. Connected: ${agent.connected}")
            httpClient.close()
        }
    }

}
