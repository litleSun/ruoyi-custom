package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.domain.employee.SysEmployee;
import java.util.List;

/**
 * 员工管理服务接口。
 */
public interface ISysEmployeeService
{
    /**
     * 查询员工列表。
     *
     * @param employee 查询参数
     * @return 员工集合
     */
    List<SysEmployee> selectEmployeeList(SysEmployee employee);

    /**
     * 查询员工详情。
     *
     * @param employeeId 员工ID
     * @return 员工信息
     */
    SysEmployee selectEmployeeById(Long employeeId);

    /**
     * 新增员工。
     *
     * @param employee 员工信息
     * @return 结果
     */
    int insertEmployee(SysEmployee employee);

    /**
     * 更新员工。
     *
     * @param employee 员工信息
     * @return 结果
     */
    int updateEmployee(SysEmployee employee);

    /**
     * 删除员工。
     *
     * @param employeeIds 员工ID数组
     * @return 结果
     */
    int deleteEmployeeByIds(Long[] employeeIds);

    /**
     * 修改员工状态。
     *
     * @param employee 员工信息
     * @return 结果
     */
    int updateEmployeeStatus(SysEmployee employee);

    /**
     * 查询可选员工列表。
     *
     * @param keyword 关键字
     * @param excludeBound 是否排除已绑定员工
     * @param includeEmployeeId 额外包含的员工ID
     * @return 员工集合
     */
    List<SysEmployee> selectEmployeeOptions(String keyword, boolean excludeBound, Long includeEmployeeId);

    /**
     * 查询可绑定账号列表。
     *
     * @param keyword 关键字
     * @param employeeId 员工ID
     * @return 账号集合
     */
    List<SysUser> selectBindableUsers(String keyword, Long employeeId);

    /**
     * 绑定账号。
     *
     * @param employeeId 员工ID
     * @param userId 账号ID
     */
    void bindUser(Long employeeId, Long userId);

    /**
     * 根据员工解除绑定。
     *
     * @param employeeId 员工ID
     */
    void unbindByEmployeeId(Long employeeId);

    /**
     * 根据账号解除绑定。
     *
     * @param userId 账号ID
     */
    void unbindByUserId(Long userId);
}

