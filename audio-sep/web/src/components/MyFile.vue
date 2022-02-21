<template>
    <div>
        <div class="text1">
            <span>我的文件</span>
        </div>

        <el-table :data="fileData" stripe style="width: 900px; margin-top: 100px">
            <el-table-column prop="name" label="文件名" width="220" />
            <el-table-column prop="status" label="状态" width="220" />
            <el-table-column prop="lastDate" label="完成时间" width="220" />
            <el-table-column label="" align="right">
                <template #default="scope">
                    <el-button size="mini" type="primary" @click="handleDownload(scope.row)">下载</el-button>
                    <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    import {ElMessage} from "element-plus";
    import {getRecords, deleteRecord} from "@/config/api";

    export default {
        name: "MyFile",
        data() {
            return {
                fileData: [],
                choose: [],
                dialogVisible: false
            }
        },
        methods: {
            handleDelete(item) {
                deleteRecord(item.id)
                    .then(response => {
                        ElMessage.success(response.data)
                        this.handleSelect()
                    })
                    .catch(error => {
                        console.log(error)
                    })
            },
            handleSelect() {
                getRecords()
                    .then(response => {
                        this.fileData = response.data
                    })
                    .catch(error => {
                        console.log(error)
                    })
            },
            handleDownload(item) {
                this.choose = item.urls
                this.dialogVisible = true
            }
        },
        mounted() {
            this.handleSelect()
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
</style>