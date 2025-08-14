package com.ruoyi.web.controller.common;

import com.google.code.kaptcha.Producer;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.system.service.ISysConfigService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import java.awt.image.BufferedImage;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CaptchaController.class)
class CaptchaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ISysConfigService configService;

    @MockBean
    private RedisCache redisCache;

    @MockBean
    private com.ruoyi.framework.web.service.TokenService tokenService;

    @MockBean(name = "captchaProducer")
    private Producer captchaProducer;

    @MockBean(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Test
    @DisplayName("当验证码关闭时，应仅返回captchaEnabled=false")
    @WithMockUser
    void captchaDisabled_returnsFlagOnly() throws Exception {
        when(configService.selectCaptchaEnabled()).thenReturn(false);

        mockMvc.perform(get("/captchaImage"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.captchaEnabled").value(false));
    }

    @Test
    @DisplayName("当验证码开启且为char类型时，返回uuid与img")
    @WithMockUser
    void captchaEnabled_charType_returnsImage() throws Exception {
        when(configService.selectCaptchaEnabled()).thenReturn(true);
        try (MockedStatic<RuoYiConfig> mocked = Mockito.mockStatic(RuoYiConfig.class)) {
            mocked.when(RuoYiConfig::getCaptchaType).thenReturn("char");
            when(captchaProducer.createText()).thenReturn("ABCD");
            when(captchaProducer.createImage(Mockito.anyString()))
                    .thenReturn(new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB));

            mockMvc.perform(get("/captchaImage"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.captchaEnabled").value(true))
                    .andExpect(jsonPath("$.uuid").isNotEmpty())
                    .andExpect(jsonPath("$.img").isNotEmpty());
        }
    }
}


