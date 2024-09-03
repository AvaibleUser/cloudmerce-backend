package com.ayds.Cloudmerce.service;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class TemplateRendererService {

    public String renderTemplate(String template, Map<String, Object> variables) {
        Context context = new Context();
        context.setVariables(variables);

        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        return templateEngine.process(template, context);
    }
}
