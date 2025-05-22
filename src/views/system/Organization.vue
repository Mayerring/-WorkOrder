<template>
  <div class="organization-container">
    <el-row :gutter="20">
      <!-- 部门树 -->
      <el-col :span="6">
        <el-card class="tree-card">
          <template #header>
            <div class="card-header">
              <span>组织架构</span>
              <!-- <el-button-group>
                <el-button type="primary" size="small" @click="handleAddDept">
                  <el-icon>
                    <Plus />
                  </el-icon>添加
                </el-button>
                <el-button type="primary" size="small" @click="handleRefresh">
                  <el-icon>
                    <Refresh />
                  </el-icon>刷新
                </el-button>
              </el-button-group> -->
            </div>
          </template>
          <el-input v-model="filterText" placeholder="输入关键字进行过滤" clearable prefix-icon="Search" />
          <el-tree ref="deptTree" :data="deptData" :props="defaultProps" :filter-node-method="filterNode" node-key="id"
            default-expand-all highlight-current @node-click="handleNodeClick">
            <template #default="{ node, data }">
              <span class="custom-tree-node">
                <span>{{ node.label }}</span>
                <span class="actions">
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
        </el-card>
      </el-col>

      <!-- 部门成员 -->
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>{{ currentDept.name || '所有成员' }}</span>
              <!-- <el-button-group>
                <el-button type="primary" size="small" @click="handleAddMember">
                  <el-icon>
                    <Plus />
                  </el-icon>添加成员
                </el-button>
                <el-button type="success" size="small" @click="handleImport">
                  <el-icon>
                    <Upload />
                  </el-icon>导入
                </el-button>
                <el-button type="primary" size="small" @click="handleExport">
                  <el-icon>
                    <Download />
                  </el-icon>导出
                </el-button>
              </el-button-group> -->
            </div>
          </template>

          <!-- 搜索栏 -->
          <el-form :inline="true" :model="searchForm" class="search-form">
            <el-form-item label="关键字">
              <el-input v-model="searchForm.keyword" placeholder="姓名/工号" clearable @keyup.enter="handleSearch" />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择" clearable>
                <el-option label="在职" value="active" />
                <el-option label="离职" value="inactive" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSearch">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 成员表格 -->
          <el-table :data="memberList" v-loading="loading" border style="width: 100%">
            <el-table-column type="selection" width="55" />
            <el-table-column prop="employeeId" label="工号" width="100" />
            <el-table-column prop="name" label="姓名" width="120" />
            <el-table-column prop="department" label="部门" />
            <el-table-column prop="position" label="职位" width="120" />
            <el-table-column prop="email" label="邮箱" min-width="200" show-overflow-tooltip />
            <el-table-column prop="phone" label="手机号" width="120" />
            <el-table-column prop="status" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'active' ? 'success' : 'info'">
                  {{ row.status === 'active' ? '在职' : '离职' }}
                </el-tag>
              </template>
            </el-table-column>
            <!-- <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleEditMember(row)">
                  编辑
                </el-button>
                <el-button link type="danger" @click="handleDeleteMember(row)">
                  删除
                </el-button>
              </template>
            </el-table-column> -->
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container">
            <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange" @current-change="handleCurrentChange" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 部门对话框 -->
    <el-dialog v-model="deptDialog.visible" :title="deptDialog.type === 'add' ? '添加部门' : '编辑部门'" width="500px">
      <el-form ref="deptFormRef" :model="deptForm" :rules="deptRules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select v-model="deptForm.parentId" :data="deptData" :props="defaultProps" placeholder="请选择上级部门"
            check-strictly />
        </el-form-item>
        <el-form-item label="部门名称" prop="name">
          <el-input v-model="deptForm.name" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="deptForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-select v-model="deptForm.leaderId" placeholder="请选择负责人" filterable remote :remote-method="remoteSearch">
            <el-option v-for="item in leaderOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="deptForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="deptDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitDept">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 成员对话框 -->
    <el-dialog v-model="memberDialog.visible" :title="memberDialog.type === 'add' ? '添加成员' : '编辑成员'" width="600px">
      <el-form ref="memberFormRef" :model="memberForm" :rules="memberRules" label-width="100px">
        <el-form-item label="工号" prop="employeeId">
          <el-input v-model="memberForm.employeeId" placeholder="请输入工号" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="memberForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="departmentId">
          <el-tree-select v-model="memberForm.departmentId" :data="deptData" :props="defaultProps"
            placeholder="请选择部门" />
        </el-form-item>
        <el-form-item label="职位" prop="position">
          <el-input v-model="memberForm.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="memberForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="memberForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="memberForm.status">
            <el-radio label="active">在职</el-radio>
            <el-radio label="inactive">离职</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="memberDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="submitMember">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { Plus, Edit, Delete, Search, Refresh, Upload, Download } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 部门树相关
const deptTree = ref(null)
const filterText = ref('')
const currentDept = ref({})
const deptData = ref([
  {
    id: 1,
    label: '总公司',
    children: [
      {
        id: 2,
        label: '技术部',
        children: [
          { id: 5, label: '开发组' },
          { id: 6, label: '测试组' }
        ]
      },
      {
        id: 3,
        label: '运维部'
      },
      {
        id: 4,
        label: '产品部'
      }
    ]
  }
])

const defaultProps = {
  children: 'children',
  label: 'label'
}

// 搜索相关
const loading = ref(false)
const searchForm = reactive({
  keyword: '',
  status: ''
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 成员列表
const memberList = ref([
  {
    employeeId: 'EMP001',
    name: '张三',
    department: '技术部/开发组',
    position: '高级工程师',
    email: 'zhangsan@example.com',
    phone: '13800138000',
    status: 'active'
  }
])

// 部门表单
const deptDialog = reactive({
  visible: false,
  type: 'add' // add or edit
})

const deptForm = reactive({
  parentId: null,
  name: '',
  sort: 0,
  leaderId: null,
  remark: ''
})

const deptRules = {
  name: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ]
}

// 成员表单
const memberDialog = reactive({
  visible: false,
  type: 'add' // add or edit
})

const memberForm = reactive({
  employeeId: '',
  name: '',
  departmentId: null,
  position: '',
  email: '',
  phone: '',
  status: 'active'
})

const memberRules = {
  employeeId: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// 负责人选项
const leaderOptions = ref([])

// 监听过滤文本
watch(filterText, (val) => {
  deptTree.value?.filter(val)
})

// 过滤节点
const filterNode = (value, data) => {
  if (!value) return true
  return data.label.includes(value)
}

// 节点点击
const handleNodeClick = (data) => {
  currentDept.value = data
  // TODO: 加载该部门的成员
}

// 刷新
const handleRefresh = () => {
  // TODO: 重新加载部门树
  ElMessage.success('刷新成功')
}

// 搜索
const handleSearch = () => {
  // TODO: 实现搜索
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.status = ''
  handleSearch()
}

// 远程搜索
const remoteSearch = (query) => {
  if (query) {
    // TODO: 实现远程搜索
    leaderOptions.value = [
      { id: 1, name: '张三' },
      { id: 2, name: '李四' }
    ]
  }
}

// 添加部门
const handleAddDept = () => {
  deptDialog.type = 'add'
  deptDialog.visible = true
  deptForm.parentId = currentDept.value.id
}

// 编辑部门
const handleEdit = (data) => {
  deptDialog.type = 'edit'
  deptDialog.visible = true
  Object.assign(deptForm, data)
}

// 删除部门
const handleDelete = (node, data) => {
  ElMessageBox.confirm(
    '确认删除该部门吗？删除后无法恢复',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用删除API
    ElMessage.success('删除成功')
  })
}

// 提交部门表单
const submitDept = () => {
  // TODO: 提交部门表单
  deptDialog.visible = false
  ElMessage.success('保存成功')
}

// 添加成员
const handleAddMember = () => {
  memberDialog.type = 'add'
  memberDialog.visible = true
  memberForm.departmentId = currentDept.value.id
}

// 编辑成员
const handleEditMember = (row) => {
  memberDialog.type = 'edit'
  memberDialog.visible = true
  Object.assign(memberForm, row)
}

// 删除成员
const handleDeleteMember = (row) => {
  ElMessageBox.confirm(
    '确认删除该成员吗？删除后无法恢复',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // TODO: 调用删除API
    ElMessage.success('删除成功')
  })
}

// 提交成员表单
const submitMember = () => {
  // TODO: 提交成员表单
  memberDialog.visible = false
  ElMessage.success('保存成功')
}

// 导入导出
const handleImport = () => {
  // TODO: 实现导入功能
}

const handleExport = () => {
  // TODO: 实现导出功能
}

// 分页
const handleSizeChange = (val) => {
  pageSize.value = val
  handleSearch()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  handleSearch()
}
</script>

<style scoped>
.organization-container {
  padding: 20px;
}

.tree-card {
  height: calc(100vh - 140px);
}

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

.search-form {
  margin-bottom: 20px;
  height: 40px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-tree-select) {
  width: 100%;
}

:deep(.el-card__body) {
  padding-top: 10px;
}

.el-input {
  margin-bottom: 10px;
}
</style>