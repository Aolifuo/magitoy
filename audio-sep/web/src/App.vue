<template>
    <el-container>
        <el-header>
            <el-menu
                    mode="horizontal"
                    router>
                <div style="margin: auto"></div>
                <el-image
                        :src="logoUrl"
                        style="width: 70px; height: 70px; margin-right: auto;">
                </el-image>
                <el-menu-item index="/start">在线工具</el-menu-item>
                <el-menu-item index="/my-file">我的文件</el-menu-item>
                <el-menu-item index="4">关于</el-menu-item>
                <div style="margin: auto"></div>
            </el-menu>
        </el-header>

        <el-main style="margin: auto; text-align: center">
            <transition name="el-fade-in">
                <router-view></router-view>
            </transition>

        </el-main>
    </el-container>
</template>

<script>
    export default {
        name: 'App',
        data() {
            return {
                logoUrl: require('./assets/logo.png'),
            }
        },
    }
</script>

<style>
</style>
