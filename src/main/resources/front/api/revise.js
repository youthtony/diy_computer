//修改资料
function  updateReviseApi(data){
    return $axios({
        'url': '/user/revise',
        'method': 'put',
        data
    })
}

// //查询单个地址
// function addressFindOneApi(id) {
//     return $axios({
//         'url': `/addressBook/${id}`,
//         'method': 'get',
//     })
// }