package com.ilunos.orchestrator.filter

import com.ilunos.orchestrator.exception.AgentNotEnabledException
import com.ilunos.orchestrator.exception.AgentNotFoundException
import com.ilunos.orchestrator.exception.IllegalAgentId
import com.ilunos.orchestrator.service.AgentProvider
import io.micronaut.http.HttpRequest
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.client.RxProxyHttpClient
import io.micronaut.http.filter.OncePerRequestHttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.micronaut.http.uri.UriBuilder
import org.reactivestreams.Publisher
import java.util.stream.Collectors

@Filter("/agents/**/api/**", "/agents/**/api/")
class AgentProxyFilter(
        private val client: RxProxyHttpClient,
        private val provider: AgentProvider
) : OncePerRequestHttpServerFilter() {

    override fun doFilterOnce(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        val groups = regex.find(request.uri.path)?.groups ?: throw IllegalAgentId(request.uri.path.removePrefix("/agents/").substringBefore("/"))
        if (groups.size != 3) throw IllegalAgentId(groups.stream().map { it?.value }.collect(Collectors.joining()))

        val agent = provider.get(groups[1]!!.value.toLong()) ?: throw AgentNotFoundException(groups[1]!!.value.toLong())

        if (!agent.active) throw AgentNotEnabledException(agent.id) //TODO: Replace with AgentNotActiveException

        return client.proxy(request.mutate()
                .uri { b: UriBuilder ->
                    b.apply {
                        //scheme(agent.url.protocol) // TODO: Need to pass this into Agent
                        host(agent.ip)
                        port(agent.port)
                        replacePath(groups[2]?.value ?: "")
                    }
                }).doOnError { provider.setDisconnected(agent.id) }
    }

    companion object {
        private val regex = Regex("/agents/([0-9]+)/api(/.+|/|)")
    }
}