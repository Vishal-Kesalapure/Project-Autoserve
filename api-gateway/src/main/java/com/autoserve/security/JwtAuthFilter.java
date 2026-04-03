package com.autoserve.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

	@Autowired
	private JwtService jwtService;

	// Fully public — no token required at all
	private static final List<String> PUBLIC_PATHS = List.of(
		"/api/auth/register",
		"/api/auth/login"
	);

	// Protected garage paths — token required even for GET
	// /api/garages/my       -> SUPPLIER only
	// /api/garages/all      -> ADMIN only
	// /api/garages/admin/** -> ADMIN only
	// /api/garages/internal/** -> internal Feign calls
	private static final List<String> PROTECTED_GARAGE_SEGMENTS = List.of(
		"/api/garages/my",
		"/api/garages/all",
		"/api/garages/admin",
		"/api/garages/internal"
	);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();
		String path = request.getURI().getPath();
		String method = request.getMethod().name();

		// 1. Fully public endpoints — skip token entirely
		if (PUBLIC_PATHS.contains(path)) {
			return chain.filter(exchange);
		}

		// 2. Public GET garage endpoints — no token needed to browse
		if ("GET".equals(method) && isPublicGaragePath(path)) {
			return chain.filter(exchange);
		}

		// 3. All other requests — require valid JWT
		String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return onError(exchange, HttpStatus.UNAUTHORIZED);
		}

		String token = authHeader.substring(7);
		if (!jwtService.isTokenValid(token)) {
			return onError(exchange, HttpStatus.UNAUTHORIZED);
		}

		// 4. Extract claims and inject headers for downstream services
		Claims claims = jwtService.extractAllClaims(token);
		String username = claims.getSubject();
		Long userId   = claims.get("userId", Long.class);
		String role   = claims.get("role", String.class);

		ServerHttpRequest mutated = exchange.getRequest().mutate()
				.header("X-User-Id",   userId   != null ? userId.toString() : "")
				.header("X-Username",  username != null ? username          : "")
				.header("X-User-Role", role     != null ? role              : "")
				.build();

		return chain.filter(exchange.mutate().request(mutated).build());
	}

	/**
	 * Returns true only for truly public GET /api/garages/** paths.
	 * Protected segments are excluded even if the method is GET.
	 */
	private boolean isPublicGaragePath(String path) {
		if (!path.startsWith("/api/garages")) {
			return false;
		}
		// Exact: /api/garages — list all active garages
		if (path.equals("/api/garages")) {
			return true;
		}
		// Block any protected segment
		for (String seg : PROTECTED_GARAGE_SEGMENTS) {
			if (path.equals(seg) || path.startsWith(seg + "/")) {
				return false;
			}
		}
		// Remaining: /api/garages/{id}, /api/garages/search, /api/garages/specialization/{spec}
		return path.startsWith("/api/garages/");
	}

	private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(status);
		return response.setComplete();
	}

	@Override
	public int getOrder() {
		return -1;
	}
}
