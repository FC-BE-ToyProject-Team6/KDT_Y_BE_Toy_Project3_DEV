package com.fastcampus.toyproject.config.security.filter;

import com.fastcampus.toyproject.config.security.jwt.TokenProvider;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenProvider tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String jwt = resolveToken(request);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }

    private String resolveToken(HttpServletRequest request) {

        if (request.getCookies() == null) {
            return null;
        }

        Cookie accessToken = Arrays.stream(request.getCookies())
            .filter(cookie -> cookie.getName().equals("access_token"))
            .findFirst()
            .orElse(null);

        if (accessToken == null) {
            return null;
        }

        return accessToken.getValue();

//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)){
//            return bearerToken.split(" ")[1].trim();
//        }
//
//        return null;
    }
}
