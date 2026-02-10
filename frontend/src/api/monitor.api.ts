import api from './auth.api'

export interface Monitor {
  id?: number
  projectId: number
  metricName: string
  metricValue: number
  threshold: number
  status: string
  alertMessage?: string
  createTime?: string
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

  alerts: (params: MonitorPageRequest) => {
    return api.get<MonitorPageResponse>('/monitor/alerts', { params })
  }
}
