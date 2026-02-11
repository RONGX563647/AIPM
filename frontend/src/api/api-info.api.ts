import api from './auth.api'

export interface ApiInfo {
  id?: number
  projectId: number
  path: string
  method: string
  params?: string
  header?: string
  response?: string
  remark?: string
  createTime?: string
}

export interface ApiInfoPageRequest {
  pageNum: number
  pageSize: number
  projectId?: number
  path?: string
  method?: string
}

export interface ApiInfoPageResponse {
  records: ApiInfo[]
  total: number
  size: number
  current: number
  pages: number
}

export interface ApiDebugRequest {
  path: string
  method: string
  params?: string
  header?: string
}

export interface ApiDebugResponse {
  success: boolean
  statusCode?: number
  body?: string
  error?: string
}

export const apiInfoApi = {
  add: (data: ApiInfo) => {
    return api.post('/api/info/add', data)
  },

  update: (data: ApiInfo) => {
    return api.put('/api/info/update', data)
  },

  delete: (id: number) => {
    return api.delete(`/api/info/delete/${id}`)
  },

  page: (params: ApiInfoPageRequest) => {
    return api.get<ApiInfoPageResponse>('/api/info/page', { params })
  },

  debug: (data: ApiDebugRequest) => {
    return api.post<ApiDebugResponse>('/api/info/debug', data)
  }
}
