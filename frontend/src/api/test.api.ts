import api from './auth.api'

export interface TestCase {
  id?: number
  apiId: number
  caseName: string
  params: string
  expectedResult: string
  createTime?: string
}

export interface TestReport {
  id?: number
  apiId: number
  caseId: number
  actualResult: string
  status: string
  testTime?: string
  remark?: string
}

export interface TestCasePageRequest {
  pageNum?: number
  pageSize?: number
  apiId?: number
  caseName?: string
}

export interface TestReportPageRequest {
  pageNum?: number
  pageSize?: number
  apiId?: number
  caseId?: number
  status?: string
}

export interface TestPageResponse {
  records: (TestCase | TestReport)[]
  total: number
  size: number
  current: number
  pages: number
}

export const testApi = {
  // 测试用例相关
  addCase: (data: TestCase) => {
    return api.post('/test/case', data)
  },

  updateCase: (data: TestCase) => {
    return api.put('/test/case', data)
  },

  deleteCase: (id: number) => {
    return api.delete(`/test/case/${id}`)
  },

  getCase: (id: number) => {
    return api.get<TestCase>(`/test/case/${id}`)
  },

  getCasePage: (params: TestCasePageRequest) => {
    return api.get<TestPageResponse>('/test/case/page', { params })
  },

  // 执行测试用例
  runCase: (caseId: number) => {
    return api.post(`/test/case/run/${caseId}`)
  },

  // 测试报告相关
  getReportPage: (params: TestReportPageRequest) => {
    return api.get<TestPageResponse>('/test/report/page', { params })
  },

  getReport: (id: number) => {
    return api.get<TestReport>(`/test/report/${id}`)
  }
}