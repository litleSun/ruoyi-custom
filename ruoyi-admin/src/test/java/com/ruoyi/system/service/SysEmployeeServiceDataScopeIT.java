package com.ruoyi.system.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.framework.security.context.PermissionContextHolder;
import com.ruoyi.system.domain.employee.SysEmployee;
import com.ruoyi.test.base.AbstractIntegrationTest;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 集成测试：验证数据权限在员工选项查询中的作用。
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SysEmployeeServiceDataScopeIT extends AbstractIntegrationTest
{
    @Autowired
    private ISysEmployeeService employeeService;

    @Autowired
    private DataSource dataSource;

    private ServletRequestAttributes requestAttributes;

    @BeforeAll
    void loadEmployeeModule() throws Exception
    {
        File script = Paths.get("..", "sql", "ry_20250522_employee_module.sql").toFile();
        if (!script.exists())
        {
            script = Paths.get("sql", "ry_20250522_employee_module.sql").toFile();
        }
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(new FileSystemResource(script));
        populator.execute(dataSource);
    }

    @BeforeEach
    void setupSecurityContext()
    {
        SysRole role = new SysRole();
        role.setRoleId(200L);
        role.setRoleKey("dataScopeRole");
        role.setDataScope("3");
        role.setStatus(UserConstants.ROLE_NORMAL);
        role.setPermissions(Set.of("system:employee:list"));

        SysUser user = new SysUser();
        user.setUserId(200L);
        user.setDeptId(105L);
        user.setUserName("limited-user");
        user.setRoles(Collections.singletonList(role));

        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUser(user);
        loginUser.setPermissions(Set.of("system:employee:list"));

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null,
                Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(requestAttributes);
        PermissionContextHolder.setContext("system:employee:list");
    }

    @AfterEach
    void clearSecurityContext()
    {
        SecurityContextHolder.clearContext();
        if (requestAttributes != null)
        {
            requestAttributes.requestCompleted();
            RequestContextHolder.resetRequestAttributes();
            requestAttributes = null;
        }
    }

    @Test
    @DisplayName("受限数据权限的用户仅能看到所属部门的员工选项")
    void shouldRestrictEmployeeOptionsByDeptDataScope()
    {
        SysEmployee query = new SysEmployee();
        List<SysEmployee> employees = employeeService.selectEmployeeOptions(query, null, false, null);

        assertThat(employees)
                .extracting(SysEmployee::getEmployeeId)
                .contains(2002L)
                .doesNotContain(2001L);
    }
}
