<template>
  <div class="role-management">
    <!-- 工具栏 -->
    <div class="toolbar" v-if="isAdmin">
      <el-button type="primary" @click="handleAdd">新增角色</el-button>
    </div>

    <!-- 角色列表 -->
    <el-table :data="tableData" style="width: 100%" v-loading="loading">
      <el-table-column prop="name" label="角色名称" width="180" />
      <el-table-column prop="code" label="角色标识" width="180" />
      <el-table-column prop="description" label="角色描述" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="250" fixed="right" v-if="isAdmin">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" link @click="handlePermission(row)">权限设置</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 角色表单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogType === 'add' ? '新增角色' : '编辑角色'" width="500px">
      <el-form :model="roleForm" label-width="100px" :rules="rules" ref="roleFormRef">
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" />
        </el-form-item>
        <el-form-item label="角色标识" prop="code">
          <el-input v-model="roleForm.code" :disabled="dialogType === 'edit'" />
        </el-form-item>
        <el-form-item label="角色描述" prop="description">
          <el-input v-model="roleForm.description" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 权限设置对话框 -->
    <el-dialog v-model="permissionDialogVisible" title="权限设置" width="600px">
      <el-tree ref="permissionTree" :data="permissionData" show-checkbox node-key="id"
        :default-checked-keys="checkedPermissions" :props="{ label: 'name' }" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePermissions">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const isAdmin = computed(() => userStore.role === 'admin')

// 表格数据
const loading = ref(false)
const tableData = ref([
  {
    id: 1,
    name: '超级管理员',
    code: 'SUPER_ADMIN',
    description: '系统最高权限角色',
    createTime: '2025-05-20 10:00:00'
  },
  {
    id: 2,
    name: '普通用户',
    code: 'NORMAL_USER',
    description: '普通用户角色',
    createTime: '2025-05-20 11:00:00'
  }
])

// 角色表单对话框
const dialogVisible = ref(false)
const dialogType = ref('add')
const roleFormRef = ref(null)
const roleForm = reactive({
  name: '',
  code: '',
  description: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色标识', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色标识只能包含大写字母和下划线', trigger: 'blur' }
  ]
}

// 权限设置对话框
const permissionDialogVisible = ref(false)
const currentRole = ref(null)
const permissionTree = ref(null)
const checkedPermissions = ref([])

// 权限数据
const permissionData = [
  {
    id: 1,
    name: '系统管理',
    children: [
      {
        id: 11,
        name: '用户管理',
        children: [
          { id: 111, name: '查看用户' },
          { id: 112, name: '新增用户' },
          { id: 113, name: '编辑用户' },
          { id: 114, name: '删除用户' }
        ]
      },
      {
        id: 12,
        name: '角色管理',
        children: [
          { id: 121, name: '查看角色' },
          { id: 122, name: '新增角色' },
          { id: 123, name: '编辑角色' },
          { id: 124, name: '删除角色' }
        ]
      }
    ]
  },
  {
    id: 2,
    name: '工单管理',
    children: [
      {
        id: 21,
        name: '工单操作',
        children: [
          { id: 211, name: '查看工单' },
          { id: 212, name: '创建工单' },
          { id: 213, name: '分派工单' },
          { id: 214, name: '处理工单' }
        ]
      }
    ]
  }
]

// 新增角色
const handleAdd = () => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以添加角色')
    return
  }
  dialogType.value = 'add'
  Object.keys(roleForm).forEach(key => {
    roleForm[key] = ''
  })
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以编辑角色')
    return
  }
  dialogType.value = 'edit'
  Object.keys(roleForm).forEach(key => {
    roleForm[key] = row[key]
  })
  dialogVisible.value = true
}

// 删除角色
const handleDelete = (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以删除角色')
    return
  }
  ElMessageBox.confirm(
    '确认删除该角色吗？',
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

// 打开权限设置
const handlePermission = (row) => {
  if (!isAdmin.value) {
    ElMessage.warning('只有管理员可以设置权限')
    return
  }
  currentRole.value = row
  // TODO: 获取角色的权限列表
  checkedPermissions.value = [111, 121, 211] // 示例数据
  permissionDialogVisible.value = true
}

// 保存权限设置
const savePermissions = () => {
  if (!permissionTree.value) return
  const checkedKeys = permissionTree.value.getCheckedKeys()
  const halfCheckedKeys = permissionTree.value.getHalfCheckedKeys()
  // TODO: 保存权限设置
  console.log('选中的权限：', [...checkedKeys, ...halfCheckedKeys])
  ElMessage.success('权限设置成功')
  permissionDialogVisible.value = false
}

// 提交表单
const submitForm = () => {
  if (!roleFormRef.value) return
  roleFormRef.value.validate((valid) => {
    if (valid) {
      // TODO: 实现表单提交
      ElMessage.success(dialogType.value === 'add' ? '添加成功' : '修改成功')
      dialogVisible.value = false
    }
  })
}
</script>

<style scoped>
.role-management {
  padding: 20px 0;
}

.toolbar {
  margin-bottom: 20px;
}

@media screen and (max-width: 768px) {
  .role-management {
    padding: 10px 0;
  }
}
</style>