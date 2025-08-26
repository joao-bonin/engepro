package br.com.engepro.api.infra.security

import br.com.engepro.api.model.User
import br.com.engepro.api.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.servlet.HandlerExceptionResolver

@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    final JwtService jwtService
    final UserRepository userRepository
    final HandlerExceptionResolver handlerExceptionResolver

    JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository,
                            HandlerExceptionResolver handlerExceptionResolver) {
        this.jwtService = jwtService
        this.userRepository = userRepository
        this.handlerExceptionResolver = handlerExceptionResolver
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            String jwt = authHeader.substring(7)
            String email = jwtService.extractUsername(jwt)

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findByEmail(email)
                        .orElseThrow { new RuntimeException("Usuário não encontrado") }

                if (jwtService.isTokenValid(jwt, user)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(user, null, List.of())

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request))
                    SecurityContextHolder.getContext().setAuthentication(authToken)
                }
            }

            filterChain.doFilter(request, response)
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acesso negado: ${e.message}")
        }
    }
}