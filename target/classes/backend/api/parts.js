// 查询列表接口
const getPartPage = (params) => {
  return $axios({
    url: '/part/page',
    method: 'get',
    params
  })
}

// 删除接口
const deletePart = (ids) => {
  return $axios({
    url: '/part',
    method: 'delete',
    params: { ids }
  })
}

// 修改接口
const editPart = (params) => {
  return $axios({
    url: '/part',
    method: 'put',
    data: { ...params }
  })
}

// 新增接口
const addPart = (params) => {
  return $axios({
    url: '/part',
    method: 'post',
    data: { ...params }
  })
}

// 查询详情
const queryPartById = (id) => {
  return $axios({
    url: `/part/${id}`,
    method: 'get'
  })
}

// 获取零件分类列表
const getCategoryList = (params) => {
  return $axios({
    url: '/category/list',
    method: 'get',
    params
  })
}

// 查零件列表的接口
const queryPartList = (params) => {
  return $axios({
    url: '/part/list',
    method: 'get',
    params
  })
}

// 文件down预览
const commonDownload = (params) => {
  return $axios({
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'
    },
    url: '/common/download',
    method: 'get',
    params
  })
}

// 起售停售---批量起售停售接口
const partStatusByStatus = (params) => {
  return $axios({
    url: `/part/status/${params.status}`,
    method: 'post',
    params: { ids: params.id }
  })
}