import axios from 'axios'

export const http = axios.create({
  withCredentials: true,
})

export interface MeResponse {
  oidc: {
    sub: string
    username: string
    name: string
  }
  localUser: {
    userCode: string
    username: string
    nickname: string
  }
  accessToken: string
  idToken: string
}

export async function fetchMe(): Promise<MeResponse> {
  const { data } = await http.get<MeResponse>('/api/me')
  return data
}

export async function logout(): Promise<void> {
  await http.post('/api/logout')
}
