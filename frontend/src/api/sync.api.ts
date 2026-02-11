import api from './auth.api'

export interface GitHubRepo {
  id?: number
  repoId?: number
  repoName?: string
  repoUrl?: string
  description?: string
  language?: string
  starsCount?: number
  forksCount?: number
  openIssuesCount?: number
  updatedAt?: string
  createTime?: string
  updateTime?: string
}

export interface GiteeRepo {
  id?: number
  repoId?: number
  repoName?: string
  repoUrl?: string
  description?: string
  language?: string
  starsCount?: number
  forksCount?: number
  openIssuesCount?: number
  updatedAt?: string
  createTime?: string
  updateTime?: string
}

export interface SyncRecord {
  id?: number
  source?: string
  repoName?: string
  syncTime?: string
  syncStatus?: string
  errorMessage?: string
  dataCount?: number
  createTime?: string
}

export interface PageRequest {
  pageNum?: number
  pageSize?: number
  source?: string
}

export const syncApi = {
  syncGitHub: (username: string, accessToken?: string) => {
    return api.post('/sync/github', null, { params: { username, accessToken } })
  },

  syncGitee: (username: string, accessToken?: string) => {
    return api.post('/sync/gitee', null, { params: { username, accessToken } })
  },

  syncAll: () => {
    return api.post('/sync/all')
  },

  getGitHubRepos: (params: PageRequest) => {
    return api.get<GitHubRepo[]>('/sync/github/repos', { params })
  },

  getGiteeRepos: (params: PageRequest) => {
    return api.get<GiteeRepo[]>('/sync/gitee/repos', { params })
  },

  getSyncRecords: (params: PageRequest) => {
    return api.get<SyncRecord[]>('/sync/records', { params })
  },

  deleteGitHubRepo: (id: number) => {
    return api.delete(`/sync/github/${id}`)
  },

  deleteGiteeRepo: (id: number) => {
    return api.delete(`/sync/gitee/${id}`)
  }
}
