<template>
  <div class="users-container">
    <el-card class="box-card">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-form :inline="true" :model="searchForm" class="search-form">
          <el-form-item label="用户名">
            <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable />
          </el-form-item>
          <el-form-item label="部门">
            <el-cascader v-model="searchForm.department" :options="departmentOptions" :props="{ checkStrictly: true }"
              clearable placeholder="请选择部门" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
              <el-option label="启用" value="active" />
              <el-option label="禁用" value="inactive" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button @click="resetSearch">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 工具栏 -->
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增用户</el-button>
        <el-button type="danger" :disabled="!selectedUsers.length" @click="handleBatchDelete">批量删除</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="tableData" @selection-change="handleSelectionChange" style="width: 100%" v-loading="loading">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="department" label="部门" width="180" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'active' ? 'success' : 'danger'">
              {{ row.status === 'active' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleChangeStatus(row)">
              {{ row.status === 'active' ? '禁用' : '启用' }}
            </el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]"
          :total="total" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 用户表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增用户' : '编辑用户'" width="500px">
      <el-form :model="userForm" label-width="100px" :rules="rules" ref="userFormRef">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="userForm.realName" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" v-if="dialogType === 'add'">
          <el-input v-model="userForm.confirmPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="部门" prop="department">
          <el-cascader v-model="userForm.department" :options="departmentOptions" :props="{ checkStrictly: true }" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role">
            <el-option label="管理员" value="admin" />
            <el-option label="普通用户" value="user" />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 搜索表单
const searchForm = reactive({
  username: '',
  realName: '',
  department: [],
  status: ''
})

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    username: 'admin',
    realName: '系统管理员',
    department: '技术部/开发组',
    role: '管理员',
    email: 'admin@example.com',
    status: 'active',
    createTime: '2025-05-20 10:00:00'
  },
  {
    id: 2,
    username: 'user1',
    realName: '张三',
    department: '运维部/运维组',
    role: '普通用户',
    email: 'zhangsan@example.com',
    status: 'active',
    createTime: '2025-05-20 11:00:00'
  }
])

// 部门选项
const departmentOptions = [
  {
    value: 'tech',
    label: '技术部',
    children: [
      {
        value: 'dev',
        label: '开发组'
      },
      {
        value: 'ops',
        label: '运维组'
      }
    ]
  },
  {
    value: 'support',
    label: '支持部',
    children: [
      {
        value: 'service',
        label: '客服组'
      }
    ]
  }
]

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(100)

// 选中的用户
const selectedUsers = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const userFormRef = ref(null)
const userForm = reactive({
  username: '',
  realName: '',
  password: '',
  confirmPassword: '',
  department: [],
  role: '',
  email: ''
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于 6 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== userForm.password) {
          callback(new Error('两次输入密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  department: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索功能
  console.log('搜索条件：', searchForm)
}

// 重置搜索
const resetSearch = () => {
  Object.keys(searchForm).forEach(key => {
    searchForm[key] = ''
  })
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

// 新增用户
const handleAdd = () => {
  dialogType.value = 'add'
  Object.keys(userForm).forEach(key => {
    userForm[key] = ''
  })
  dialogVisible.value = true
}

// 编辑用户
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(userForm).forEach(key => {
    if (key !== 'password' && key !== 'confirmPassword') {
      userForm[key] = row[key]
    }
  })
  dialogVisible.value = true
}

// 更改用户状态
const handleChangeStatus = (row) => {
  const action = row.status === 'active' ? '禁用' : '启用'
  ElMessageBox.confirm(
    `确认${action}该用户吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现状态更改
    row.status = row.status === 'active' ? 'inactive' : 'active'
    ElMessage.success(`${action}成功`)
  }).catch(() => { })
}

// 删除用户
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该用户吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现删除
    const index = tableData.value.findIndex(item => item.id === row.id)
    if (index > -1) {
      tableData.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  }).catch(() => { })
}

// 批量删除
const handleBatchDelete = () => {
  if (!selectedUsers.value.length) return
  ElMessageBox.confirm(
    `确认删除选中的 ${selectedUsers.value.length} 个用户吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 实现批量删除
    const ids = selectedUsers.value.map(item => item.id)
    tableData.value = tableData.value.filter(item => !ids.includes(item.id))
    ElMessage.success('删除成功')
  }).catch(() => { })
}

// 提交表单
const submitForm = () => {
  if (!userFormRef.value) return
  userFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 实现表单提交
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
      dialogVisible.value = false
    }
  })
}

// 分页操作
const handleSizeChange = (val) => {
  pageSize.value = val
  // TODO: 重新加载数据
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  // TODO: 重新加载数据
}
</script>

<style scoped>
.users-container {
  padding: 20px;
}

.search-area {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.toolbar {
  margin-bottom: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

@media screen and (max-width: 768px) {
  .users-container {
    padding: 10px;
  }

  .search-form {
    .el-form-item {
      margin-bottom: 10px;
    }
  }
}
</style>