# 测试框架说明

## 概述
本项目已集成完整的测试框架，包括单元测试、集成测试和端到端测试。

## 📁 测试文件结构
```
ruoyi-admin/src/test/
├── java/
│   └── com/ruoyi/
│       ├── test/base/
│       │   └── AbstractIntegrationTest.java # 集成测试基类(Testcontainers)
│       ├── common/core/domain/
│       │   └── AjaxResultTest.java          # 单元测试示例
│       ├── web/controller/common/
│       │   └── CaptchaControllerTest.java   # Web层测试示例
│       └── system/service/
│           ├── SysUserServiceSimpleIT.java  # Testcontainers集成测试
│           ├── SysUserServiceMockIT.java    # 内存数据库集成测试
│           └── SysUserServiceIT.java        # 通用集成测试
├── resources/
│   ├── application-test.yml                 # 测试环境配置
│   └── README.md                           # 测试说明文档
└── README.md                               # 测试框架说明
```

## 测试类型

### 1. 单元测试 (Unit Tests)
- **位置**: `src/test/java/` 下以 `Test.java` 结尾的类
- **特点**: 快速执行，不依赖外部服务
- **示例**: `AjaxResultTest.java`

### 2. 集成测试 (Integration Tests)
- **位置**: `src/test/java/` 下以 `IT.java` 结尾的类
- **特点**: 使用真实数据库，测试完整业务流程
- **示例**: `SysUserServiceSimpleIT.java`

### 3. Web层测试
- **位置**: `src/test/java/` 下使用 `@WebMvcTest` 注解的类
- **特点**: 测试 Controller 层，模拟 HTTP 请求
- **示例**: `CaptchaControllerTest.java`

## 技术栈
- **JUnit 5**: 测试框架
- **Spring Boot Test**: Spring Boot 测试支持
- **Mockito**: Mock 框架
- **Testcontainers**: 容器化测试支持
- **JaCoCo**: 代码覆盖率

## 测试最佳实践

### 1. 测试命名规范
- 单元测试: `*Test.java`
- 集成测试: `*IT.java`
- 测试方法: 使用描述性的方法名

### 2. 测试结构
```java
@Test
@DisplayName("描述测试目的")
void shouldDoSomething() {
    // Given - 准备测试数据
    // When - 执行被测试的方法
    // Then - 验证结果
}
```

### 3. 断言使用
- 使用 JUnit 5 的断言方法
- 提供清晰的错误消息
- 验证多个方面

## 运行测试

### 常用命令
```bash
# 运行所有测试
mvn test

# 运行单元测试
mvn test -Dtest="*Test"

# 运行集成测试
mvn verify -Dtest="*IT"

# 生成覆盖率报告
mvn verify
# 报告位置: target/site/jacoco/index.html
```

## Testcontainers 使用指南

### 配置示例
```java
@Testcontainers
@SpringBootTest
class SysUserServiceIT {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("sql/ry_20250522.sql");
    
    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
```

### 环境要求
- Docker Desktop 已安装并运行
- JDK 8+
- Maven 3.6+
