import api from './auth.api'

export interface Task {
  id?: number
  projectId: number
  title: string
  content?: string
  status: string
  priority: string
  progress: number
  createTime?: string
}

export interface TaskPageRequest {
  pageNum?: number
  pageSize?: number
  projectId?: number
  status?: string
}

export interface TaskPageResponse {
  records: Task[]
  total: number
  size: number
  current: number
  pages: number
}

export interface TaskProgressResponse {
  projectId: number
  progress: number
  total: number
  completed: number
}

export const taskApi = {
  add: (data: Task) => {
    return api.post('/task', data)
  },

  update: (data: Task) => {
    return api.put('/task', data)
  },

  delete: (id: number) => {
    return api.delete(`/task/${id}`)
  },

  page: (params: TaskPageRequest) => {
    return api.get<TaskPageResponse>('/task/page', { params })
  },

  getById: (id: number) => {
    return api.get<Task>(`/task/${id}`)
  },

  getProgress: (projectId: number) => {
    return api.get<TaskProgressResponse>(`/task/progress/${projectId}`)
  }
}