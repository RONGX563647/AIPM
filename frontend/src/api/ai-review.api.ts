import api from './auth.api'

export interface CodeAiReview {
  id?: number
  projectId: number
  codeContent: string
  score?: number
  suggestion?: string
  improvedCode?: string
  createTime?: string
}

export interface ReviewPageRequest {
  pageNum: number
  pageSize: number
  projectId?: number
}

export interface ReviewPageResponse {
  records: CodeAiReview[]
  total: number
  current: number
  size: number
}

export const aiReviewApi = {
  submit: (data: CodeAiReview) => {
    return api.post<CodeAiReview>('/ai/review/submit', data)
  },

  page: (params: ReviewPageRequest) => {
    return api.get<ReviewPageResponse>('/ai/review/page', { params })
  },

  delete: (id: number) => {
    return api.delete(`/ai/review/delete/${id}`)
  }
}
