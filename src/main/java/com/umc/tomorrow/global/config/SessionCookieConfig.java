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
     * SameSite 설정 - 무조건 None
     */
    @Bean
    public TomcatServletWebServerFactory cookieSameSiteCustomizer() {
        return new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(org.apache.catalina.Context context) {
                Rfc6265CookieProcessor cookieProcessor = new Rfc6265CookieProcessor();
                cookieProcessor.setSameSiteCookies("None"); // 항상 None
                context.setCookieProcessor(cookieProcessor);
            }
        };
    }

    /**
     * Secure 플래그 설정 - 무조건 true
     */
    @Bean
    public ServletContextInitializer secureFlagCustomizer() {
        return new ServletContextInitializer() {
            @Override
            public void onStartup(ServletContext servletContext) throws ServletException {
                servletContext.getSessionCookieConfig().setSecure(true); // 항상 Secure
            }
        };
    }
}
