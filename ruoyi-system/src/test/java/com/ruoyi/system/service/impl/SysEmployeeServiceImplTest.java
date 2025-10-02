package com.ruoyi.system.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.system.domain.employee.SysEmployee;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysEmployeeMapper;
import com.ruoyi.system.service.ISysDeptService;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class SysEmployeeServiceImplTest
{
    @InjectMocks
    private SysEmployeeServiceImpl employeeService;

    @Mock
    private SysEmployeeMapper employeeMapper;

    @Mock
    private ISysDeptService deptService;

    @Mock
    private SysDeptMapper deptMapper;

    @BeforeEach
    void setUp()
    {
        ReflectionTestUtils.setField(employeeService, "baseMapper", employeeMapper);
    }

    @Test
    @DisplayName("校验通过时不会抛出部门不存在异常")
    void validateEmployeeShouldPassWhenDeptNormal()
    {
        SysEmployee employee = new SysEmployee();
        employee.setEmployeeCode("E001");
        employee.setEmployeeName("张三");
        employee.setDeptIds(Collections.singletonList(1L));

        when(employeeMapper.selectCount(any())).thenReturn(0L);

        SysDept dept = new SysDept();
        dept.setDeptId(1L);
        dept.setDeptName("测试部门");
        dept.setDelFlag(UserConstants.NORMAL);
        dept.setStatus(UserConstants.DEPT_NORMAL);
        when(deptMapper.selectDeptById(1L)).thenReturn(dept);

        assertDoesNotThrow(() -> ReflectionTestUtils.invokeMethod(employeeService, "validateEmployee", employee, true));

        verify(deptService).checkDeptDataScope(1L);
    }
}
