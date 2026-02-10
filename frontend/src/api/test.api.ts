import api from './auth.api'

export interface TestCase {
  id?: number
  apiId: number
  caseName: string
  params?: string
  expectedResult?: string
  createTime?: string
}

export interface TestCasePageRequest {
  pageNum: number
  pageSize: number
  apiId?: number
}

export interface TestCasePageResponse {
  records: TestCase[]
  total: number
  size: number
  current: number
  pages: number
}

export interface TestReport {
  id?: number
  apiId: number
  caseId: number
  actualResult?: string
  status?: string
  testTime?: string
  remark?: string
}

export interface TestReportPageRequest {
  pageNum: number
  pageSize: number
  apiId?: number
  caseId?: number
  status?: string
}

export interface TestReportPageResponse {
  records: TestReport[]
  total: number
  size: number
  current: number
  pages: number
}

export interface RunTestCaseResponse {
  success: boolean
  reportId?: number
  status?: string
  actualResult?: string
  expectedResult?: string
  error?: string
}

export const testApi = {
  addCase: (data: TestCase) => {
    return api.post('/test/case/add', data)
  },

  updateCase: (data: TestCase) => {
    return api.put('/test/case/update', data)
  },

  deleteCase: (id: number) => {
    return api.delete(`/test/case/delete/${id}`)
  },

  getCasePage: (params: TestCasePageRequest) => {
    return api.get<TestCasePageResponse>('/test/case/page', { params })
  },

  runCase: (caseId: number) => {
    return api.post<RunTestCaseResponse>(`/test/case/run/${caseId}`)
  },

  getReportPage: (params: TestReportPageRequest) => {
    return api.get<TestReportPageResponse>('/test/report/page', { params })
  }
}
