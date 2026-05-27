import {createRouter,createWebHistory} from 'vue-router';

const routers=[
    {
        path:'/',
        component:()=>import('@/views/Index.vue'),
        meta:{
            title:'首页',
        },
    },
];

const router=createRouter({
    history:createWebHistory(),
    routes:routers
});

export default router;