//导入request.js请求工具
import request from '@/utils/request.js'

//提供调用注册接口的函数
export const userRegisterService = (registerData) => {
    //借助与UrlSearchParams构造对象，将数据转为查询字符串 
    const params = new URLSearchParams()
    for (let key in registerData) {
        params.append(key, registerData[key])
    }
    //调用request.js中的post方法，传入请求地址和参数
    return request.post('/user/register', params)
}
export const userLoginService = (loginData) => {
    //借助与UrlSearchParams构造对象，将数据转为查询字符串 
    const params = new URLSearchParams()
    for (let key in loginData) {
        params.append(key, loginData[key])
    }
    //调用request.js中的post方法，传入请求地址和参数
    return request.post('/user/login', params)
}

//提供调用获取用户信息接口
export const userInfoGetService = () => {
    //调用request.js中的get方法，传入请求地址
    return request.get('/user/userInfo')
}

//提供调用修改用户信息接口
export const userInfoUpdateService = (userInfoData) => {
    return request.put('/user/update', userInfoData)
}

//修改头像
export const userAvatarUpdateService = (avatarUrl) => {
    const params = new URLSearchParams()
    params.append('avatarUrl', avatarUrl)
    return request.patch('/user/avatar', params)
}

//修改密码
export const userPasswordUpdateService = (passwordData) => {
    return request.patch('/user/updatePwd', passwordData)
}
