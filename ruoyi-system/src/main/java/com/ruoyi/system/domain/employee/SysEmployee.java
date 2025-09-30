package com.ruoyi.system.domain.employee;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 员工信息实体。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_employee")
public class SysEmployee extends BaseEntity implements Serializable
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "employee_id", type = IdType.AUTO)
    private Long employeeId;

    @TableField("employee_code")
    private String employeeCode;

    @TableField("employee_name")
    private String employeeName;

    @TableField("job_title")
    private String jobTitle;

    @TableField("entry_date")
    private LocalDate entryDate;

    @TableField("mobile")
    private String mobile;

    @TableField("email")
    private String email;

    @TableField("status")
    private String status;

    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;
}
