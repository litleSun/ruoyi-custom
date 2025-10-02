package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.system.domain.employee.SysEmployee;
import com.ruoyi.system.domain.employee.SysUserEmployee;
import com.ruoyi.system.service.ISysEmployeeService;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工管理控制器。
 */
@RestController
@RequestMapping("/system/employee")
public class SysEmployeeController extends BaseController
{
    @Autowired
    private ISysEmployeeService employeeService;

    /**
     * 查询员工列表。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysEmployee employee)
    {
        startPage();
        List<SysEmployee> list = employeeService.selectEmployeeList(employee);
        return getDataTable(list);
    }

    /**
     * 导出员工数据。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:export')")
    @Log(title = "员工管理", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public void export(HttpServletResponse response, SysEmployee employee)
    {
        List<SysEmployee> list = employeeService.selectEmployeeList(employee);
        ExcelUtil<SysEmployee> util = new ExcelUtil<>(SysEmployee.class);
        util.exportExcel(response, list, "员工数据");
    }

    /**
     * 获取员工详情。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:query')")
    @GetMapping("/{employeeId}")
    public AjaxResult getInfo(@PathVariable Long employeeId)
    {
        return success(employeeService.selectEmployeeById(employeeId));
    }

    /**
     * 新增员工。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:add')")
    @Log(title = "员工管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysEmployee employee)
    {
        employee.setCreateBy(getUsername());
        return toAjax(employeeService.insertEmployee(employee));
    }

    /**
     * 修改员工。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:edit')")
    @Log(title = "员工管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody SysEmployee employee)
    {
        employee.setUpdateBy(getUsername());
        return toAjax(employeeService.updateEmployee(employee));
    }

    /**
     * 删除员工。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:remove')")
    @Log(title = "员工管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{employeeIds}")
    public AjaxResult remove(@PathVariable Long[] employeeIds)
    {
        return toAjax(employeeService.deleteEmployeeByIds(employeeIds));
    }

    /**
     * 修改员工状态。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:edit')")
    @Log(title = "员工管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody SysEmployee employee)
    {
        employee.setUpdateBy(getUsername());
        return toAjax(employeeService.updateEmployeeStatus(employee));
    }

    /**
     * 查询可选员工列表。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:list')")
    @GetMapping("/options")
    public AjaxResult options(String keyword, Boolean excludeBound, Long includeEmployeeId)
    {
        SysEmployee query = new SysEmployee();
        List<SysEmployee> list = employeeService.selectEmployeeOptions(query, keyword, Boolean.TRUE.equals(excludeBound), includeEmployeeId);
        return success(list);
    }

    /**
     * 查询可绑定账号列表。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:bind')")
    @GetMapping("/bind/candidates")
    public AjaxResult bindCandidates(String keyword, Long employeeId)
    {
        List<SysUser> list = employeeService.selectBindableUsers(keyword, employeeId);
        return success(list);
    }

    /**
     * 绑定账号。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:bind')")
    @Log(title = "员工管理", businessType = BusinessType.UPDATE)
    @PostMapping("/bind")
    public AjaxResult bind(@RequestBody SysUserEmployee request)
    {
        employeeService.bindUser(request.getEmployeeId(), request.getUserId());
        return success();
    }

    /**
     * 解绑账号。
     */
    @PreAuthorize("@ss.hasPermi('system:employee:bind')")
    @Log(title = "员工管理", businessType = BusinessType.UPDATE)
    @DeleteMapping("/{employeeId}/bind")
    public AjaxResult unbind(@PathVariable Long employeeId)
    {
        employeeService.unbindByEmployeeId(employeeId);
        return success();
    }
}

