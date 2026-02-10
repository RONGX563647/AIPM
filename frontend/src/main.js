import "./assets/main.css";
import { createApp } from "vue";
import { createPinia } from "pinia";
import router from "./router";
import ElementPlus from "element-plus";
import * as ElementPlusIconsVue from "@element-plus/icons-vue";

// 导入Element Plus样式
import "element-plus/dist/index.css";

// 导入用户store
import { useUserStore } from './stores/user';

import App from "./App.vue";

const app = createApp(App);
const pinia = createPinia();

// 注册Element Plus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component);
}

// 注册插件
app.use(pinia);
app.use(router);
app.use(ElementPlus, {
  size: "default",
  zIndex: 3000,
});

// 初始化用户store
const userStore = useUserStore();
userStore.init();

// GitHub OAuth: handle token returned in URL hash (e.g., #token=...)
(function () {
  const hash = window.location.hash || "";
  if (hash.startsWith("#token=")) {
    const token = decodeURIComponent(hash.substring("#token=".length));
    if (token) {
      userStore.login(token);
      // clear hash and navigate to root
      window.location.hash = "";
      if (
        window.location.pathname === "/" ||
        window.location.pathname.startsWith("/#")
      ) {
        window.location.href = "/";
      }
    }
  }
})();

// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error("Vue Error:", err, instance, info);
};

app.mount("#app");
