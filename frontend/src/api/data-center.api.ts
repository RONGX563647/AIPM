import api from './auth.api'

export interface DataMetric {
  id?: number
  name: string
  value: number
  unit: string
  category: string
  timestamp?: string
}

export interface DataMetricPageRequest {
  pageNum?: number
  pageSize?: number
  category?: string
  name?: string
}

export interface DataMetricPageResponse {
  records: DataMetric[]
  total: number
  size: number
  current: number
  pages: number
}

export interface DataCategoryStats {
  category: string
  count: number
  avgValue: number
  maxValue: number
  minValue: number
}

export const dataCenterApi = {
  getMetrics: (params: DataMetricPageRequest) => {
    return api.get<DataMetricPageResponse>('/data/metrics', { params })
  },

  getCategoryStats: () => {
    return api.get<DataCategoryStats[]>('/data/category-stats')
  },

  getById: (id: number) => {
    return api.get<DataMetric>(`/data/metric/${id}`)
  }
}