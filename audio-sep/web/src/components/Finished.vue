<template>
    <div>
        <div class="text1">
            <span>已完成</span>
        </div>
        <el-tag type="success" class="tag1">请下载文件</el-tag><br>

        <el-table :data="fileData" stripe style="width: 100%; margin-top: 100px">
            <el-table-column prop="name" label="文件" width="220" />
            <el-table-column prop="status" label="状态" width="220" />
            <el-table-column label="" align="right">
                <template #default="scope">
                    <el-button size="mini" type="primary" @click="handleDownload(scope.row)">下载</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog
                v-model="dialogVisible"
                title="链接"
                width="40%"
        >
            <el-space wrap>
                <template v-for="(item, index) in choose" :key="index">
                    <el-link type="primary" :href="item">{{item}}</el-link>
                </template>
            </el-space>
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">返回</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "Finished",
        data() {
            return {
                fileData: [],
                choose: [],
                dialogVisible: false
            }
        },
        methods: {
            handleDownload(item) {
                this.choose = item.urls
                this.dialogVisible = true
            },

        },
        mounted() {
            if (!this.$route.params['downloadUrls'])
                return

            let data = JSON.parse(this.$route.params['downloadUrls'])
            for (let key in data) {
                this.fileData.push({
                    name: key,
                    status: '完成',
                    urls: data[key]
                })
            }

        }
    }
</script>

<style>
    .text1 {
        text-align: center;
        font-family: 微软雅黑;
        font-size: 30px;
        color: dimgray;
        margin-top: 50px;
    }
    .tag1 {
        margin-top: 20px;
    }
</style>