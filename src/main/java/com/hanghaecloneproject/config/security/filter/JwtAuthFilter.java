package com.hanghaecloneproject.config.security.filter;

import com.hanghaecloneproject.common.error.CommonResponse;
import com.hanghaecloneproject.common.error.ErrorCode;
import com.hanghaecloneproject.common.error.ErrorResponseUtils;
import com.hanghaecloneproject.config.security.dto.UserDetailsImpl;
import com.hanghaecloneproject.config.security.jwt.JwtUtils;
import com.hanghaecloneproject.config.security.jwt.VerifyResult;
import com.hanghaecloneproject.config.security.jwt.VerifyResult.TokenStatus;
import com.hanghaecloneproject.user.service.UserService;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public JwtAuthFilter(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
          FilterChain chain) throws IOException, ServletException {

        String accessToken = null;
        try {
            accessToken = extractTokenFromHeader(request, HttpHeaders.AUTHORIZATION);
        } catch (IllegalArgumentException e) {
            chain.doFilter(request, response);
            return;
        }

        VerifyResult verifyResult = jwtUtils.verifyToken(accessToken);

        if (verifyResult.getTokenStatus() == TokenStatus.ACCESS) {
//            ValueOperations<String, Object> operations = redisTemplate.opsForValue();
//            if (operations.get(accessToken) != null && (boolean) operations.get(accessToken)) {
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
//                      "?????? ???????????? ???????????????. ?????? ????????? ??? ?????????");
//                return;
//            }

            SecurityContextHolder.getContext()
                  .setAuthentication(createSecurityTokenByUsername(verifyResult.getUsername()));
            chain.doFilter(request, response);
            return;
        }

        if (verifyResult.getTokenStatus() == TokenStatus.EXPIRED) {
            String refreshToken = null;
            try {
                refreshToken = extractTokenFromHeader(request, "refresh_token");
            } catch (IllegalArgumentException e) {
                return;
            }
            // ????????? ????????? ????????? ?????? ???????????? refresh_token ??? ????????? ???.
            VerifyResult refreshTokenVerifyResult = jwtUtils.verifyToken(refreshToken);
            if (refreshTokenVerifyResult.getTokenStatus() == TokenStatus.ACCESS) {
                SecurityContextHolder.getContext().setAuthentication(
                      reIssueAccessToken(response, refreshTokenVerifyResult.getUsername()));
            } else {
                ErrorResponseUtils.sendError(response, new CommonResponse(ErrorCode.EXPIRED_TOKEN,
                            "?????? ????????? ?????????????????????. ?????? ?????????????????????."));
                return;
            }
        } else {
            ErrorResponseUtils.sendError(response, new CommonResponse(ErrorCode.INVALID_TOKEN,
                        "???????????? ?????? ???????????????."));
            return;
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken createSecurityTokenByUsername(String username) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());
    }

    private UsernamePasswordAuthenticationToken reIssueAccessToken(HttpServletResponse response,
          String username) {
        UserDetailsImpl userDetails = (UserDetailsImpl) userService.loadUserByUsername(username);
        String reIssueAccessToken = jwtUtils.issueAccessToken(userDetails.getUsername());
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + reIssueAccessToken);

        return new UsernamePasswordAuthenticationToken(
              userDetails,
              null,
              userDetails.getAuthorities());
    }

    private String extractTokenFromHeader(HttpServletRequest request, String tokenType)
          throws IllegalArgumentException {
        String headerValue = request.getHeader(tokenType);
        if (headerValue == null || !headerValue.startsWith("Bearer ")) {
            throw new IllegalArgumentException("????????? ?????? ?????????.");
        }
        return headerValue.substring("Bearer ".length());
    }
}
