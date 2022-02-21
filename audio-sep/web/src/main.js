import { createApp } from 'vue'
import App from './App.vue'
import router from "@/config/router";
import 'element-plus/dist/index.css'
import ElementPlus from 'element-plus'


const app = createApp(App)
    .use(router)
    .use(ElementPlus)

app.mount('#app')