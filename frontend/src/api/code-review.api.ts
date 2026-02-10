import api from './auth.api'

export interface CodeReview {
  id?: number
  projectId: number
  file: string
  commitId: string
  reviewer: string
  status: string
  comments?: string
  score: number
  createTime?: string
}

export interface CodeReviewPageRequest {
  pageNum?: number
  pageSize?: number
  projectId?: number
  file?: string
  status?: string
}

export interface CodeReviewPageResponse {
  records: CodeReview[]
  total: number
  size: number
  current: number
  pages: number
}

export const codeReviewApi = {
  add: (data: CodeReview) => {
    return api.post('/code-review', data)
  },

  update: (data: CodeReview) => {
    return api.put('/code-review', data)
  },

  delete: (id: number) => {
    return api.delete(`/code-review/${id}`)
  },

  page: (params: CodeReviewPageRequest) => {
    return api.get<CodeReviewPageResponse>('/code-review/page', { params })
  },

  getById: (id: number) => {
    return api.get<CodeReview>(`/code-review/${id}`)
  }
}