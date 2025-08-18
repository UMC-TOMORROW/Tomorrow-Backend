package com.umc.tomorrow.global.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionCookieConfig {

    /**
     * SameSite 설정
     * - local: Lax (브라우저에서 Secure=false 허용)
     * - dev/prod: None (cross-site 요청 허용, Secure=true와 세트)
     */
    @Bean
    public TomcatServletWebServerFactory cookieSameSiteCustomizer() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                String activeProfile = System.getProperty("spring.profiles.active", "local");
                Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();

                if ("local".equalsIgnoreCase(activeProfile)) {
                    // 로컬에서는 HTTPS가 아니므로 Lax로 (Secure=false 조합 허용)
                    cookieProcessor.setSameSiteCookies("Lax");
                } else {
                    // 운영/개발 서버는 HTTPS이므로 None + Secure
                    cookieProcessor.setSameSiteCookies("None");
                }

                context.setCookieProcessor(cookieProcessor);
            }
        };
    }

    /**
     * Secure 플래그 설정
     * - local: false
     * - dev/prod: true
     */
    @Bean
    public ServletContextInitializer secureFlagCustomizer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                String activeProfile = System.getProperty("spring.profiles.active", "local");

                if ("local".equalsIgnoreCase(activeProfile)) {
                    servletContext.getSessionCookieConfig().setSecure(false);
                } else {
                    servletContext.getSessionCookieConfig().setSecure(true);
                }
            }
        };
    }
}
