<script setup>
import {
    Edit,
    Delete
} from '@element-plus/icons-vue'

import { ref, onMounted } from 'vue'

//文章分类数据模型
const categorys = ref([]);

//用户搜索时选中的分类id
const categoryId=ref('')

//用户搜索时选中的发布状态
const state=ref('')

//文章列表数据模型
const articles = ref([]);

//分页条数据模型
const pageNum = ref(1)//当前页
const total = ref(20)//总条数
const pageSize = ref(5)//每页条数

//当每页条数发生了变化，调用此函数
const onSizeChange = (size) => {
    pageSize.value = size;
    getArticleList();
}
//当前页码发生变化，调用此函数
const onCurrentChange = (num) => {
    pageNum.value = num;
    getArticleList();
}

//文章分类下拉框数据模型
import { getCategoryListService } from '@/api/article'
const getCategoryList = async () => {
    const res = await getCategoryListService()
    categorys.value = res.data
}

onMounted(async () => {
    await getCategoryList()
    await getArticleList()
})

//文章列表数据模型
import { getArticleListService,addArticleService,updateArticleService,deleteArticleService} from '@/api/article'
const getArticleList = async () => {
    if (!categorys.value || categorys.value.length === 0) {
        await getCategoryList()
    }
    const res = await getArticleListService({
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        categoryId: categoryId.value?categoryId.value:null,
        state: state.value?state.value:null
    })
    const categoryNameMap = new Map((categorys.value || []).map(c => [c.id, c.categoryName]))

    articles.value = (res.data.items || []).map(item => ({
        ...item,
        categoryName: categoryNameMap.get(item.categoryId) || ''
    }));
    total.value = res.data.total
}

//添加文章抽屉组件
import {Plus} from '@element-plus/icons-vue'
import { QuillEditor } from '@vueup/vue-quill'
import '@vueup/vue-quill/dist/vue-quill.snow.css'
import {useTokenStore} from '@/stores/token'
const tokenStore = useTokenStore();
//控制抽屉是否显示
const visibleDrawer = ref(false)
//添加表单数据模型
const articleModel = ref({
    title: '',
    categoryId: '',
    coverImg: '',
    content:'',
    state:''
})

//上传成功成功的回调函数
const uploadSuccess = (res) => {
    //articleModel.value.coverImg = res.data;
    articleModel.value.coverImg ="https://cdn.pixabay.com/photo/2020/04/13/19/40/sun-5039871_1280.jpg";
    console.log(res.data);
}
//弹窗表单标题
const dialogTitle = ref('');
const isEdit = ref(false)

const clearArticleModel = () => {
    articleModel.value = {
        title: '',
        categoryId: '',
        coverImg: '',
        content: '',
        state: ''
    }
}

const openAddDrawer = () => {
    isEdit.value = false
    dialogTitle.value = '添加文章'
    clearArticleModel()
    visibleDrawer.value = true
}

//添加文章
import {ElMessage,ElMessageBox} from 'element-plus';
const addArticle = async (clickSatte) => { 
    articleModel.value.state = clickSatte;
    let res = await addArticleService(articleModel.value);
    ElMessage.success(res.message?res.message:'添加成功');
    visibleDrawer.value = false;
    getArticleList();
}

//修改文章
const editArticle = (row) => {
    isEdit.value = true
    dialogTitle.value = '修改文章'
    articleModel.value = {
        id: row.id,
        title: row.title,
        categoryId: row.categoryId,
        coverImg: row.coverImg,
        content: row.content,
        state: row.state
    }
    visibleDrawer.value = true
}

//调用修改文章接口，修改数据
const updateArticle = async (clickSatte) => {
    const payload = {
        id: articleModel.value.id,
        title: articleModel.value.title,
        content: articleModel.value.content,
        coverImg: articleModel.value.coverImg,
        state: clickSatte,
        categoryId: articleModel.value.categoryId
    }
    let res = await updateArticleService(payload)
    ElMessage.success(res.message ? res.message : '修改成功')
    visibleDrawer.value = false
    getArticleList()
}

const submitArticle = async (clickSatte) => {
    if (isEdit.value) {
        await updateArticle(clickSatte)
        return
    }
    await addArticle(clickSatte)
}

//删除文章 
const deleteArticle =  (row) => {
       ElMessageBox.confirm('此操作将永久删除该文章, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
    }).then(async () => {
        let res = await deleteArticleService(row.id);
        ElMessage.success('删除成功');
        getArticleList();
    }).catch(() => {
      ElMessage({
        type: 'info',
        message: '取消删除',
      })
    })
}

const stripHtml = (value) => {
    if (!value) return ''
    return String(value)
        .replace(/<[^>]+>/g, '')
        .replace(/\s+/g, ' ')
        .trim()
}
</script>
<template>
    <el-card class="page-container">
        <template #header>
            <div class="header">
                <span>文章管理</span>
                <div class="extra">
                    <el-button type="primary" @click="openAddDrawer()">添加文章</el-button>
                </div>
            </div>
        </template>
        <!-- 搜索表单 -->
        <el-form inline>
            <el-form-item label="文章分类：">
                <el-select placeholder="请选择" v-model="categoryId" style="width: 150px">
                    <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item label="发布状态：">
                <el-select placeholder="请选择" v-model="state" style="width: 150px">
                    <el-option label="已发布" value="已发布"></el-option>
                    <el-option label="草稿" value="草稿"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="getArticleList()">搜索</el-button>
                <el-button @click="categoryId='';state=''">重置</el-button>
            </el-form-item>
        </el-form>
        <!-- 文章列表 -->
        <el-table :data="articles" style="width: 100%">
            <el-table-column label="文章标题" width="400" prop="title"></el-table-column>
            <el-table-column label="内容" width="420">
                <template #default="{ row }">
                    <el-tooltip :content="stripHtml(row.content)" placement="top" :show-after="300">
                        <div class="content-ellipsis">{{ stripHtml(row.content) }}</div>
                    </el-tooltip>
                </template>
            </el-table-column>
            <el-table-column label="分类" prop="categoryName"></el-table-column>
            <el-table-column label="发表时间" prop="createTime"> </el-table-column>
            <el-table-column label="状态" prop="state"></el-table-column>
            <el-table-column label="操作" width="100">
                <template #default="{ row }">
                    <el-button :icon="Edit" circle plain type="primary" @click="editArticle(row)"></el-button>
                    <el-button :icon="Delete" circle plain type="danger" @click="deleteArticle(row)"></el-button>
                </template>
            </el-table-column>
            <template #empty>
                <el-empty description="没有数据" />
            </template>
        </el-table>
        <!-- 分页条 -->
        <el-pagination v-model:current-page="pageNum" v-model:page-size="pageSize" :page-sizes="[ 5 ,10, 15, 30]"
            layout="jumper, total, sizes, prev, pager, next" background :total="total" @size-change="onSizeChange"
            @current-change="onCurrentChange" style="margin-top: 20px; justify-content: flex-end" />
        <!-- 抽屉 -->
        <el-drawer v-model="visibleDrawer" :title="dialogTitle" direction="rtl" size="50%">
            <!-- 添加文章表单 -->
            <el-form :model="articleModel" label-width="100px" >
                <el-form-item label="文章标题" >
                    <el-input v-model="articleModel.title" placeholder="请输入标题"></el-input>
                </el-form-item>
                <el-form-item label="文章分类">
                    <el-select placeholder="请选择" v-model="articleModel.categoryId">
                        <el-option v-for="c in categorys" :key="c.id" :label="c.categoryName" :value="c.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="文章封面">

                    <el-upload class="avatar-uploader" :auto-upload="true" :show-file-list="false" action="/api/upload" name="file" :headers="{'Authorization':tokenStore.token}" :on-success="uploadSuccess">
                        <img v-if="articleModel.coverImg" :src="articleModel.coverImg" class="avatar" />
                        <el-icon v-else class="avatar-uploader-icon">
                            <Plus />
                        </el-icon>
                    </el-upload>
                </el-form-item>
                <el-form-item label="文章内容">
                    <div class="editor">
                        <quill-editor theme="snow" v-model:content="articleModel.content" contentType="html"></quill-editor>
                    </div>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="submitArticle('已发布')">发布</el-button>
                    <el-button type="info" @click="submitArticle('草稿')">草稿</el-button>
                </el-form-item>
            </el-form>
        </el-drawer>
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
/* 抽屉样式 */
.avatar-uploader {
    :deep() {
        .avatar {
            width: 178px;
            height: 178px;
            display: block;
        }

        .el-upload {
            border: 1px dashed var(--el-border-color);
            border-radius: 6px;
            cursor: pointer;
            position: relative;
            overflow: hidden;
            transition: var(--el-transition-duration-fast);
        }

        .el-upload:hover {
            border-color: var(--el-color-primary);
        }

        .el-icon.avatar-uploader-icon {
            font-size: 28px;
            color: #8c939d;
            width: 178px;
            height: 178px;
            text-align: center;
        }
    }
}
.editor {
  width: 100%;
  :deep(.ql-editor) {
    min-height: 200px;
  }
}
</style>