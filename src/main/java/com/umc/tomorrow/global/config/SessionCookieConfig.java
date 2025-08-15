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

    // SameSite 설정
    @Bean
    public TomcatServletWebServerFactory cookieSameSiteCustomizer() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
                cookieProcessor.setSameSiteCookies("None"); // 운영·로컬 모두 None
                context.setCookieProcessor(cookieProcessor);
            }
        };
    }

    // Secure 플래그 설정
    @Bean
    public ServletContextInitializer secureFlagCustomizer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                String activeProfile = System.getProperty("spring.profiles.active", "local");

                if (!"local".equalsIgnoreCase(activeProfile)) {
                    servletContext.getSessionCookieConfig().setSecure(true);
                } else {
                    servletContext.getSessionCookieConfig().setSecure(false);
                }
            }
        };
    }
}
