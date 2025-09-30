package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.employee.SysUserEmployee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 账号与员工绑定Mapper。
 */
@Mapper
public interface SysUserEmployeeMapper extends BaseMapper<SysUserEmployee>
{
}
