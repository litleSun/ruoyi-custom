SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- 1、员工信息表
-- ----------------------------
DROP TABLE IF EXISTS `sys_employee`;
CREATE TABLE `sys_employee` (
  `employee_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '员工ID',
  `employee_code` varchar(64) NOT NULL DEFAULT '' COMMENT '员工编号',
  `employee_name` varchar(50) NOT NULL COMMENT '员工姓名',
  `job_title` varchar(100) DEFAULT NULL COMMENT '岗位',
  `entry_date` date DEFAULT NULL COMMENT '入职日期',
  `mobile` varchar(20) DEFAULT NULL COMMENT '联系方式',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '员工状态（0在职 1离职）',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `uk_employee_code` (`employee_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

-- ----------------------------
-- 初始化-员工信息表数据
-- ----------------------------
INSERT INTO `sys_employee` (`employee_id`, `employee_code`, `employee_name`, `job_title`, `entry_date`, `mobile`, `email`, `status`, `del_flag`, `create_by`, `create_time`, `remark`)
VALUES
  (2001, 'E2024001', '张三', '高级工程师', '2023-03-18', '13800000001', 'zhangsan@example.com', '0', '0', 'admin', sysdate(), '资深后端工程师'),
  (2002, 'E2024002', '李四', '测试经理', '2022-11-02', '13800000002', 'lisi@example.com', '0', '0', 'admin', sysdate(), '长沙分公司测试负责人');

-- ----------------------------
-- 2、员工与部门关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_employee_dept`;
CREATE TABLE `sys_employee_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_employee_dept` (`employee_id`,`dept_id`),
  KEY `idx_employee_dept_dept` (`dept_id`),
  CONSTRAINT `fk_employee_dept_employee` FOREIGN KEY (`employee_id`) REFERENCES `sys_employee` (`employee_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_employee_dept_dept` FOREIGN KEY (`dept_id`) REFERENCES `sys_dept` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工与部门关联表';

-- ----------------------------
-- 初始化-员工与部门关联表数据
-- ----------------------------
INSERT INTO `sys_employee_dept` (`id`, `employee_id`, `dept_id`, `create_time`)
VALUES
  (3001, 2001, 103, sysdate()),
  (3002, 2001, 107, sysdate()),
  (3003, 2002, 105, sysdate());

-- ----------------------------
-- 3、账号与员工绑定表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_employee`;
CREATE TABLE `sys_user_employee` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '账号ID',
  `employee_id` bigint(20) NOT NULL COMMENT '员工ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_employee_user` (`user_id`),
  UNIQUE KEY `uk_user_employee_employee` (`employee_id`),
  CONSTRAINT `fk_user_employee_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_employee_employee` FOREIGN KEY (`employee_id`) REFERENCES `sys_employee` (`employee_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号与员工绑定表';

-- ----------------------------
-- 初始化-账号与员工绑定数据
-- ----------------------------
INSERT INTO `sys_user_employee` (`id`, `user_id`, `employee_id`, `create_time`)
VALUES
  (4001, 1, 2001, sysdate());

SET FOREIGN_KEY_CHECKS = 1;
