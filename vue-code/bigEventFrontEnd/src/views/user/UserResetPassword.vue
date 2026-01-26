<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { useTokenStore } from '@/stores/token'
import useUserInfoStore from '@/stores/userInfo'
import { userPasswordUpdateService } from '@/api/user'

const router = useRouter()
const tokenStore = useTokenStore()
const userInfoStore = useUserInfoStore()

const formRef = ref()

const passwordModel = ref({
  oldPwd: '',
  newPwd: '',
  rePwd: ''
})

const checkNewPwd = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入新密码'))
    return
  }
  if (value === passwordModel.value.oldPwd) {
    callback(new Error('新密码不能与原密码一致'))
    return
  }
  callback()
}

const checkRePwd = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请再次输入新密码'))
    return
  }
  if (value !== passwordModel.value.newPwd) {
    callback(new Error('两次输入的密码不一致'))
    return
  }
  callback()
}

const rules = {
  oldPwd: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
    { min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' }
  ],
  newPwd: [
    { validator: checkNewPwd, trigger: 'blur' },
    { min: 5, max: 16, message: '长度在 5 到 16 个字符', trigger: 'blur' }
  ],
  rePwd: [{ validator: checkRePwd, trigger: 'blur' }]
}

const clearPasswordModel = () => {
  passwordModel.value = {
    oldPwd: '',
    newPwd: '',
    rePwd: ''
  }
  formRef.value?.clearValidate?.()
}

const submit = async () => {
  await formRef.value.validate()
  await userPasswordUpdateService(passwordModel.value)
  ElMessage.success('密码修改成功，请重新登录')
  userInfoStore.removeInfo()
  tokenStore.removeToken()
  router.push('/login')
}
</script>

<template>
  <el-card class="page-container">
    <template #header>
      <div class="header">
        <span>重置密码</span>
      </div>
    </template>

    <el-row>
      <el-col :span="12">
        <el-form ref="formRef" :model="passwordModel" :rules="rules" label-width="100px" size="large">
          <el-form-item label="原密码" prop="oldPwd">
            <el-input v-model="passwordModel.oldPwd" type="password" show-password autocomplete="off" />
          </el-form-item>
          <el-form-item label="新密码" prop="newPwd">
            <el-input v-model="passwordModel.newPwd" type="password" show-password autocomplete="off" />
          </el-form-item>
          <el-form-item label="确认密码" prop="rePwd">
            <el-input v-model="passwordModel.rePwd" type="password" show-password autocomplete="off" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submit()">提交修改</el-button>
            <el-button @click="clearPasswordModel()">重置</el-button>
          </el-form-item>
        </el-form>
      </el-col>
    </el-row>
  </el-card>
</template>

<style lang="scss" scoped>
.page-container {
  min-height: 100%;
  box-sizing: border-box;

  .header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }
}
</style>
