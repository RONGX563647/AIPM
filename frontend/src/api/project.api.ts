import api from './auth.api'

export interface Project {
  id?: number
  name: string
  description?: string
  gitUrl?: string
  techStack?: string
  status: string
  onlineUrl?: string
  docUrl?: string
  createTime?: string
}

export interface ProjectPageRequest {
  pageNum?: number
  pageSize?: number
  name?: string
  status?: string
}

export interface ProjectPageResponse {
  records: Project[]
  total: number
  size: number
  current: number
  pages: number
}

export const projectApi = {
  add: (data: Project) => {
    return api.post('/project/add', data)
  },

  update: (data: Project) => {
    return api.put('/project/update', data)
  },

  delete: (id: number) => {
    return api.delete(`/project/delete/${id}`)
  },

  page: (params: ProjectPageRequest) => {
    return api.get<ProjectPageResponse>('/project/page', { params })
  },

  getById: (id: number) => {
    return api.get<Project>(`/project/${id}`)
  }
}