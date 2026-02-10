import api from './auth.api'

export interface ApiInfo {
  id?: number
  projectId: number
  path: string
  method: string
  params?: string
  createTime?: string
}

export interface ApiInfoPageRequest {
  pageNum?: number
  pageSize?: number
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

export const apiInfoApi = {
  add: (data: ApiInfo) => {
    return api.post('/api-info', data)
  },

  update: (data: ApiInfo) => {
    return api.put('/api-info', data)
  },

  delete: (id: number) => {
    return api.delete(`/api-info/${id}`)
  },

  page: (params: ApiInfoPageRequest) => {
    return api.get<ApiInfoPageResponse>('/api-info/page', { params })
  },

  getById: (id: number) => {
    return api.get<ApiInfo>(`/api-info/${id}`)
  }
}