import axios from 'axios'

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || 'http://localhost:8123/api'

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
  headers: {
    Accept: 'application/json, text/event-stream, text/plain, */*',
  },
})

export function buildApiUrl(path, params = {}) {
  const cleanBase = API_BASE_URL.replace(/\/$/, '')
  const cleanPath = path.startsWith('/') ? path : `/${path}`
  const query = new URLSearchParams()

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      query.set(key, value)
    }
  })

  const suffix = query.toString() ? `?${query.toString()}` : ''

  if (/^https?:\/\//i.test(cleanBase)) {
    return `${cleanBase}${cleanPath}${suffix}`
  }

  return `${cleanBase}${cleanPath}${suffix}`
}
