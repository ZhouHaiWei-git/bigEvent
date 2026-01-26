import request from '@/utils/request'
//文章分类列表查询
export const getCategoryListService = () => {
    //在pinia中定义的响应式数据，会自动返回一个响应式对象，不需要.value
    return request.get('/category');
}

//文章分类添加
export const addArtCategoryService = (categoryData) => {
    return request.post('/category', categoryData);
}

//文章分类编辑
export const updateArtCategoryService = (categoryData) => {
    return request.put('/category', categoryData);
}

//文章分类删除
export const deleteArtCategoryService = (id) => {
    return request.delete('/category?id='+id);
}

//文章列表查询
export const getArticleListService = (params) => {
    return request.get('/article', {params});
}
//文章添加
export const addArticleService = (articleData) => {
    return request.post('/article', articleData);
}

//修改文章
export const updateArticleService = (articleData) => {
    return request.put('/article', articleData);
}

//文章删除
export const deleteArticleService = (id) => {
    return request.delete('/article?id='+id);
}
