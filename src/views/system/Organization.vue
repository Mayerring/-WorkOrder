<template>
  <div class="organization-container">
    <el-row :gutter="20">
      <!-- 组织架构树 -->
      <el-col :span="6">
        <el-card class="tree-card" v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>组织架构</span>
              <el-button-group v-if="isAdmin">
                <el-button type="primary" size="small" @click="handleAddCompany">
                  <el-icon>
                    <Plus />
                  </el-icon>添加公司
                </el-button>
                <el-button type="primary" size="small" @click="handleAddDept">
                  <el-icon>
                    <Plus />
                  </el-icon>添加部门
                </el-button>
              </el-button-group>
            </div>
          </template>
          <div class="tree-container">
            <el-input v-model="filterText" placeholder="输入关键字进行过滤" clearable prefix-icon="Search" />
            <el-tree ref="deptTree" :data="deptData" :props="defaultProps" :filter-node-method="filterNode"
              node-key="id" default-expand-all highlight-current @node-click="handleNodeClick">
              <template #default="{ node, data }">
                <span class="custom-tree-node" :class="{ 'is-company': data.type === 'company' }">
                  <span>
                    <el-tag size="small" :type="data.type === 'company' ? 'primary' : 'info'" class="mr-1">
                      {{ data.type === 'company' ? '公司' : '部门' }}
                    </el-tag>
                    {{ node.label }}
                  </span>
                  <span class="actions" v-if="isAdmin">
                    <el-button link type="primary" @click.stop="handleEdit(data)">
                      <el-icon>
                        <Edit />
                      </el-icon>
                    </el-button>
                    <el-button link type="danger" @click.stop="handleDelete(node, data)">
                      <el-icon>
                        <Delete />
                      </el-icon>
                    </el-button>
                  </span>
                </span>
              </template>
            </el-tree>
          </div>
        </el-card>
      </el-col>

      <!-- 员工列表 -->
      <el-col :span="18">
        <el-card class="staff-card">
          <template #header>
            <div class="card-header">
              <span>{{ currentDept.type === 'company' ? currentDept.name + '员工列表' : '请选择公司' }}</span>
            </div>
          </template>

          <div class="table-container">
            <template v-if="currentDept.type === 'company'">
              <el-table :data="memberList" v-loading="loading" border style="width: 100%">
                <el-table-column prop="employeeId" label="工号" width="180" />
                <el-table-column prop="name" label="姓名" width="120" />
                <el-table-column prop="department" label="部门" width="120" />
                <el-table-column prop="position" label="职位" width="120" />
                <el-table-column prop="phone" label="手机号" width="120" />
                <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
                <el-table-column prop="status" label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                      {{ row.status === 'active' ? '在职' : '离职' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>

              <!-- 分页 -->
              <div class="pagination-container">
                <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
                  :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
                  @size-change="handleSizeChange" @current-change="handleCurrentChange" />
              </div>
            </template>
            <el-empty v-else description="请选择一个公司查看员工信息" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 部门对话框 -->
    <el-dialog v-model="deptDialog.visible" :title="deptDialog.type === 'add' ? '添加部门' : '编辑部门'" width="500px">
      <el-form :model="deptForm" label-width="100px">
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="deptForm.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="code">
          <el-input v-model="deptForm.code" placeholder="请输入部门编码" :disabled="deptDialog.type === 'edit'" />
        </el-form-item>
        <el-form-item label="负责人工号">
          <el-input v-model="deptForm.leaderNumber" placeholder="请输入负责人工号" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="deptForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="deptForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deptDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitDeptForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 公司表单对话框 -->
    <el-dialog v-model="companyDialog.visible" title="添加公司" width="500px">
      <el-form :model="companyForm" label-width="100px">
        <el-form-item label="公司名称" prop="name">
          <el-input v-model="companyForm.name" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="公司编码" prop="code">
          <el-input v-model="companyForm.code" placeholder="请输入公司编码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="companyDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitCompanyForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch, onMounted } from 'vue'
import { Plus, Edit, Delete, Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOrganization, getUserInfo, getStaffList } from '@/api/user'
import { addDepartment, updateDepartment, addCompany } from '@/api/admin'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = ref(false)

// 获取用户信息并设置权限
const getUserRole = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 1) {
      isAdmin.value = res.data.role === 'admin'
      userStore.setUserInfo(res.data)
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 部门树相关
const deptTree = ref(null)
const filterText = ref('')
const currentDept = ref({})
const deptData = ref([])
const loading = ref(false)

const defaultProps = {
  children: 'children',
  label: 'name'
}

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 成员列表
const memberList = ref([])

// 部门表单对话框
const deptDialog = reactive({
  visible: false,
  type: 'add' // add or edit
})

const deptForm = reactive({
  name: '',
  code: '',
  companyCode: '',
  leaderNumber: '',
  sort: 0,
  remark: ''
})

// 公司表单对话框
const companyDialog = reactive({
  visible: false
})

const companyForm = ref({
  name: '',
  code: '',
  level: 1
})

// 转换组织架构数据为树形结构
const transformOrganizationData = (data) => {
  const result = []

  // 处理所有公司
  data.forEach(item => {
    const company = {
      id: item.company.id,
      name: item.company.name,
      code: item.company.code,
      level: item.company.level,
      type: 'company',
      children: [] // 用于存储部门
    }

    // 处理部门
    if (item.departments && item.departments.length > 0) {
      company.children = item.departments.map(dept => ({
        id: dept.id,
        name: dept.name,
        code: dept.code,
        companyCode: dept.companyCode,
        leaderNumber: dept.leaderNumber,
        type: 'department'
      }))
    }

    result.push(company)
  })

  return result
}

// 加载组织架构数据
const loadOrganizationData = async () => {
  loading.value = true
  try {
    const res = await getOrganization()
    if (res.code === 1) {
      deptData.value = transformOrganizationData(res.data)
    } else {
      ElMessage.error(res.msg || '获取组织架构失败')
    }
  } catch (error) {
    console.error('加载组织架构失败:', error)
    ElMessage.error('加载组织架构失败')
  } finally {
    loading.value = false
  }
}

// 节点点击
const handleNodeClick = (data) => {
  if (data.type !== 'company') {
    return // 如果不是公司节点，直接返回
  }
  currentDept.value = data
  loadCompanyStaffList(data.code)
}

// 加载公司员工列表
const loadCompanyStaffList = async (companyCode) => {
  loading.value = true
  try {
    const res = await getStaffList({
      pageNum: currentPage.value,
      pageSize: pageSize.value,
      companyCode: companyCode
    })

    if (res.code === 1) {
      memberList.value = res.data.records.map(staff => ({
        ...staff,
        employeeId: staff.staffNumber,
        department: staff.department,
        status: staff.status === 0 ? 'active' : 'inactive'
      }))
      total.value = res.data.total
    } else {
      ElMessage.error(res.msg || '获取员工列表失败')
    }
  } catch (error) {
    console.error('加载员工列表失败:', error)
    ElMessage.error('加载员工列表失败')
  } finally {
    loading.value = false
  }
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  if (currentDept.value?.type === 'company') {
    loadCompanyStaffList(currentDept.value.code)
  }
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  if (currentDept.value?.type === 'company') {
    loadCompanyStaffList(currentDept.value.code)
  }
}

// 监听过滤文本
watch(filterText, (val) => {
  deptTree.value?.filter(val)
})

// 过滤节点
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}

// 添加部门
const handleAddDept = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以添加部门')
    return
  }
  if (!currentDept.value || currentDept.value.type !== 'company') {
    ElMessage.warning('请先选择一个公司')
    return
  }
  deptDialog.type = 'add'
  deptDialog.visible = true
  Object.assign(deptForm, {
    name: '',
    code: '',
    companyCode: currentDept.value.code,
    leaderNumber: '',
    sort: 0,
    remark: ''
  })
}

// 编辑部门
const handleEdit = (data) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以编辑')
    return
  }
  deptDialog.type = 'edit'
  deptDialog.visible = true
  Object.assign(deptForm, data)
}

// 删除节点
const handleDelete = (node, data) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以删除')
    return
  }
  ElMessageBox.confirm(
    '确认删除该节点吗？删除后无法恢复',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      // TODO: 根据类型调用不同的删除API
      if (data.type === 'company') {
        // 调用删除公司API
      } else {
        // 调用删除部门API
      }
      ElMessage.success('删除成功')
      loadOrganizationData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => { })
}

// 提交部门表单
const submitDeptForm = async () => {
  try {
    const api = deptDialog.type === 'add' ? addDepartment : updateDepartment
    const res = await api(deptForm)
    if (res.code === 1) {
      ElMessage.success(deptDialog.type === 'add' ? '添加部门成功' : '修改部门成功')
      deptDialog.visible = false
      loadOrganizationData()
    } else {
      ElMessage.error(res.msg || (deptDialog.type === 'add' ? '添加部门失败' : '修改部门失败'))
    }
  } catch (error) {
    console.error(deptDialog.type === 'add' ? '添加部门失败:' : '修改部门失败:', error)
    ElMessage.error(deptDialog.type === 'add' ? '添加部门失败' : '修改部门失败')
  }
}

// 添加公司
const handleAddCompany = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以添加公司')
    return
  }
  companyDialog.visible = true
  companyForm.value = {
    name: '',
    code: '',
    level: 1
  }
}

// 提交公司表单
const submitCompanyForm = async () => {
  try {
    const res = await addCompany(companyForm.value)
    if (res.code === 1) {
      ElMessage.success('添加公司成功')
      companyDialog.visible = false
      loadOrganizationData()
    } else {
      ElMessage.error(res.msg || '添加公司失败')
    }
  } catch (error) {
    console.error('添加公司失败:', error)
    ElMessage.error('添加公司失败')
  }
}

// 在组件挂载时加载数据
onMounted(async () => {
  await getUserRole()
  loadOrganizationData()
})
</script>

<style scoped>
.organization-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
  padding: 8px;
}

.el-row {
  flex: 1;
  display: flex;
  min-height: 0;
  overflow: hidden;
}

/* 左侧组织树卡片 */
.tree-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 树容器可滚动 */
.tree-container {
  flex: 1;
  overflow-y: auto;
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
}

/* 右侧员工卡片 */
.staff-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 表格容器撑满剩余空间并可滚动 */
.table-container {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
  margin-top: 10px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
}

/* 分页固定在底部 */
.pagination-container {
  margin-top: 16px;
  padding: 10px;
  background-color: #fff;
  border-radius: 4px;
  display: flex;
  justify-content: flex-end;
  flex-shrink: 0;
}

/* 自定义滚动条样式 */
:deep(.el-scrollbar__wrap) {
  overflow-x: hidden;
}

.tree-container::-webkit-scrollbar,
.table-container::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

.tree-container::-webkit-scrollbar-thumb,
.table-container::-webkit-scrollbar-thumb {
  border-radius: 3px;
  background: #c0c4cc;
}

.tree-container::-webkit-scrollbar-track,
.table-container::-webkit-scrollbar-track {
  border-radius: 3px;
  background: #f5f7fa;
}

/* 树节点和操作按钮样式 */
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}

.actions {
  visibility: hidden;
}

.el-tree-node__content:hover .actions {
  visibility: visible;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  white-space: nowrap;
  font-size: 16px;
  font-weight: bold;
}

.card-header .el-button-group {
  margin-left: auto;
  padding-left: 60px;
}

:deep(.el-card__body) {
  padding: 10px;
  height: 100%;
  overflow: hidden;
}

.el-input {
  margin-bottom: 10px;
}

:deep(.el-tree-select) {
  width: 100%;
}
</style>
