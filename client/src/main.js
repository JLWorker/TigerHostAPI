import { createApp } from 'vue'
import App from './App.vue'
import router from "@/route/router";
import "bootstrap/dist/css/bootstrap.css"
import bootstrap from "bootstrap/dist/js/bootstrap"

const app = createApp(App)
app.use(router)
app.use(bootstrap)
app.mount('#app')
