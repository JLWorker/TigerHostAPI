import {createRouter, createWebHistory} from "vue-router"
import PasswordChange from "@/components/PasswordChange.vue";
const routes = [
    {path: "/password/update/:code", component: PasswordChange, name: "PasswordChange"},
]

const router = createRouter({history: createWebHistory(),
    routes})

// router.beforeEach((to) => {
//     if(to.name === "PasswordChange"){
//         Main.methods.startInterval();
//     }

// })

export default router