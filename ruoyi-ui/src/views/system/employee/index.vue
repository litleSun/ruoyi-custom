<template>
  <div class="app-container employee-container">
    <el-row :gutter="20">
      <splitpanes :horizontal="$store.getters.device === 'mobile'" class="default-theme">
        <pane size="16">
          <el-col>
            <div class="head-container">
              <el-input
                v-model="deptName"
                placeholder="请输入部门名称"
                clearable
                size="small"
                prefix-icon="el-icon-search"
                style="margin-bottom: 20px"
              />
            </div>
            <div class="head-container">
              <el-tree
                ref="tree"
                :data="deptOptions"
                :props="defaultProps"
                :expand-on-click-node="false"
                :filter-node-method="filterNode"
                node-key="id"
                default-expand-all
                highlight-current
                @node-click="handleNodeClick"
              />
            </div>
          </el-col>
        </pane>
        <pane size="84">
          <el-col>
            <el-form
              ref="queryForm"
              :model="queryParams"
              size="small"
              :inline="true"
              v-show="showSearch"
              label-width="80px"
            >
              <el-form-item label="员工编号" prop="employeeCode">
                <el-input
                  v-model="queryParams.employeeCode"
                  placeholder="请输入员工编号"
                  clearable
                  style="width: 240px"
                  @keyup.enter.native="handleQuery"
                />
              </el-form-item>
              <el-form-item label="员工姓名" prop="employeeName">
                <el-input
                  v-model="queryParams.employeeName"
                  placeholder="请输入员工姓名"
                  clearable
                  style="width: 240px"
                  @keyup.enter.native="handleQuery"
                />
              </el-form-item>
              <el-form-item label="状态" prop="status">
                <el-select v-model="queryParams.status" placeholder="员工状态" clearable style="width: 240px">
                  <el-option
                    v-for="dict in dict.type.sys_normal_disable"
                    :key="dict.value"
                    :label="dict.label"
                    :value="dict.value"
                  />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <el-row :gutter="10" class="mb8">
              <el-col :span="1.5">
                <el-button
                  type="primary"
                  plain
                  icon="el-icon-plus"
                  size="mini"
                  @click="handleAdd"
                  v-hasPermi="['system:employee:add']"
                >新增</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="success"
                  plain
                  icon="el-icon-edit"
                  size="mini"
                  :disabled="single"
                  @click="handleUpdate"
                  v-hasPermi="['system:employee:edit']"
                >修改</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="danger"
                  plain
                  icon="el-icon-delete"
                  size="mini"
                  :disabled="multiple"
                  @click="handleDelete"
                  v-hasPermi="['system:employee:remove']"
                >删除</el-button>
              </el-col>
              <el-col :span="1.5">
                <el-button
                  type="warning"
                  plain
                  icon="el-icon-download"
                  size="mini"
                  @click="handleExport"
                  v-hasPermi="['system:employee:export']"
                >导出</el-button>
              </el-col>
              <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" :columns="columns" />
            </el-row>

            <el-table v-loading="loading" :data="employeeList" @selection-change="handleSelectionChange">
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column
                v-if="columns.employeeCode.visible"
                label="员工编号"
                align="center"
                key="employeeCode"
                prop="employeeCode"
                show-overflow-tooltip
              />
              <el-table-column
                v-if="columns.employeeName.visible"
                label="员工姓名"
                align="center"
                key="employeeName"
                prop="employeeName"
                show-overflow-tooltip
              />
              <el-table-column
                v-if="columns.jobTitle.visible"
                label="岗位"
                align="center"
                key="jobTitle"
                prop="jobTitle"
                show-overflow-tooltip
              />
              <el-table-column v-if="columns.deptNames.visible" label="所属部门" align="center" key="deptNames">
                <template slot-scope="scope">
                  <span>{{ formatDeptNames(scope.row.deptNames) }}</span>
                </template>
              </el-table-column>
              <el-table-column
                v-if="columns.entryDate.visible"
                label="入职日期"
                align="center"
                key="entryDate"
                prop="entryDate"
                width="120"
              />
              <el-table-column
                v-if="columns.mobile.visible"
                label="联系方式"
                align="center"
                key="mobile"
                prop="mobile"
                width="140"
              />
              <el-table-column v-if="columns.bindAccount.visible" label="绑定账号" align="center" key="bindAccount">
                <template slot-scope="scope">
                  <span v-if="scope.row.bindUser">
                    {{ scope.row.bindUser.userName }}
                    <span v-if="scope.row.bindUser.nickName" class="text-muted">（{{ scope.row.bindUser.nickName }}）</span>
                  </span>
                  <span v-else class="text-muted">未绑定</span>
                </template>
              </el-table-column>
              <el-table-column v-if="columns.status.visible" label="状态" align="center" key="status">
                <template slot-scope="scope">
                  <el-switch
                    v-model="scope.row.status"
                    active-value="0"
                    inactive-value="1"
                    @change="handleStatusChange(scope.row)"
                    v-hasPermi="['system:employee:edit']"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-view"
                    @click="openDetail(scope.row)"
                    v-hasPermi="['system:employee:query']"
                  >详情</el-button>
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-edit"
                    @click="handleUpdate(scope.row)"
                    v-hasPermi="['system:employee:edit']"
                  >修改</el-button>
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-delete"
                    @click="handleDelete(scope.row)"
                    v-hasPermi="['system:employee:remove']"
                  >删除</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination
              v-show="total > 0"
              :total="total"
              :page.sync="queryParams.pageNum"
              :limit.sync="queryParams.pageSize"
              @pagination="getList"
            />
          </el-col>
        </pane>
      </splitpanes>
    </el-row>

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工编号" prop="employeeCode">
              <el-input v-model="form.employeeCode" placeholder="请输入员工编号" maxlength="64" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="员工姓名" prop="employeeName">
              <el-input v-model="form.employeeName" placeholder="请输入员工姓名" maxlength="50" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="岗位" prop="jobTitle">
              <el-input v-model="form.jobTitle" placeholder="请输入岗位" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="entryDate">
              <el-date-picker
                v-model="form.entryDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择入职日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系方式" prop="mobile">
              <el-input v-model="form.mobile" placeholder="请输入联系方式" maxlength="20" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属部门" prop="deptIds">
              <treeselect
                v-model="form.deptIds"
                :options="enabledDeptOptions"
                :show-count="true"
                :multiple="true"
                placeholder="请选择所属部门"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-drawer
      title="员工详情"
      :visible.sync="detailVisible"
      size="480px"
      append-to-body
      :destroy-on-close="true"
    >
      <div v-loading="detailLoading" class="employee-detail">
        <el-descriptions v-if="detail" :column="1" border size="small">
          <el-descriptions-item label="员工编号">{{ detail.employeeCode }}</el-descriptions-item>
          <el-descriptions-item label="员工姓名">{{ detail.employeeName }}</el-descriptions-item>
          <el-descriptions-item label="岗位">{{ detail.jobTitle || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入职日期">{{ detail.entryDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系方式">{{ detail.mobile || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ detail.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="所属部门">
            {{ formatDeptNames(detail.deptNames) || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <dict-tag :options="dict.type.sys_normal_disable" :value="detail.status" />
          </el-descriptions-item>
          <el-descriptions-item label="账号绑定">
            <template v-if="detail.bindUser">
              <div class="bind-user">
                <span>{{ detail.bindUser.userName }}</span>
                <span v-if="detail.bindUser.nickName" class="text-muted">（{{ detail.bindUser.nickName }}）</span>
                <el-button
                  v-hasPermi="['system:employee:bind']"
                  type="text"
                  size="mini"
                  class="ml10"
                  @click="handleUnbind(detail)"
                >解绑</el-button>
              </div>
            </template>
            <template v-else>
              <span class="text-muted">未绑定</span>
              <el-button
                v-hasPermi="['system:employee:bind']"
                type="text"
                size="mini"
                class="ml10"
                @click="openBindDialog(detail)"
              >绑定账号</el-button>
            </template>
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ detail.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-drawer>

    <el-dialog
      title="绑定账号"
      :visible.sync="bindDialogVisible"
      width="420px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form :model="bindForm" label-width="90px">
        <el-form-item label="选择账号" prop="userId">
          <el-select
            v-model="bindForm.userId"
            filterable
            clearable
            remote
            reserve-keyword
            placeholder="请输入账号名称搜索"
            :remote-method="searchBindCandidates"
            :loading="bindCandidatesLoading"
          >
            <el-option
              v-for="user in bindCandidates"
              :key="user.userId"
              :label="formatUserOption(user)"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" :loading="bindLoading" @click="confirmBind">绑 定</el-button>
        <el-button @click="bindDialogVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listEmployee,
  getEmployee,
  addEmployee,
  updateEmployee,
  delEmployee,
  changeEmployeeStatus,
  exportEmployee,
  listEmployeeBindCandidates,
  bindEmployeeAccount,
  unbindEmployeeAccount,
  deptTreeSelect
} from '@/api/system/employee'
import Treeselect from '@riophae/vue-treeselect'
import '@riophae/vue-treeselect/dist/vue-treeselect.css'
import { Splitpanes, Pane } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'

export default {
  name: 'Employee',
  components: { Treeselect, Splitpanes, Pane },
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      employeeList: [],
      title: '',
      open: false,
      deptOptions: [],
      enabledDeptOptions: [],
      deptName: undefined,
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        employeeCode: undefined,
        employeeName: undefined,
        status: undefined,
        deptId: undefined
      },
      columns: {
        employeeCode: { label: '员工编号', visible: true },
        employeeName: { label: '员工姓名', visible: true },
        jobTitle: { label: '岗位', visible: true },
        deptNames: { label: '所属部门', visible: true },
        entryDate: { label: '入职日期', visible: true },
        mobile: { label: '联系方式', visible: true },
        bindAccount: { label: '绑定账号', visible: true },
        status: { label: '状态', visible: true }
      },
      form: {
        employeeId: undefined,
        employeeCode: undefined,
        employeeName: undefined,
        jobTitle: undefined,
        entryDate: undefined,
        mobile: undefined,
        email: undefined,
        status: '0',
        remark: undefined,
        deptIds: []
      },
      rules: {
        employeeCode: [
          { required: true, message: '员工编号不能为空', trigger: 'blur' },
          { min: 1, max: 64, message: '员工编号长度必须介于 1 和 64 之间', trigger: 'blur' }
        ],
        employeeName: [
          { required: true, message: '员工姓名不能为空', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        mobile: [
          {
            pattern: /^1[3-9]\d{9}$/,
            message: '请输入正确的手机号码',
            trigger: 'blur',
            transform(value) {
              return value || ''
            }
          }
        ],
        deptIds: [
          { type: 'array', required: true, message: '请选择所属部门', trigger: 'change' }
        ]
      },
      detailVisible: false,
      detailLoading: false,
      detail: null,
      bindDialogVisible: false,
      bindForm: {
        employeeId: undefined,
        userId: undefined
      },
      bindCandidates: [],
      bindCandidatesLoading: false,
      bindLoading: false
    }
  },
  watch: {
    deptName(val) {
      if (this.$refs.tree) {
        this.$refs.tree.filter(val)
      }
    }
  },
  created() {
    this.getList()
    this.getDeptTree()
  },
  methods: {
    getList() {
      this.loading = true
      listEmployee(this.queryParams).then(response => {
        this.employeeList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    getDeptTree() {
      deptTreeSelect().then(response => {
        this.deptOptions = response.data || []
        this.enabledDeptOptions = this.filterDisabledDept(JSON.parse(JSON.stringify(this.deptOptions || [])))
      })
    },
    filterDisabledDept(deptList) {
      return deptList.filter(dept => {
        if (dept.disabled) {
          return false
        }
        if (dept.children && dept.children.length) {
          dept.children = this.filterDisabledDept(dept.children)
        }
        return true
      })
    },
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    handleNodeClick(data) {
      this.queryParams.deptId = data.id
      this.handleQuery()
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      this.$modal.confirm('确认要"' + text + '""' + row.employeeName + '"员工吗？').then(() => {
        return changeEmployeeStatus(row.employeeId, row.status)
      }).then(() => {
        this.$modal.msgSuccess(text + '成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        employeeId: undefined,
        employeeCode: undefined,
        employeeName: undefined,
        jobTitle: undefined,
        entryDate: undefined,
        mobile: undefined,
        email: undefined,
        status: '0',
        remark: undefined,
        deptIds: []
      }
      this.resetForm('form')
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.queryParams.deptId = undefined
      this.deptName = undefined
      if (this.$refs.tree) {
        this.$refs.tree.setCurrentKey(null)
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.employeeId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = '新增员工'
    },
    handleUpdate(row) {
      const employeeId = row.employeeId || this.ids[0]
      if (!employeeId) {
        return
      }
      this.reset()
      getEmployee(employeeId).then(response => {
        const data = response.data || {}
        this.form = {
          employeeId: data.employeeId,
          employeeCode: data.employeeCode,
          employeeName: data.employeeName,
          jobTitle: data.jobTitle,
          entryDate: data.entryDate,
          mobile: data.mobile,
          email: data.email,
          status: data.status || '0',
          remark: data.remark,
          deptIds: data.deptIds || []
        }
        this.open = true
        this.title = '修改员工'
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) {
          return
        }
        const submit = this.form.employeeId ? updateEmployee : addEmployee
        submit(this.form).then(() => {
          this.$modal.msgSuccess(this.form.employeeId ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        })
      })
    },
    handleDelete(row) {
      const employeeIds = row.employeeId || this.ids
      if (!employeeIds || (Array.isArray(employeeIds) && !employeeIds.length)) {
        return
      }
      this.$modal.confirm('是否确认删除员工编号为"' + employeeIds + '"的数据项？').then(() => {
        return delEmployee(employeeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/employee/export', { ...this.queryParams }, `employee_${new Date().getTime()}.xlsx`)
    },
    formatDeptNames(deptNames) {
      if (!deptNames) {
        return ''
      }
      if (Array.isArray(deptNames)) {
        return deptNames.join('、')
      }
      return deptNames
    },
    openDetail(row) {
      this.detailVisible = true
      this.detailLoading = true
      getEmployee(row.employeeId).then(response => {
        this.detail = response.data || null
        this.detailLoading = false
      }).catch(() => {
        this.detailLoading = false
      })
    },
    openBindDialog(detail) {
      this.bindForm = {
        employeeId: detail.employeeId,
        userId: undefined
      }
      this.bindCandidates = []
      this.bindDialogVisible = true
    },
    searchBindCandidates(keyword) {
      this.bindCandidatesLoading = true
      listEmployeeBindCandidates({ keyword, employeeId: this.bindForm.employeeId }).then(response => {
        this.bindCandidates = response.data || response.rows || []
        this.bindCandidatesLoading = false
      }).catch(() => {
        this.bindCandidatesLoading = false
      })
    },
    confirmBind() {
      if (!this.bindForm.userId) {
        this.$modal.msgWarning('请选择需要绑定的账号')
        return
      }
      this.bindLoading = true
      bindEmployeeAccount(this.bindForm).then(() => {
        this.$modal.msgSuccess('绑定成功')
        this.bindLoading = false
        this.bindDialogVisible = false
        if (this.detailVisible) {
          this.refreshDetail()
        }
        this.getList()
      }).catch(() => {
        this.bindLoading = false
      })
    },
    handleUnbind(detail) {
      this.$modal.confirm('确认要解绑当前账号吗？').then(() => {
        return unbindEmployeeAccount(detail.employeeId)
      }).then(() => {
        this.$modal.msgSuccess('解绑成功')
        if (this.detailVisible) {
          this.refreshDetail()
        }
        this.getList()
      }).catch(() => {})
    },
    refreshDetail() {
      if (!this.detail) {
        return
      }
      this.detailLoading = true
      getEmployee(this.detail.employeeId).then(response => {
        this.detail = response.data || null
        this.detailLoading = false
      }).catch(() => {
        this.detailLoading = false
      })
    },
    formatUserOption(user) {
      if (!user) {
        return ''
      }
      const base = user.userName || ''
      if (user.nickName) {
        return `${base}（${user.nickName}）`
      }
      return base
    }
  }
}
</script>

<style scoped>
.employee-container .text-muted {
  color: #909399;
}
.employee-detail {
  padding-right: 8px;
}
.ml10 {
  margin-left: 10px;
}
</style>
