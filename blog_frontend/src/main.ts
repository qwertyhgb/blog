// 导入Vue框架的createApp函数，用于创建Vue应用实例
import { createApp } from "vue";
// 导入Pinia状态管理库的createPinia函数，用于创建状态管理实例
import { createPinia } from "pinia";
// 导入全局样式文件
import "./style.css";
// 导入Lato字体样式
import "vfonts/Lato.css";
// 导入FiraCode字体样式
import "vfonts/FiraCode.css";
// 导入根组件App.vue
import App from "./App.vue";
// 导入路由配置
import router from "./router";

// 创建Vue应用实例
const app = createApp(App);
// 创建Pinia状态管理实例
const pinia = createPinia();

// 在应用中注册Pinia状态管理
app.use(pinia);
// 在应用中注册路由
app.use(router);
// 将应用挂载到id为app的DOM元素上
app.mount("#app");
