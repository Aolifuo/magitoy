import axios from "axios";

const http = axios.create({
    baseURL: 'http://localhost:8088'
})

http.interceptors.response.use(
    response => {
        return response
    },
    error => {
        alert("服务端出错，原因: " + error.response.data)
        return Promise.reject(error)
    }
)

export default http