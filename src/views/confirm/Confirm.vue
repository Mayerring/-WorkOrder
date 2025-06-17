<template>
  <div class="confirm-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待确认" name="pending">
        <el-table :data="pendingList" style="width: 100%" v-loading="loading">
          <el-table-column prop="code" label="工单编号" width="180" />
          <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="type" label="工单类型" width="120">
            <template #default="{ row }">
              <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitterInfo.userName" label="申请人" width="120" />
          <el-table-column prop="submitterInfo.departmentName" label="申请部门" width="150" />
          <el-table-column prop="handlerInfo" label="处理人" width="120">
            <template #default="{ row }">
              <span v-if="row.handlerInfo && row.handlerInfo.length > 0">
                {{ row.handlerInfo[row.handlerInfo.length - 1].userName }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="deadlineTime" label="截止时间" width="180" />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleConfirm(row)">确认</el-button>
              <el-button type="info" link @click="viewDetail(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="已确认" name="finished">
        <el-table :data="finishedList" style="width: 100%" v-loading="loading">
          <el-table-column prop="code" label="工单编号" width="180" />
          <el-table-column prop="title" label="工单标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="type" label="工单类型" width="120">
            <template #default="{ row }">
              <el-tag :type="row.type === 0 ? 'primary' : 'warning'">{{ row.typeDesc }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="submitterInfo.userName" label="申请人" width="120" />
          <el-table-column prop="submitterInfo.departmentName" label="申请部门" width="150" />
          <el-table-column prop="handlerInfo" label="处理人" width="120">
            <template #default="{ row }">
              <span v-if="row.handlerInfo && row.handlerInfo.length > 0">
                {{ row.handlerInfo[row.handlerInfo.length - 1].userName }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="checkerInfo" label="确认结果" width="120">
            <template #default="{ row }">
              <el-tag v-if="row.checkerInfo" :type="getConfirmResultType(row.checkerInfo.finished)">
                {{ getConfirmResultText(row.checkerInfo.finished) }}
              </el-tag>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="checkerInfo" label="确认时间" width="180">
            <template #default="{ row }">
              <span v-if="row.checkerInfo && row.checkerInfo.handleTime">
                {{ row.checkerInfo.handleTime }}
              </span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button type="info" link @click="viewDetail(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 分页器 -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="pagination.pageNum" v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]" :total="pagination.total" layout="total, sizes, prev, pager, next"
        @size-change="handleSizeChange" @current-change="handlePageChange" />
    </div>

    <!-- 确认对话框 -->
    <el-dialog v-model="confirmDialogVisible" title="工单确认" width="500px">
      <el-form :model="confirmForm" label-width="100px">
        <el-form-item label="确认结果">
          <el-radio-group v-model="confirmForm.isConfirmed">
            <el-radio :value="true">确认完成</el-radio>
            <el-radio :value="false">仍有问题</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="确认意见">
          <el-input v-model="confirmForm.remark" type="textarea" rows="4" placeholder="请输入确认意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="confirmDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitConfirm" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getWorkOrderPage, handleWorkOrder } from '@/api/workorder'
import { getUserInfo } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 标签页激活状态
const activeTab = ref('pending')

// 表格数据
const loading = ref(false)
const pendingList = ref([])
const finishedList = ref([])
const userId = ref(null)

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 确认对话框
const confirmDialogVisible = ref(false)
const submitLoading = ref(false)
const confirmForm = reactive({
  id: null,
  code: '',
  isConfirmed: true, // 确认结果：true-确认完成，false-仍有问题
  handleType: 5, // 5: 确认完成
  remark: ''
})
const currentRow = ref(null)

// 获取当前用户信息
const getCurrentUserInfo = async () => {
  try {
    const res = await getUserInfo()
    if (res.code === 1) {
      userId.value = Number(res.data.userId)
    } else {
      ElMessage.error(res.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败：', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 获取确认结果类型
const getConfirmResultType = (finished) => {
  return finished ? 'success' : 'danger'
}

// 获取确认结果文本
const getConfirmResultText = (finished) => {
  return finished ? '确认完成' : '仍有问题'
}

// 加载确认工单列表
const loadConfirmList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      checkerInfo: {
        userId: userId.value,
        finished: activeTab.value === 'pending' ? false : true // 待确认：false，已确认：true
      }
    }

    const res = await getWorkOrderPage(params)
    if (res.code === 1) {
      const { records, total, current, size } = res.data

      // 筛选当前用户需要确认的工单
      const userConfirmOrders = records.filter(order =>
        order.checkerInfo?.userId === userId.value
      )

      // 根据 finished 状态分类
      if (activeTab.value === 'pending') {
        pendingList.value = userConfirmOrders.filter(order =>
          !order.checkerInfo.finished
        )
      } else {
        finishedList.value = userConfirmOrders.filter(order =>
          order.checkerInfo.finished
        )
      }

      pagination.total = total
      pagination.pageNum = current
      pagination.pageSize = size
    } else {
      ElMessage.error(res.msg || '获取确认工单列表失败')
    }
  } catch (error) {
    console.error('获取确认工单列表失败：', error)
    ElMessage.error('获取确认工单列表失败')
  } finally {
    loading.value = false
  }
}

// 处理分页变化
const handleSizeChange = (size) => {
  pagination.pageSize = size
  pagination.pageNum = 1
  loadConfirmList()
}

const handlePageChange = (page) => {
  pagination.pageNum = page
  loadConfirmList()
}

// 确认操作
const handleConfirm = (row) => {
  currentRow.value = row
  confirmForm.id = row.id
  confirmForm.code = row.code
  confirmForm.isConfirmed = true // 默认选择确认完成
  confirmForm.handleType = 5 // 确认完成
  confirmForm.remark = ''
  confirmDialogVisible.value = true
}

// 提交确认
const submitConfirm = async () => {
  if (!confirmForm.remark.trim()) {
    ElMessage.warning('请输入确认意见')
    return
  }

  submitLoading.value = true
  try {
    const params = {
      id: confirmForm.id,
      code: confirmForm.code,
      handleType: confirmForm.isConfirmed ? 5 : 6, // 5: 确认完成，6: 仍有问题
      remark: confirmForm.remark
    }

    const res = await handleWorkOrder(params)
    if (res.code === 1) {
      ElMessage.success('确认提交成功')
      confirmDialogVisible.value = false
      loadConfirmList() // 重新加载列表
    } else {
      ElMessage.error(res.msg || '确认提交失败')
    }
  } catch (error) {
    console.error('确认提交失败：', error)
    ElMessage.error('确认提交失败')
  } finally {
    submitLoading.value = false
  }
}

// 查看详情
const viewDetail = (row) => {
  router.push(`/workorder/detail/${row.id}`)
}

// 监听标签页切换
watch(activeTab, () => {
  pagination.pageNum = 1
  loadConfirmList()
})

// 初始化
onMounted(async () => {
  await getCurrentUserInfo()
  loadConfirmList()
})
</script>

<style scoped>
.confirm-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  margin-top: 20px;
}

:deep(.el-tag) {
  margin-right: 5px;
}
</style>
