<template>
    <div>
        <div class="text1">
            <span>多人声分离</span>
        </div>

        <el-upload
                class="upload"
                ref="upload"
                drag
                multiple
                action="#"
                :limit="10"
                :on-change="handleChange"
                :on-exceed="handleExceed"
                :before-upload="handleBeforeUpload"
                :http-request="upload"
                :auto-upload="false"
        >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
                将文件拖放到这个区域，或<em>点击这里</em>并从电脑上选择音频文件来上传
            </div>
            <template #tip>
                <div class="el-upload__tip">
                    支持mp3、wav、wma格式，单个文件大小不能超过10mb
                </div>
            </template>
        </el-upload>
        <el-button type="primary" class="submit-button" @click="beforeSubmitUpload">提交</el-button>
        <el-row :gutter="20" class="card-box">
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <span>1.上传</span>
                    </template>
                    <span class="text3">拖放您的一个或者多个音频文件到这个区域并开始上传</span>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <span>2.提取人声</span>
                    </template>
                    <span class="text3">从音频中混合的人声中根据声音特征提取出不同人发出的声音</span>
                </el-card>
            </el-col>
            <el-col :span="8">
                <el-card shadow="hover">
                    <template #header>
                        <span class="text3">3.下载</span>
                    </template>
                    <span class="text3">下载提取人声后的mp3文件</span>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script>
    import { UploadFilled } from '@element-plus/icons-vue'
    import { ElMessage } from 'element-plus'
    import { ElLoading } from 'element-plus'
    import {uploadFiles} from "@/config/api";

    export default {
        name: "Start",
        data() {
            return {
                logoUrl: require('../assets/logo.png'),
                fileNum: 0,
                fileList: [],
                loading: null
            }

        },
        methods: {
            handleChange(file, fileList) {
                if (file.size >= 1024 * 1024 * 10) {
                    ElMessage.error('单个文件不能超过10MB')
                    fileList.pop()
                    return
                }

                let isExist = fileList.slice(0, fileList.length - 1).find(f => f.name === file.name)
                if (isExist) {
                    ElMessage.error('文件: ' + file.name + '已存在')
                    fileList.pop()
                }

                this.fileNum = fileList.length

            },
            handleExceed() {
                ElMessage.error('文件数量不能超过10')
            },
            handleBeforeUpload(file) {
                if(file.type.indexOf('audio') === -1) {
                    ElMessage.error('文件: ' + file.name + ' 类型不被允许')
                    return false
                }

                return true
            },
            upload(file) {
                this.fileList.push(file.file)
                this.fileNum--
                if (this.fileNum === 0) {
                    this.onSubmitUpload()
                }
            },
            beforeSubmitUpload() {
                this.$refs.upload.submit()
            },
            onSubmitUpload() {
                let formData = new FormData()
                this.fileList.forEach(f => formData.append('files', f))

                let config = {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }

                this.loading = ElLoading.service({
                    lock: true,
                    text: '正在处理，请稍后',
                    background: 'rgba(0, 0, 0, 0.7)',
                })

                uploadFiles(formData, config)
                    .then(response => {
                        this.loading.close()
                        this.$router.push({
                            name : 'finished',
                            params : {
                                downloadUrls : JSON.stringify(response.data)
                            }
                        })
                    })
                    .catch(error => {
                        console.log(error)
                    })
            }

        },
        components: {
            UploadFilled
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
    .text3 {
        font-family: 微软雅黑;
        font-size: 15px;
        color: dimgray;
    }
    .upload {
        margin-top: 100px;
    }
    .card-box {
        margin-top: 100px;
        width: 1000px;
    }
    .submit-button {
        margin-top: 50px;
    }
</style>