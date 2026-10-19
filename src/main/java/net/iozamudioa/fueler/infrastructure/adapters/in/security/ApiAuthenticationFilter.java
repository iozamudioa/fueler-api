
package net.iozamudioa.fueler.infrastructure.adapters.in.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import net.iozamudioa.fueler.application.ports.out.DeviceOutputPort;
import net.iozamudioa.fueler.application.ports.out.TokenOutputPort;
import net.iozamudioa.fueler.domain.Device;
import net.iozamudioa.fueler.infrastructure.adapters.in.security.model.DevicePrincipal;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiAuthenticationFilter extends OncePerRequestFilter {

    private final DeviceOutputPort deviceOutputPort;
    private final TokenOutputPort tokenOutputPort;
    private final String adminKey;

    public ApiAuthenticationFilter(
            DeviceOutputPort deviceOutputPort,
            TokenOutputPort tokenOutputPort,
            @Value("${fueler.admin-key:}") String adminKey) {

        this.deviceOutputPort = deviceOutputPort;
        this.tokenOutputPort = tokenOutputPort;
        this.adminKey = adminKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest req,
            HttpServletResponse res,
            FilterChain chain) throws ServletException, IOException {

        String suppliedAdmin = req.getHeader("X-Admin-Key");

        if (req.getServletPath().startsWith("/api/v1/admin/")
                && !adminKey.isBlank()
                && suppliedAdmin != null
                && MessageDigest.isEqual(
                        adminKey.getBytes(StandardCharsets.UTF_8),
                        suppliedAdmin.getBytes(StandardCharsets.UTF_8))) {

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            "admin",
                            null,
                            List.of(
                                    new SimpleGrantedAuthority("ROLE_ADMIN"))));

        } else {

            String header = req.getHeader("Authorization");

            if (header != null
                    && header.startsWith("Bearer ")
                    && header.length() > 7) {

                String hash = tokenOutputPort.hash(
                        header.substring(7));

                deviceOutputPort.findByTokenHash(hash)
                        .filter(device -> device.status() == Device.Status.ACTIVE)
                        .ifPresent(device -> SecurityContextHolder.getContext()
                                .setAuthentication(
                                        new UsernamePasswordAuthenticationToken(
                                                new DevicePrincipal(
                                                        device.id(),
                                                        device.clientId()),
                                                null,
                                                List.of(
                                                        new SimpleGrantedAuthority(
                                                                "ROLE_DEVICE")))));
            }
        }

        chain.doFilter(req, res);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getServletPath();

        return path.equals("/swagger-ui.html")
                || path.startsWith("/swagger-ui/")
                || path.equals("/v3/api-docs")
                || path.startsWith("/v3/api-docs/")
                || path.equals("/v3/api-docs.yaml")
                || path.equals("/actuator/health")
                || (request.getMethod().equals("POST")
                        && path.equals("/api/v1/devices"));
    }
}
