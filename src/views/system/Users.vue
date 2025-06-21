<template>
  <div class="users-container">
    <!-- 搜索表单 -->
    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item label="工号">
        <el-input v-model="queryParams.staffNumber" placeholder="请输入工号" clearable @keyup.enter="handleQuery"
          @clear="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名">
        <el-input v-model="queryParams.name" placeholder="请输入姓名" clearable @keyup.enter="handleQuery"
          @clear="handleQuery" />
      </el-form-item>
      <el-form-item label="部门">
        <el-input v-model="queryParams.department" placeholder="请输入部门" clearable @keyup.enter="handleQuery"
          @clear="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px;"
          @change="handleQuery">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery">查询</el-button>
        <el-button @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格工具栏 -->
    <div class="table-toolbar" v-if="isAdmin">
      <el-button type="primary" @click="handleAdd">新增员工</el-button>
    </div>


    <!-- 员工表格 -->
    <div class="table-container">
      <el-table v-loading="loading" :data="tableData" style="width: 100%">
        <el-table-column prop="staffNumber" label="工号" width="210" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="email" label="邮箱" width="190" />
        <el-table-column prop="company" label="公司" width="150" />
        <el-table-column prop="department" label="部门" width="150" />
        <el-table-column prop="position" label="职位" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" v-if="isAdmin">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 员工表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增员工' : '编辑员工'" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="工号" prop="staffNumber">
          <el-input v-model="form.staffNumber" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="公司" prop="companyName">
          <el-input v-model="form.companyName" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentName">
          <el-input v-model="form.departmentName" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="form.position" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStaffList, getUserInfo } from '@/api/user'
import { addStaff, deleteStaff, updateStaff } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = ref(false)

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,  // 设置一个较大的初始值
  staffNumber: '',
  name: '',
  department: '',
  status: ''
})

// 状态选项
const statusOptions = [
  { value: 0, label: '正常' },
  { value: 1, label: '休假' },
  { value: 2, label: '停职' },
  { value: 3, label: '离职' }
]

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogType = ref('add')
const formRef = ref(null)

// 表单数据
const form = reactive({
  name: '',
  companyName: '',
  departmentName: '',
  position: '',
  phone: '',
  email: '',
  status: 0
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  companyName: [
    { required: true, message: '请输入公司名称', trigger: 'blur' }
  ],
  departmentName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ],
  position: [
    { required: true, message: '请输入职位', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    0: 'success',
    1: 'warning',
    2: 'danger',
    3: 'info'
  }
  return map[status] || 'info'
}

// 获取状态标签
const getStatusLabel = (status) => {
  const map = {
    0: '正常',
    1: '休假',
    2: '停职',
    3: '离职'
  }
  return map[status] || '未知'
}

// 获取用户信息并设置权限
const getUserRole = async () => {
  try {
    const res = await getUserInfo()
    console.log('获取用户信息返回:', res)
    if (res.code === 1) {
      isAdmin.value = res.data.role === 'admin'
      console.log('当前用户是否为管理员:', isAdmin.value)
      // 存储用户信息到store
      userStore.setUserInfo(res.data)
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    // 构建查询参数，确保所有参数都正确传递
    const params = {
      pageNum: queryParams.pageNum,
      pageSize: queryParams.pageSize
    }

    // 处理筛选条件，确保空值不会影响查询
    if (queryParams.staffNumber && queryParams.staffNumber.trim() !== '') {
      params.staffNumber = queryParams.staffNumber.trim()
    }
    if (queryParams.name && queryParams.name.trim() !== '') {
      params.name = queryParams.name.trim()
    }
    if (queryParams.department && queryParams.department.trim() !== '') {
      params.department = queryParams.department.trim()
    }
    if (queryParams.status !== '' && queryParams.status !== null && queryParams.status !== undefined) {
      params.status = queryParams.status
    }

    console.log('发送到后端的查询参数：', params)
    const res = await getStaffList(params)
    console.log('后端返回数据：', res)

    if (res.code === 1) {
      console.log('查询成功，记录数：', res.data.records.length)
      tableData.value = res.data.records.map(item => ({
        ...item,
        key: item.id
      }))
      total.value = res.data.total || 0
      console.log('处理后的表格数据:', tableData.value)

      // 显示查询结果提示
      if (hasActiveFilters.value) {
        ElMessage.success(`查询完成，共找到 ${total.value} 条记录`)
      }
    } else {
      console.error('查询失败，错误信息：', res.msg)
      ElMessage.error(res.msg || '查询失败')
    }
  } catch (error) {
    console.error('查询员工列表失败：', error)
    ElMessage.error('查询失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 查询按钮
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮
const resetQuery = () => {
  // 重置查询条件
  queryParams.pageNum = 1
  queryParams.pageSize = 10
  queryParams.staffNumber = ''
  queryParams.name = ''
  queryParams.department = ''
  queryParams.status = ''

  // 重新加载所有数据
  getList()
  ElMessage.success('筛选条件已重置')
}

// 清除单个筛选条件
const clearFilter = (field) => {
  queryParams[field] = ''
  queryParams.pageNum = 1
  getList()
  // ElMessage.success(`已清除${getFieldLabel(field)}筛选条件`)
}

// 获取字段标签
const getFieldLabel = (field) => {
  const labels = {
    staffNumber: '工号',
    name: '姓名',
    department: '部门',
    status: '状态'
  }
  return labels[field] || field
}

// 新增按钮
const handleAdd = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以添加员工')
    return
  }
  dialogType.value = 'add'
  Object.assign(form, {
    name: '',
    companyName: '',
    departmentName: '',
    position: '',
    phone: '',
    email: '',
    status: 0
  })
  dialogVisible.value = true
}

// 编辑按钮
const handleEdit = (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以编辑员工信息')
    return
  }
  dialogType.value = 'edit'
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除按钮
const handleDelete = (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以删除员工')
    return
  }

  console.log('当前用户角色:', isAdmin.value)
  console.log('要删除的员工信息:', row)

  ElMessageBox.confirm(
    '确认删除该员工吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      console.log('开始删除员工，ID:', row.id)
      const res = await deleteStaff(row.id)
      console.log('删除接口返回:', res)

      if (res.code === 1) {
        ElMessage.success('删除成功')
        getList()
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      console.error('删除失败，详细错误:', error)
      if (error.response) {
        console.error('服务器响应:', error.response.data)
        ElMessage.error(error.response.data.msg || '删除失败，服务器返回错误')
      } else if (error.request) {
        console.error('未收到响应:', error.request)
        ElMessage.error('删除失败，未收到服务器响应')
      } else {
        console.error('请求配置错误:', error.message)
        ElMessage.error('删除失败，请求配置错误')
      }
    }
  }).catch(() => {
    console.log('用户取消删除操作')
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以操作')
    return
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const api = dialogType.value === 'add' ? addStaff : updateStaff
        const res = await api(form)
        if (res.code === 1) {
          ElMessage.success(dialogType.value === 'add' ? '新增成功' : '修改成功')
          dialogVisible.value = false
          getList()
        } else {
          ElMessage.error(res.msg || (dialogType.value === 'add' ? '新增失败' : '修改失败'))
        }
      } catch (error) {
        console.error('提交失败：', error)
        ElMessage.error('提交失败，请稍后重试')
      }
    }
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  queryParams.pageNum = 1
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 计算属性：判断是否有活跃的筛选条件
const hasActiveFilters = computed(() => {
  return queryParams.staffNumber ||
    queryParams.name ||
    queryParams.department ||
    (queryParams.status !== '' && queryParams.status !== null && queryParams.status !== undefined)
})

// 在组件挂载时加载数据
onMounted(async () => {
  await getUserRole() // 先获取用户角色
  getList()
})
</script>

<style scoped>
.users-container {
  padding: 8px;
  background: #fff;
  border-radius: 4px;
  height: 98%;
}

.search-form {
  background-color: #fff;
  padding: 8px;
  border-radius: 4px;
}

.table-toolbar {
  margin-bottom: 2px;
}

.table-container {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 3px;
  flex: 0.5;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  height: 100% !important;
}

/* 
.filter-info {
  margin-bottom: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.filter-info .el-tag {
  margin-right: 8px;
}

.filter-info span {
  margin-right: 12px;
  font-weight: 500;
}

.filter-title {
  margin-bottom: 8px;
  font-weight: 500;
}

.filter-tags {
  display: flex;
  flex-wrap: wrap;
}

.filter-tags .el-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.debug-info {
  margin-top: 10px;
  padding: 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
} */
</style>