import api from './auth.api'

export interface Deploy {
  id?: number
  projectId: number
  deployType: string
  version: string
  environment: string
  status: string
  operator: string
  startTime?: string
  endTime?: string
  logs?: string
}

export interface DeployPageRequest {
  pageNum?: number
  pageSize?: number
  projectId?: number
  environment?: string
  status?: string
}

export interface DeployPageResponse {
  records: Deploy[]
  total: number
  size: number
  current: number
  pages: number
}

export const deployApi = {
  add: (data: Deploy) => {
    return api.post('/deploy', data)
  },

  update: (data: Deploy) => {
    return api.put('/deploy', data)
  },

  delete: (id: number) => {
    return api.delete(`/deploy/${id}`)
  },

  page: (params: DeployPageRequest) => {
    return api.get<DeployPageResponse>('/deploy/page', { params })
  },

  getById: (id: number) => {
    return api.get<Deploy>(`/deploy/${id}`)
  }
}