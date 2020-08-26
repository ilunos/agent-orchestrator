package com.ilunos.orchestrator.security

import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpRequest
import io.micronaut.security.authentication.*
import io.reactivex.Maybe
import io.reactivex.MaybeEmitter
import org.reactivestreams.Publisher
import javax.inject.Singleton

@Singleton
class RegisterAuthenticationProvider : AuthenticationProvider {

    @Value("\${micronaut.register-token:}")
    private var registerSecret: String? = null

    override fun authenticate(httpRequest: HttpRequest<*>?, authenticationRequest: AuthenticationRequest<*, *>): Publisher<AuthenticationResponse> {
        return Maybe.create { emitter: MaybeEmitter<AuthenticationResponse> ->

           if (authenticationRequest.identity == "agent") {
                val secret = registerSecret
                if (!secret.isNullOrBlank()) {
                    if (authenticationRequest.secret == secret) {
                        emitter.onSuccess(getUserDetails())
                    } else {
                        emitter.onError(AuthenticationException(AuthenticationFailed()))
                    }
                } else {
                    emitter.onSuccess(getUserDetails())
                }
            } else {
                emitter.onError(AuthenticationException(AuthenticationFailed()))
            }

        }.toFlowable()
    }

    private fun getUserDetails(): UserDetails = UserDetails("agent", listOf("AGENT_ORCHESTRATOR_REGISTER"))

}