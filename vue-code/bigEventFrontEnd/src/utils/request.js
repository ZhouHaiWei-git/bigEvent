//导入axios  npm install axios
import axios from 'axios';
//导入element plus的消息提示组件
import { ElMessage } from "element-plus";
import {useTokenStore} from '@/stores/token';
//定义一个变量,记录公共的前缀  ,  baseURL
// const baseURL = 'http://localhost:8080';
const baseURL = '/api';
const instance = axios.create({ baseURL,timeout: 5000});

//添加请求拦截器
instance.interceptors.request.use(
  config => {
      // 添加请求头（如：token）
      const token = useTokenStore().token;
      if (token) {
          config.headers.Authorization = token;
      }
      return config;
  },
  err => {
      return Promise.reject(err);
  }
)

//添加响应拦截器
//直接导入已创建的 Router 实例,无上下文要求，任何 JS 文件都能导入
import router from '@/router';
instance.interceptors.response.use(
  result => {
      // 判断业务状态码（保持你的原有业务逻辑）
      if (result.data.code === 0) {
          return result.data;
      }
      // 业务异常提示（优化：增加默认值简化写法）
      ElMessage.error(result.data.message || "业务请求异常");
      return Promise.reject(result.data);
  },
  err => {
      // 定义错误提示信息变量
      let errorMsg = "服务异常";

      // 1. 判断：服务器未响应（无响应结果，如网络中断、请求超时、服务器宕机）
      if (!err.response) {
          // 细分未响应场景，给出更精准提示
          if (err.message.includes('timeout')) {
              errorMsg = "请求超时，请稍后重试";
          } else if (err.message.includes('Network Error')) {
              errorMsg = "网络连接异常，请检查网络";
          } else {
              errorMsg = "服务器未响应，请联系管理员";
          }
      } else {
          // 2. 判断：服务器已响应（存在 err.response，即 HTTP 状态码返回）
          const { status, data } = err.response;
          // 2.1 服务器报错：HTTP 5xx 系列状态码（服务器内部错误）
          if (status >= 500 && status < 600) {
              errorMsg = data?.message || `服务器内部错误（${status}），请稍后重试`;
          } 
          // 可选：补充其他 HTTP 错误状态码提示（如 404、401 等，增强健壮性）
          else if (status === 404) {
              errorMsg = "请求接口不存在（404）";
          } else if (status === 401) {
              errorMsg = "身份验证失效，请重新登录（401）";
              router.push('/login');
          } else if (status === 403) {
              errorMsg = "无权限访问该资源（403）";
          } else {
              // 其他 HTTP 错误（如 400 等）
              errorMsg = data?.message || `请求失败（${status}）`;
          }
      }

      // 统一弹出错误提示
      ElMessage.error(errorMsg);

      // 抛出异常，将异步状态转为失败状态（传递错误信息，方便后续捕获处理）
      return Promise.reject({
          ...err,
          customMsg: errorMsg // 附加自定义错误提示，便于业务组件中使用
      });
  }
);

export default instance;