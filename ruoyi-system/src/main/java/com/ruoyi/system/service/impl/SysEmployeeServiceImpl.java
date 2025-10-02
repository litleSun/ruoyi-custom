package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.Seq;
import com.ruoyi.system.domain.employee.SysEmployee;
import com.ruoyi.system.domain.employee.SysEmployeeDept;
import com.ruoyi.system.domain.employee.SysUserEmployee;
import com.ruoyi.system.mapper.SysDeptMapper;
import com.ruoyi.system.mapper.SysEmployeeDeptMapper;
import com.ruoyi.system.mapper.SysEmployeeMapper;
import com.ruoyi.system.mapper.SysUserEmployeeMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysEmployeeService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 员工管理服务实现。
 */
@Service
public class SysEmployeeServiceImpl extends ServiceImpl<SysEmployeeMapper, SysEmployee> implements ISysEmployeeService
{
    private static final String DATA_SCOPE = "dataScope";
    private static final String EMPLOYEE_CODE_PREFIX = "EMP";

    @Autowired
    private SysEmployeeDeptMapper employeeDeptMapper;

    @Autowired
    private SysUserEmployeeMapper userEmployeeMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private ISysDeptService deptService;

    @Override
    @DataScope(deptAlias = "d")
    public List<SysEmployee> selectEmployeeList(SysEmployee employee)
    {
        LambdaQueryWrapper<SysEmployee> wrapper = buildQueryWrapper(employee);
        List<SysEmployee> employees = list(wrapper);
        attachRelations(employees, true);
        return employees;
    }

    @Override
    public SysEmployee selectEmployeeById(Long employeeId)
    {
        if (employeeId == null)
        {
            return null;
        }
        SysEmployee employee = getById(employeeId);
        if (employee == null)
        {
            return null;
        }
        attachRelations(Collections.singletonList(employee), true);
        return employee;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertEmployee(SysEmployee employee)
    {
        validateEmployee(employee, true);
        if (StringUtils.isEmpty(employee.getStatus()))
        {
            employee.setStatus(UserConstants.NORMAL);
        }
        employee.setCreateTime(new Date());
        employee.setDelFlag(UserConstants.NORMAL);
        boolean result = save(employee);
        if (!result)
        {
            throw new ServiceException("新增员工失败，请重试");
        }
        saveEmployeeDepts(employee.getEmployeeId(), employee.getDeptIds());
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateEmployee(SysEmployee employee)
    {
        validateEmployee(employee, false);
        employee.setUpdateTime(new Date());
        boolean result = updateById(employee);
        if (!result)
        {
            throw new ServiceException("更新员工失败，请重试");
        }
        // 更新部门关联
        employeeDeptMapper.delete(new LambdaQueryWrapper<SysEmployeeDept>().eq(SysEmployeeDept::getEmployeeId, employee.getEmployeeId()));
        saveEmployeeDepts(employee.getEmployeeId(), employee.getDeptIds());
        return 1;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteEmployeeByIds(Long[] employeeIds)
    {
        if (employeeIds == null || employeeIds.length == 0)
        {
            return 0;
        }
        List<Long> ids = Arrays.stream(employeeIds).filter(id -> id != null).collect(Collectors.toList());
        if (ids.isEmpty())
        {
            return 0;
        }
        removeByIds(ids);
        employeeDeptMapper.delete(new LambdaQueryWrapper<SysEmployeeDept>().in(SysEmployeeDept::getEmployeeId, ids));
        userEmployeeMapper.delete(new LambdaQueryWrapper<SysUserEmployee>().in(SysUserEmployee::getEmployeeId, ids));
        return ids.size();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateEmployeeStatus(SysEmployee employee)
    {
        if (employee == null || employee.getEmployeeId() == null)
        {
            return 0;
        }
        LambdaUpdateWrapper<SysEmployee> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysEmployee::getEmployeeId, employee.getEmployeeId())
                .set(SysEmployee::getStatus, employee.getStatus())
                .set(StringUtils.isNotEmpty(employee.getUpdateBy()), SysEmployee::getUpdateBy, employee.getUpdateBy())
                .set(SysEmployee::getUpdateTime, new Date());
        return update(updateWrapper) ? 1 : 0;
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<SysEmployee> selectEmployeeOptions(SysEmployee employee, String keyword, boolean excludeBound, Long includeEmployeeId)
    {
        SysEmployee query = employee == null ? new SysEmployee() : employee;
        if (StringUtils.isEmpty(query.getStatus()))
        {
            query.setStatus(UserConstants.NORMAL);
        }
        LambdaQueryWrapper<SysEmployee> wrapper = buildQueryWrapper(query);
        if (StringUtils.isNotEmpty(keyword))
        {
            wrapper.and(w -> w.like(SysEmployee::getEmployeeCode, keyword)
                    .or().like(SysEmployee::getEmployeeName, keyword));
        }
        if (excludeBound)
        {
            wrapper.notInSql(SysEmployee::getEmployeeId, "select employee_id from sys_user_employee");
        }
        wrapper.orderByAsc(SysEmployee::getEmployeeCode).last("limit 50");
        List<SysEmployee> list = list(wrapper);
        if (includeEmployeeId != null)
        {
            LambdaQueryWrapper<SysEmployee> includeWrapper = buildQueryWrapper(query);
            includeWrapper.eq(SysEmployee::getEmployeeId, includeEmployeeId);
            SysEmployee include = getOne(includeWrapper, false);
            if (include != null && list.stream().noneMatch(item -> item.getEmployeeId().equals(includeEmployeeId)))
            {
                list.add(0, include);
            }
        }
        return list;
    }

    @Override
    public List<SysUser> selectBindableUsers(String keyword, Long employeeId)
    {
        return userEmployeeMapper.selectBindableUsers(keyword, employeeId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindUser(Long employeeId, Long userId)
    {
        if (employeeId == null || userId == null)
        {
            throw new ServiceException("绑定参数不完整");
        }
        SysEmployee employee = getById(employeeId);
        if (employee == null || !UserConstants.NORMAL.equals(employee.getDelFlag()))
        {
            throw new ServiceException("员工不存在或已删除");
        }
        if (!UserConstants.NORMAL.equals(employee.getStatus()))
        {
            throw new ServiceException("员工状态不可用，无法绑定账号");
        }
        SysUser user = userMapper.selectUserById(userId);
        if (user == null || !UserConstants.NORMAL.equals(user.getDelFlag()))
        {
            throw new ServiceException("账号不存在或已删除");
        }
        SysUserEmployee employeeBinding = userEmployeeMapper.selectOne(new LambdaQueryWrapper<SysUserEmployee>().eq(SysUserEmployee::getEmployeeId, employeeId));
        if (employeeBinding != null && !userId.equals(employeeBinding.getUserId()))
        {
            throw new ServiceException("当前员工已绑定其他账号，请先解绑");
        }
        SysUserEmployee userBinding = userEmployeeMapper.selectOne(new LambdaQueryWrapper<SysUserEmployee>().eq(SysUserEmployee::getUserId, userId));
        LocalDateTime now = LocalDateTime.now();
        if (userBinding == null)
        {
            SysUserEmployee bind = new SysUserEmployee();
            bind.setUserId(userId);
            bind.setEmployeeId(employeeId);
            bind.setCreateTime(now);
            userEmployeeMapper.insert(bind);
        }
        else
        {
            userBinding.setEmployeeId(employeeId);
            userBinding.setCreateTime(now);
            userEmployeeMapper.updateById(userBinding);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindByEmployeeId(Long employeeId)
    {
        if (employeeId == null)
        {
            return;
        }
        userEmployeeMapper.delete(new LambdaQueryWrapper<SysUserEmployee>().eq(SysUserEmployee::getEmployeeId, employeeId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unbindByUserId(Long userId)
    {
        if (userId == null)
        {
            return;
        }
        userEmployeeMapper.delete(new LambdaQueryWrapper<SysUserEmployee>().eq(SysUserEmployee::getUserId, userId));
    }

    private LambdaQueryWrapper<SysEmployee> buildQueryWrapper(SysEmployee employee)
    {
        LambdaQueryWrapper<SysEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysEmployee::getDelFlag, UserConstants.NORMAL);
        if (employee != null)
        {
            if (StringUtils.isNotEmpty(employee.getEmployeeCode()))
            {
                wrapper.like(SysEmployee::getEmployeeCode, employee.getEmployeeCode());
            }
            if (StringUtils.isNotEmpty(employee.getEmployeeName()))
            {
                wrapper.like(SysEmployee::getEmployeeName, employee.getEmployeeName());
            }
            if (StringUtils.isNotEmpty(employee.getStatus()))
            {
                wrapper.eq(SysEmployee::getStatus, employee.getStatus());
            }
            if (employee.getDeptId() != null)
            {
                List<Long> deptIds = collectDeptIds(employee.getDeptId());
                if (!deptIds.isEmpty())
                {
                    wrapper.inSql(SysEmployee::getEmployeeId,
                            "SELECT employee_id FROM sys_employee_dept WHERE dept_id IN (" + StringUtils.join(deptIds, ",") + ")");
                }
            }
            Object dataScope = employee.getParams().get(DATA_SCOPE);
            if (dataScope != null && StringUtils.isNotEmpty(dataScope.toString()))
            {
                wrapper.apply("EXISTS (SELECT 1 FROM sys_employee_dept d WHERE d.employee_id = sys_employee.employee_id {0})", dataScope.toString());
            }
        }
        wrapper.orderByDesc(SysEmployee::getCreateTime);
        return wrapper;
    }

    private void validateEmployee(SysEmployee employee, boolean isCreate)
    {
        if (employee == null)
        {
            throw new ServiceException("员工信息不能为空");
        }
        employee.setEmployeeCode(StringUtils.trim(employee.getEmployeeCode()));
        applyDefaultEmployeeCode(employee);
        if (StringUtils.isNotEmpty(employee.getEmployeeCode()) && employee.getEmployeeCode().length() > 64)
        {
            throw new ServiceException("员工编号长度不能超过64个字符");
        }
        if (StringUtils.isEmpty(employee.getEmployeeName()))
        {
            throw new ServiceException("员工姓名不能为空");
        }
        if (CollectionUtils.isEmpty(employee.getDeptIds()))
        {
            throw new ServiceException("请至少选择一个所属部门");
        }
        // 校验编号唯一
        LambdaQueryWrapper<SysEmployee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysEmployee::getEmployeeCode, employee.getEmployeeCode())
                .eq(SysEmployee::getDelFlag, UserConstants.NORMAL);
        if (!isCreate && employee.getEmployeeId() != null)
        {
            wrapper.ne(SysEmployee::getEmployeeId, employee.getEmployeeId());
        }
        if (count(wrapper) > 0)
        {
            throw new ServiceException("员工编号已存在");
        }
        // 校验部门合法性
        for (Long deptId : employee.getDeptIds())
        {
            if (deptId == null)
            {
                continue;
            }
            deptService.checkDeptDataScope(deptId);
            SysDept dept = deptMapper.selectDeptById(deptId);
            if (dept == null || !UserConstants.NORMAL.equals(dept.getDelFlag()))
            {
                throw new ServiceException("选择的部门不存在");
            }
            if (!UserConstants.DEPT_NORMAL.equals(dept.getStatus()))
            {
                throw new ServiceException("部门 " + dept.getDeptName() + " 已停用");
            }
        }
    }


    private void applyDefaultEmployeeCode(SysEmployee employee)
    {
        if (employee == null || StringUtils.isNotEmpty(employee.getEmployeeCode()))
        {
            return;
        }
        employee.setEmployeeCode(generateEmployeeCode());
    }

    private String generateEmployeeCode()
    {
        return EMPLOYEE_CODE_PREFIX + Seq.getId();
    }

    private void saveEmployeeDepts(Long employeeId, Collection<Long> deptIds)
    {
        if (employeeId == null || CollectionUtils.isEmpty(deptIds))
        {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (Long deptId : deptIds)
        {
            if (deptId == null)
            {
                continue;
            }
            SysEmployeeDept relation = new SysEmployeeDept();
            relation.setEmployeeId(employeeId);
            relation.setDeptId(deptId);
            relation.setCreateTime(now);
            employeeDeptMapper.insert(relation);
        }
    }

    private List<Long> collectDeptIds(Long rootDeptId)
    {
        List<Long> ids = new ArrayList<>();
        ids.add(rootDeptId);
        List<SysDept> children = deptMapper.selectChildrenDeptById(rootDeptId);
        if (CollectionUtils.isNotEmpty(children))
        {
            ids.addAll(children.stream().map(SysDept::getDeptId).collect(Collectors.toList()));
        }
        return ids;
    }

    private void attachRelations(List<SysEmployee> employees, boolean includeBindUser)
    {
        if (CollectionUtils.isEmpty(employees))
        {
            return;
        }
        List<Long> employeeIds = employees.stream().map(SysEmployee::getEmployeeId).collect(Collectors.toList());
        List<SysEmployeeDept> relations = employeeDeptMapper.selectList(new LambdaQueryWrapper<SysEmployeeDept>().in(SysEmployeeDept::getEmployeeId, employeeIds));
        Map<Long, List<Long>> employeeDeptMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(relations))
        {
            for (SysEmployeeDept relation : relations)
            {
                employeeDeptMap.computeIfAbsent(relation.getEmployeeId(), k -> new ArrayList<>()).add(relation.getDeptId());
            }
        }
        Set<Long> deptIdSet = relations.stream().map(SysEmployeeDept::getDeptId).collect(Collectors.toSet());
        Map<Long, String> deptNameMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(deptIdSet))
        {
            List<SysDept> depts = deptMapper.selectDeptByIds(new ArrayList<>(deptIdSet));
            for (SysDept dept : depts)
            {
                deptNameMap.put(dept.getDeptId(), dept.getDeptName());
            }
        }
        Map<Long, SysUserEmployee> bindMap = new HashMap<>();
        Map<Long, SysUser> userMap = new HashMap<>();
        if (includeBindUser)
        {
            List<SysUserEmployee> bindings = userEmployeeMapper.selectList(new LambdaQueryWrapper<SysUserEmployee>().in(SysUserEmployee::getEmployeeId, employeeIds));
            if (CollectionUtils.isNotEmpty(bindings))
            {
                Set<Long> userIdSet = new HashSet<>();
                for (SysUserEmployee binding : bindings)
                {
                    bindMap.put(binding.getEmployeeId(), binding);
                    userIdSet.add(binding.getUserId());
                }
                if (CollectionUtils.isNotEmpty(userIdSet))
                {
                    List<SysUser> users = userMapper.selectSimpleUserListByIds(new ArrayList<>(userIdSet));
                    for (SysUser user : users)
                    {
                        userMap.put(user.getUserId(), user);
                    }
                }
            }
        }
        for (SysEmployee employee : employees)
        {
            List<Long> deptIdList = new ArrayList<>(employeeDeptMap.getOrDefault(employee.getEmployeeId(), Collections.emptyList()));
            employee.setDeptIds(deptIdList);
            List<String> names = deptIdList.stream().map(deptNameMap::get).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
            employee.setDeptNames(names);
            if (includeBindUser)
            {
                SysUserEmployee binding = bindMap.get(employee.getEmployeeId());
                if (binding != null)
                {
                    employee.setBindUser(userMap.get(binding.getUserId()));
                }
            }
        }
    }
}

