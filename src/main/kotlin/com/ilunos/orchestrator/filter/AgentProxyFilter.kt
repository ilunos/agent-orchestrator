package com.ilunos.orchestrator.filter

import com.ilunos.orchestrator.domain.AgentStatus
import com.ilunos.orchestrator.exception.*
import com.ilunos.orchestrator.service.AgentProvider
import io.micronaut.core.async.publisher.Publishers.map
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Filter
import io.micronaut.http.client.ProxyHttpClient
import io.micronaut.http.filter.OncePerRequestHttpServerFilter
import io.micronaut.http.filter.ServerFilterChain
import io.micronaut.http.hateoas.JsonError
import io.micronaut.http.uri.UriBuilder
import io.reactivex.Flowable
import org.reactivestreams.Publisher
import java.util.stream.Collectors

@Filter("/agents/**/api/**", "/agents/**/api/")
class AgentProxyFilter(
        private val client: ProxyHttpClient,
        private val provider: AgentProvider
) : OncePerRequestHttpServerFilter() {

    override fun doFilterOnce(request: HttpRequest<*>, chain: ServerFilterChain): Publisher<MutableHttpResponse<*>> {
        val groups = regex.find(request.uri.path)?.groups ?: throw IllegalAgentId(request.uri.path.removePrefix("/agents/").substringBefore("/"))
        if (groups.size != 3) throw IllegalAgentId(groups.stream().map { it?.value }.collect(Collectors.joining()))

        val agent = provider.get(groups[1]!!.value.toLong()) ?: throw AgentNotFoundException(groups[1]!!.value.toLong())

        if (!agent.enabled) throw AgentNotEnabledException(agent.id)
        if (agent.status != AgentStatus.CONNECTED) throw AgentNotConnectedException(agent.id)

        return map((client.proxy(
                request.mutate()
                        .uri { b: UriBuilder ->
                            b.apply {
                                scheme(agent.url.protocol)
                                host(agent.url.host)
                                port(agent.url.port)
                                replacePath(groups[2]?.value ?: "")
                            }
                        }
        ) as Flowable<MutableHttpResponse<*>>).onErrorReturn {
            provider.setUnreachable(agent)
            HttpResponse.notFound(JsonError(AgentUnreachable(agent.id, it).message))
        }, { response: MutableHttpResponse<*> -> response })
    }

    companion object {
        private val regex = Regex("/agents/([0-9]+)/api(/.+|/|)")
    }
}