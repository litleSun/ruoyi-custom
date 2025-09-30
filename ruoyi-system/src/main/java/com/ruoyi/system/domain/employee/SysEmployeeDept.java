package com.ruoyi.system.domain.employee;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 员工与部门关联实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_employee_dept")
public class SysEmployeeDept implements Serializable
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("employee_id")
    private Long employeeId;

    @TableField("dept_id")
    private Long deptId;

    @TableField("create_time")
    private LocalDateTime createTime;
}
