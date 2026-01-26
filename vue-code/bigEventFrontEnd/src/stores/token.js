//定义store
import { defineStore } from "pinia"

import {ref} from "vue"

export const useTokenStore = defineStore("token", () => {
    //定义数据
    const token = ref("");

    //定义方法
    const setToken = (newToken) => {
        token.value = newToken;
    };
    const removeToken = () => {
        token.value = "";
    };

    //返回数据
    return { token, setToken ,removeToken};
},{ 
    persist: true //持久化
});