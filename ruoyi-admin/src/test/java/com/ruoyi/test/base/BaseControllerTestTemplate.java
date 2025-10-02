package com.ruoyi.test.base;

import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.common.core.redis.RedisCache;

import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web层测试模板 - 使用@WebMvcTest
 */
@WebMvcTest
@Import(TestTokenConfig.class)
public abstract class BaseControllerTestTemplate {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected TokenService tokenService;

    @MockBean
    protected RedisCache redisCache;

}