<script setup>
import {
    Edit,
    Delete
} from '@element-plus/icons-vue'
import { ref } from 'vue'

const categorys = ref([]);
//异步获取文章分类列表
import { getCategoryListService,addArtCategoryService,updateArtCategoryService,deleteArtCategoryService } from '@/api/article'
const getCategoryList = async () => {
    let res = await getCategoryListService();
    categorys.value = res.data;
}
getCategoryList();

//控制添加分类弹窗
const dialogVisible = ref(false)

//添加分类数据模型
const categoryModel = ref({
    categoryName: '',
    categoryAlias: ''
})
//添加分类表单校验
const rules = {
    categoryName: [
        { required: true, message: '请输入分类名称', trigger: 'blur' },
    ],
    categoryAlias: [
        { required: true, message: '请输入分类别名', trigger: 'blur' },
    ]
}
//调用接口添加表单
import { ElMessage, ElMessageBox } from "element-plus";
const addArtCategory = async () => {
    let res = await addArtCategoryService(categoryModel.value);
    ElMessage.success('添加成功');
    dialogVisible.value = false;
    clearCategoryModel();
    getCategoryList();
}

//弹窗表单标题
const dialogTitle = ref('');


//编辑分类
const editCategory = (row) => {
    dialogVisible.value = true;
    dialogTitle.value = '编辑分类';
    categoryModel.value.categoryName = row.categoryName;
    categoryModel.value.categoryAlias = row.categoryAlias;
    //扩展ID属性
    categoryModel.value.id = row.id;
}
//调用接口修改分类
const updateArtCategory = async () => {
    let res = await updateArtCategoryService(categoryModel.value);
    ElMessage.success('修改成功');
    dialogVisible.value = false;
    clearCategoryModel();
    getCategoryList();
}

//调用接口删除分类
const deleteCategory = (row) => {
    ElMessageBox.confirm('此操作将永久删除该分类, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        let res = await deleteArtCategoryService(row.id);
        ElMessage.success('删除成功');
        getCategoryList();
    }).catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除',
      })
    })
}
//清空弹窗数据chuang
const clearCategoryModel = () => {
    categoryModel.value = {
        categoryName: '',
        categoryAlias: ''
    }
}
</script>
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>文章分类</span>
                <div class="extra">
                    <el-button type="primary" @click="dialogVisible = true;dialogTitle = '添加分类'">添加分类</el-button>
                </div>
            </div>
        </template>
        <el-table :data="categorys" style="width: 100%">
            <el-table-column label="序号" width="100" type="index"> </el-table-column>
            <el-table-column label="分类名称" prop="categoryName"></el-table-column>
            <el-table-column label="分类别名" prop="categoryAlias"></el-table-column>
            <el-table-column label="操作" width="100">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="editCategory(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteCategory(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
    </el-card>
    <!-- 添加分类弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="30%">
      <el-form :model="categoryModel" :rules="rules" label-width="100px" style="padding-right: 30px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="categoryModel.categoryName" minlength="1" maxlength="10"></el-input>
        </el-form-item>
        <el-form-item label="分类别名" prop="categoryAlias">
          <el-input v-model="categoryModel.categoryAlias" minlength="1" maxlength="15"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false;clearCategoryModel()">取消</el-button>
          <el-button type="primary" @click="dialogTitle == '添加分类' ? addArtCategory():updateArtCategory();"> 确认 </el-button>
        </span>
      </template>
    </el-dialog>
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