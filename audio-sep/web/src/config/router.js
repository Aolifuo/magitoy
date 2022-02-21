import {createRouter, createWebHistory} from 'vue-router'

const routes = [
    {
        path: '/start',
        component: () => import('@/components/Start')
    },
    {
        path: '/my-file',
        component: () => import('@/components/MyFile')
    },
    {
        path: '/finished',
        name: 'finished',
        component: () => import('@/components/Finished')
    },
    {
        path: '/',
        redirect: '/start'
    },

]

const router = createRouter( {
    history: createWebHistory(),
    routes
})


export default router