package com.ruoyi.system.domain.employee;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.domain.entity.SysUser;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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
    @Excel(name = "员工编号")
    private String employeeCode;

    @TableField("employee_name")
    @Excel(name = "员工姓名")
    private String employeeName;

    @TableField("job_title")
    @Excel(name = "岗位")
    private String jobTitle;

    @TableField("entry_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "入职日期", width = 20, dateFormat = "yyyy-MM-dd")
    private LocalDate entryDate;

    @TableField("mobile")
    @Excel(name = "联系方式")
    private String mobile;

    @TableField("email")
    @Excel(name = "邮箱")
    private String email;

    @TableField("status")
    @Excel(name = "状态", readConverterExp = "0=在职,1=离职")
    private String status;

    @TableLogic(value = "0", delval = "2")
    @TableField("del_flag")
    private String delFlag;

    @TableField(exist = false)
    private Long deptId;

    @TableField(exist = false)
    private List<Long> deptIds;

    @TableField(exist = false)
    private List<String> deptNames;

    @TableField(exist = false)
    private SysUser bindUser;

    public List<Long> getDeptIds()
    {
        if (deptIds == null)
        {
            deptIds = new ArrayList<>();
        }
        return deptIds;
    }

    public void setDeptIds(List<Long> deptIds)
    {
        this.deptIds = deptIds;
    }

    public List<String> getDeptNames()
    {
        if (deptNames == null)
        {
            deptNames = new ArrayList<>();
        }
        return deptNames;
    }

    public void setDeptNames(List<String> deptNames)
    {
        this.deptNames = deptNames;
    }
}
