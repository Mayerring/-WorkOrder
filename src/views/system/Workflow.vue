<template>
  <div class="workflow-settings">
    <!-- 工具栏 -->
    <div class="toolbar">
      <el-button type="primary" @click="handleAdd">新增工作流</el-button>
    </div>

    <!-- 工作流列表 -->
    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="工作流名称" width="180" />
      <el-table-column prop="type" label="工单类型" width="120">
        <template #default="{ row }">
          <el-tag :type="getTypeTag(row.type)">{{ getTypeName(row.type) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'active' ? 'success' : 'info'">
            {{ row.status === 'active' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" link @click="handleConfig(row)">流程配置</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 工作流表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增工作流' : '编辑工作流'" width="500px">
      <el-form :model="workflowForm" label-width="100px" :rules="rules" ref="workflowFormRef">
        <el-form-item label="工作流名称" prop="name">
          <el-input v-model="workflowForm.name" />
        </el-form-item>
        <el-form-item label="工单类型" prop="type">
          <el-select v-model="workflowForm.type" placeholder="请选择工单类型">
            <el-option label="故障报修" value="fault" />
            <el-option label="需求申请" value="requirement" />
            <el-option label="运维服务" value="operation" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="workflowForm.description" type="textarea" rows="3" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="workflowForm.status" :active-value="'active'" :inactive-value="'inactive'"
            active-text="启用" inactive-text="停用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 流程配置对话框 -->
    <el-dialog v-model="configDialogVisible" title="流程配置" width="800px">
      <div class="workflow-config">
        <div class="node-list">
          <div class="node-title">节点列表</div>
          <draggable v-model="workflowNodes" item-key="id" handle=".el-card" ghost-class="ghost" @end="handleDragEnd">
            <template #item="{ element }">
              <div class="node-item">
                <el-card :body-style="{ padding: '10px' }">
                  <div class="node-content">
                    <span>{{ element.name }}</span>
                    <div class="node-actions">
                      <el-button type="primary" link @click="handleEditNode(element)">编辑</el-button>
                      <el-button type="danger" link @click="handleDeleteNode(element)">删除</el-button>
                    </div>
                  </div>
                </el-card>
              </div>
            </template>
          </draggable>
          <el-button type="primary" plain class="add-node-btn" @click="handleAddNode">添加节点</el-button>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="configDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveWorkflowConfig">保存配置</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 节点配置对话框 -->
    <el-dialog v-model="nodeDialogVisible" :title="nodeDialogType === 'add' ? '添加节点' : '编辑节点'" width="500px">
      <el-form :model="nodeForm" label-width="100px" :rules="nodeRules" ref="nodeFormRef">
        <el-form-item label="节点名称" prop="name">
          <el-input v-model="nodeForm.name" />
        </el-form-item>
        <el-form-item label="处理角色" prop="roles">
          <el-select v-model="nodeForm.roles" multiple placeholder="请选择处理角色">
            <el-option v-for="role in roleOptions" :key="role.value" :label="role.label" :value="role.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理时限" prop="timeLimit">
          <el-input-number v-model="nodeForm.timeLimit" :min="1" :max="72" />
          <span class="unit">小时</span>
        </el-form-item>
        <el-form-item label="是否可转派" prop="canReassign">
          <el-switch v-model="nodeForm.canReassign" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="nodeForm.remark" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="nodeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitNodeForm">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import draggable from 'vuedraggable'

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    name: '故障报修工作流',
    type: 'fault',
    description: '处理故障报修工单的标准流程',
    status: 'active',
    createTime: '2025-05-20 10:00:00'
  },
  {
    id: 2,
    name: '需求申请工作流',
    type: 'requirement',
    description: '处理需求申请工单的标准流程',
    status: 'active',
    createTime: '2025-05-20 11:00:00'
  }
])

// 工作流表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const workflowFormRef = ref(null)
const workflowForm = reactive({
  name: '',
  type: '',
  description: '',
  status: 'active'
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入工作流名称', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择工单类型', trigger: 'change' }
  ]
}

// 工单类型标签
const getTypeTag = (type) => {
  const map = {
    fault: 'danger',
    requirement: 'warning',
    operation: 'success'
  }
  return map[type]
}

// 工单类型名称
const getTypeName = (type) => {
  const map = {
    fault: '故障报修',
    requirement: '需求申请',
    operation: '运维服务'
  }
  return map[type]
}

// 流程配置对话框
const configDialogVisible = ref(false)
const currentWorkflow = ref(null)
const workflowNodes = ref([
  {
    id: 1,
    name: '提交申请',
    roles: ['USER'],
    timeLimit: 24,
    canReassign: false,
    remark: '用户提交工单'
  },
  {
    id: 2,
    name: '部门审批',
    roles: ['DEPT_MANAGER'],
    timeLimit: 48,
    canReassign: true,
    remark: '部门经理审批'
  },
  {
    id: 3,
    name: '运维处理',
    roles: ['OPS'],
    timeLimit: 72,
    canReassign: true,
    remark: '运维人员处理'
  }
])

// 节点配置对话框
const nodeDialogVisible = ref(false)
const nodeDialogType = ref('add')
const nodeFormRef = ref(null)
const nodeForm = reactive({
  name: '',
  roles: [],
  timeLimit: 24,
  canReassign: false,
  remark: ''
})

// 节点表单验证规则
const nodeRules = {
  name: [
    { required: true, message: '请输入节点名称', trigger: 'blur' }
  ],
  roles: [
    { required: true, message: '请选择处理角色', trigger: 'change' }
  ],
  timeLimit: [
    { required: true, message: '请输入处理时限', trigger: 'blur' }
  ]
}

// 角色选项
const roleOptions = [
  { value: 'USER', label: '普通用户' },
  { value: 'DEPT_MANAGER', label: '部门经理' },
  { value: 'OPS', label: '运维人员' },
  { value: 'ADMIN', label: '系统管理员' }
]

// 新增工作流
const handleAdd = () => {
  dialogType.value = 'add'
  Object.keys(workflowForm).forEach(key => {
    workflowForm[key] = key === 'status' ? 'active' : ''
  })
  dialogVisible.value = true
}

// 编辑工作流
const handleEdit = (row) => {
  dialogType.value = 'edit'
  Object.keys(workflowForm).forEach(key => {
    workflowForm[key] = row[key]
  })
  dialogVisible.value = true
}

// 删除工作流
const handleDelete = (row) => {
  ElMessageBox.confirm(
    '确认删除该工作流吗？',
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

// 打开流程配置
const handleConfig = (row) => {
  currentWorkflow.value = row
  // TODO: 获取工作流节点配置
  configDialogVisible.value = true
}

// 节点拖拽结束
const handleDragEnd = () => {
  // TODO: 保存节点顺序
}

// 添加节点
const handleAddNode = () => {
  nodeDialogType.value = 'add'
  Object.keys(nodeForm).forEach(key => {
    nodeForm[key] = key === 'timeLimit' ? 24 : key === 'canReassign' ? false : key === 'roles' ? [] : ''
  })
  nodeDialogVisible.value = true
}

// 编辑节点
const handleEditNode = (node) => {
  nodeDialogType.value = 'edit'
  Object.keys(nodeForm).forEach(key => {
    nodeForm[key] = node[key]
  })
  nodeDialogVisible.value = true
}

// 删除节点
const handleDeleteNode = (node) => {
  ElMessageBox.confirm(
    '确认删除该节点吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    const index = workflowNodes.value.findIndex(item => item.id === node.id)
    if (index > -1) {
      workflowNodes.value.splice(index, 1)
    }
    ElMessage.success('删除成功')
  }).catch(() => { })
}

// 提交工作流表单
const submitForm = () => {
  if (!workflowFormRef.value) return
  workflowFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 实现表单提交
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
      dialogVisible.value = false
    }
  })
}

// 提交节点表单
const submitNodeForm = () => {
  if (!nodeFormRef.value) return
  nodeFormRef.value.validate((valid) => {
    if (valid) {
      if (nodeDialogType.value === 'add') {
        workflowNodes.value.push({
          id: Date.now(),
          ...nodeForm
        })
      } else {
        const index = workflowNodes.value.findIndex(node => node.id === nodeForm.id)
        if (index > -1) {
          workflowNodes.value[index] = { ...nodeForm }
        }
      }
      ElMessage.success(nodeDialogType.value === 'add' ? '添加成功' : '修改成功')
      nodeDialogVisible.value = false
    }
  })
}

// 保存工作流配置
const saveWorkflowConfig = () => {
  // TODO: 保存工作流配置
  ElMessage.success('保存成功')
  configDialogVisible.value = false
}
</script>

<style scoped>
.workflow-settings {
  padding: 20px 0;
}

.toolbar {
  margin-bottom: 20px;
}

.workflow-config {
  min-height: 400px;
}

.node-list {
  border: 1px solid var(--el-border-color-light);
  border-radius: 4px;
  padding: 20px;
}

.node-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
}

.node-item {
  margin-bottom: 10px;
}

.node-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.node-actions {
  display: flex;
  gap: 10px;
}

.add-node-btn {
  margin-top: 20px;
  width: 100%;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}

.unit {
  margin-left: 10px;
}

@media screen and (max-width: 768px) {
  .workflow-settings {
    padding: 10px 0;
  }
}
</style>