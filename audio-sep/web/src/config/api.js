import http from "@/config/axios";

export function uploadFiles(data, config) {
    return http({
        method: 'post',
        url: '/files',
        data: data,
        config: config
    })
}

export function downloadFiles() {
    return http({
        method: 'get',
        url: '/files',
    })
}

export function getRecords() {
    return http({
        method: 'get',
        url: '/records'
    })
}

export function deleteRecord(id) {
    return http({
        method: 'delete',
        url: '/records/' + id
    })
}

