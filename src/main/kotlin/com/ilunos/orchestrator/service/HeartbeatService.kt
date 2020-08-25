package com.ilunos.orchestrator.service

import com.ilunos.orchestrator.domain.AgentInfo
import com.ilunos.orchestrator.domain.AgentStatus
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.netty.DefaultHttpClient
import io.micronaut.scheduling.annotation.Async
import io.micronaut.scheduling.annotation.Scheduled
import io.reactivex.Flowable
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
open class HeartbeatService(private val provider: AgentProvider) {

    private val logger = LoggerFactory.getLogger(HeartbeatService::class.java)

    @Async
    @Scheduled(fixedRate = "1m", initialDelay = "2s")
    open fun checkHeartbeats() {
        logger.debug("Starting Heartbeat run for all enabled Agents")

        val agents = provider.getAllEnabled()
        logger.debug("Received ${agents.size} enabled agent/s")

        for (agent in agents) {
            checkHeartbeat(agent).subscribe {
                provider.update(it)
            }
        }
    }

    fun checkHeartbeat(agent: AgentInfo): Flowable<AgentInfo> {
        logger.debug("Checking Heartbeat for $agent")

        val httpClient = DefaultHttpClient(agent.url)

        return httpClient.exchange("/")
                .onErrorReturn {
                    agent.status = AgentStatus.UNREACHABLE
                    HttpResponse.notFound()
                }.doOnNext {
                    if (it.status == HttpStatus.OK) {
                        agent.status = AgentStatus.CONNECTED
                        agent.lastCommunication = System.currentTimeMillis()
                    } else {
                        agent.status = AgentStatus.UNREACHABLE
                    }

                    logger.debug("Heartbeat check completed for agent $agent")
                }.doFinally { httpClient.close() }
                .map { agent }
    }

}