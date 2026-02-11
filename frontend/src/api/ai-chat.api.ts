import api from './auth.api'

export interface ChatRequest {
  message: string
}

export interface ChatResponse {
  code: number
  message: string
  data: string
}

export const aiChatApi = {
  sendMessage: (message: string) => {
    return api.post<string>('/ai/chat/send', { message })
  }
}
