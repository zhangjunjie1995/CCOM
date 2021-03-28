package com.ccom.oauth.interceptor;

import com.ccom.oauth.util.AdminToken;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenRequestInterceptor implements RequestInterceptor {
    /**
     * fegin执行之前进行拦截
     * 1.生成令牌
     * 2.将令牌放到heards
     * 3.fegin调用
     * @param template
     */
    @Override
    public void apply(RequestTemplate template) {
        String token = AdminToken.adminToken();
        template.header("Authorization","bearer "+token);
    }
}
