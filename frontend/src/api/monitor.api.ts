import api from './auth.api'

export interface Monitor {
  id?: number
  projectId: number
  monitorUrl: string
  responseTime: number
  status: string
  createTime?: string
  updateTime?: string
}

export interface MonitorPageRequest {
  pageNum: number
  pageSize: number
  projectId?: number
  status?: string
}

export interface MonitorPageResponse {
  records: Monitor[]
  total: number
  current: number
  size: number
}

export const monitorApi = {
  add: (data: Monitor) => {
    return api.post<Monitor>('/monitor/add', data)
  },

  page: (params: MonitorPageRequest) => {
    return api.get<MonitorPageResponse>('/monitor/page', { params })
  },

  delete: (id: number) => {
    return api.delete(`/monitor/delete/${id}`)
  },

  getUptime: (projectId: number) => {
    return api.get<number>(`/monitor/uptime/${projectId}`)
  }
}
