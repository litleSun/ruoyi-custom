package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.employee.SysUserEmployee;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 账号与员工绑定Mapper。
 */
@Mapper
public interface SysUserEmployeeMapper extends BaseMapper<SysUserEmployee>
{
    /**
     * 查询可绑定账号列表。
     *
     * @param keyword 关键字
     * @param employeeId 员工ID
     * @return 账号集合
     */
    @Select(value = "<script>"
            + "SELECT u.user_id, u.user_name, u.nick_name, u.status "
            + "FROM sys_user u "
            + "LEFT JOIN sys_user_employee sue ON sue.user_id = u.user_id "
            + "<where>"
            + " u.del_flag = '0' "
            + "<if test='keyword != null and keyword != \"\"'>"
            + " AND (u.user_name LIKE CONCAT('%', #{keyword}, '%') OR u.nick_name LIKE CONCAT('%', #{keyword}, '%'))"
            + "</if>"
            + "<if test='employeeId != null'>"
            + " AND (sue.user_id IS NULL OR sue.employee_id = #{employeeId})"
            + "</if>"
            + "<if test='employeeId == null'>"
            + " AND sue.user_id IS NULL"
            + "</if>"
            + "</where>"
            + " ORDER BY u.create_time DESC"
            + " LIMIT 20"
            + "</script>")
    List<SysUser> selectBindableUsers(@Param("keyword") String keyword, @Param("employeeId") Long employeeId);
}
