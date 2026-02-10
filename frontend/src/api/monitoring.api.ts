import api from './auth.api'

export interface Monitoring {
  id?: number
  metric: string
  value: number
  threshold: number
  status: string
  timestamp?: string
}

export interface MonitoringPageRequest {
  pageNum?: number
  pageSize?: number
  metric?: string
  status?: string
}

export interface MonitoringPageResponse {
  records: Monitoring[]
  total: number
  size: number
  current: number
  pages: number
}

export interface MonitoringStatsResponse {
  total: number
  warning: number
  danger: number
  normal: number
}

export const monitoringApi = {
  getStats: () => {
    return api.get<MonitoringStatsResponse>('/monitoring/stats')
  },

  page: (params: MonitoringPageRequest) => {
    return api.get<MonitoringPageResponse>('/monitoring/page', { params })
  },

  getById: (id: number) => {
    return api.get<Monitoring>(`/monitoring/${id}`)
  }
}