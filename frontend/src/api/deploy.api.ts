import api from './auth.api'

export interface DeployRecord {
  id?: number
  projectId: number
  version: string
  env: string
  status: string
  content?: string
  createTime?: string
}

export interface DeployPageRequest {
  pageNum?: number
  pageSize?: number
  projectId?: number
  env?: string
  status?: string
  version?: string
}

export interface DeployPageResponse {
  records: DeployRecord[]
  total: number
  size: number
  current: number
  pages: number
}

export const deployApi = {
  add: (data: DeployRecord) => {
    return api.post<DeployRecord>('/deploy/add', data)
  },

  update: (data: DeployRecord) => {
    return api.put<DeployRecord>('/deploy/update', data)
  },

  delete: (id: number) => {
    return api.delete(`/deploy/delete/${id}`)
  },

  page: (params: DeployPageRequest) => {
    return api.get<DeployPageResponse>('/deploy/page', { params })
  }
}
