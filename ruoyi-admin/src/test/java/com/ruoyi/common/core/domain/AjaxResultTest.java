package com.ruoyi.common.core.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AjaxResultTest {

    @Test
    void success_shouldSetCode200AndMessage() {
        AjaxResult res = AjaxResult.success("ok");
        assertEquals(200, res.get(AjaxResult.CODE_TAG));
        assertEquals("ok", res.get(AjaxResult.MSG_TAG));
        assertTrue(res.isSuccess());
        assertFalse(res.isError());
    }
}



