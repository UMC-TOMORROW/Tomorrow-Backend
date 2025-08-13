package com.umc.tomorrow.domain.email.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 이메일 템플릿 유틸리티 클래스
 * HTML 템플릿을 읽고 동적 내용을 삽입하는 기능 제공
 * 
 * 작성자: 정여진
 * 작성일: 2025-01-27
 */
@Slf4j
@Component
public class EmailTemplateUtil {

    private static final String TEMPLATE_PATH = "static/email-templates/";
    
    /**
     * 템플릿 파일을 읽어서 동적 내용을 삽입
     * 
     * @param templateName 템플릿 파일명 (예: "application-result.html")
     * @param variables 동적 데이터 맵
     * @return 완성된 HTML 내용
     */
    public String processTemplate(String templateName, Map<String, String> variables) {
        try {
            // 템플릿 파일 읽기
            String template = readTemplateFile(templateName);
            
            // 동적 데이터 삽입
            return replaceVariables(template, variables);
            
        } catch (IOException e) {
            log.error("템플릿 처리 실패: {}", templateName, e);
            throw new RuntimeException("템플릿 처리 실패: " + templateName, e);
        }
    }
    
    /**
     * 템플릿 파일 읽기
     */
    private String readTemplateFile(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH + templateName);
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
    
    /**
     * 템플릿의 플레이스홀더를 실제 데이터로 교체
     */
    private String replaceVariables(String template, Map<String, String> variables) {
        String result = template;
        
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }
}
