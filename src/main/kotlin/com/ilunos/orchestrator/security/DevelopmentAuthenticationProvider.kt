package com.ilunos.orchestrator.security

import io.micronaut.context.annotation.Requires
import io.micronaut.core.util.StringUtils
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
@Requires(property = "micronaut.security.basic-auth.development", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
class DevelopmentAuthenticationProvider : AuthenticationProvider {

    override fun authenticate(httpRequest: HttpRequest<*>?, authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        return Maybe.create { emitter: MaybeEmitter<AuthenticationResponse> ->
            if (authenticationRequest.identity == "admin" && authenticationRequest.secret == "admin") {
                emitter.onSuccess(UserDetails("admin", listOf("AGENT_ORCHESTRATOR_EDIT", "AGENT_ORCHESTRATOR_VIEW")))
            } else if (authenticationRequest.identity == "user" && authenticationRequest.secret == "user") {
                emitter.onSuccess(UserDetails("user", listOf("AGENT_ORCHESTRATOR_VIEW")))
            } else {
                emitter.onError(AuthenticationException(AuthenticationFailed()))
            }
        }.toFlowable()
    }
}