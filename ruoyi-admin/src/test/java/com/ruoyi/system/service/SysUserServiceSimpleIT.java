package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.test.base.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 简化的集成测试 - 使用 Testcontainers MySQL
 * 这个测试创建一个空的数据库，然后插入测试数据
 */
class SysUserServiceSimpleIT extends AbstractIntegrationTest {

    @Autowired
    private ISysUserService userService;

    @Test
    @DisplayName("应该能连接到 MySQL 容器并创建测试用户")
    void shouldConnectToMySQLAndCreateTestUser() {
        // 创建测试用户
        SysUser testUser = new SysUser();
        testUser.setUserName("testuser");
        testUser.setNickName("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setStatus("0"); // 正常状态
        
        // 插入用户
        int result = userService.insertUser(testUser);
        assertThat(result).isEqualTo(1);
        
        // 查询用户
        SysUser foundUser = userService.selectUserByUserName("testuser");
        assertThat(foundUser).isNotNull();
        assertThat(foundUser.getUserName()).isEqualTo("testuser");
        assertThat(foundUser.getNickName()).isEqualTo("测试用户");
        assertThat(foundUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("应该能更新用户信息")
    void shouldUpdateUser() {
        // 先创建用户
        SysUser testUser = new SysUser();
        testUser.setUserName("updateuser");
        testUser.setNickName("更新用户");
        testUser.setEmail("update@example.com");
        testUser.setPassword("password123");
        testUser.setStatus("0");
        
        userService.insertUser(testUser);
        
        // 更新用户信息
        SysUser foundUser = userService.selectUserByUserName("updateuser");
        foundUser.setNickName("已更新用户");
        foundUser.setEmail("updated@example.com");
        
        int updateResult = userService.updateUser(foundUser);
        assertThat(updateResult).isEqualTo(1);
        
        // 验证更新结果
        SysUser updatedUser = userService.selectUserByUserName("updateuser");
        assertThat(updatedUser.getNickName()).isEqualTo("已更新用户");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }
}
