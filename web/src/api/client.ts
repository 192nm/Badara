import type { FileItem, ShareResponse } from '../types'

const API_BASE = import.meta.env.VITE_API_BASE ?? 'http://localhost:8080'

export async function login(username: string, password: string): Promise<string> {
  const response = await fetch(`${API_BASE}/api/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password }),
  })
  if (!response.ok) throw new Error('로그인 실패')
  const payload = await response.json()
  return payload.accessToken as string
}

export async function listFiles(token: string, q = ''): Promise<FileItem[]> {
  const response = await fetch(`${API_BASE}/api/files${q ? `?q=${encodeURIComponent(q)}` : ''}`, {
    headers: { Authorization: 'Bearer ' + token },
  })
  if (!response.ok) throw new Error('목록 조회 실패')
  return (await response.json()) as FileItem[]
}

export async function uploadFile(token: string, file: File): Promise<FileItem> {
  const formData = new FormData()
  formData.append('file', file)
  const response = await fetch(`${API_BASE}/api/files/upload`, {
    method: 'POST',
    headers: { Authorization: 'Bearer ' + token },
    body: formData,
  })
  if (!response.ok) throw new Error('업로드 실패')
  return (await response.json()) as FileItem
}

export async function createShareLink(token: string, fileKey: string): Promise<ShareResponse> {
  const response = await fetch(`${API_BASE}/api/files/${fileKey}/share`, {
    method: 'POST',
    headers: {
      Authorization: 'Bearer ' + token,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ expiresInHours: 24, maxDownloads: 20 }),
  })
  if (!response.ok) throw new Error('공유 링크 생성 실패')
  return (await response.json()) as ShareResponse
}

export async function downloadFile(token: string, fileKey: string): Promise<void> {
  const response = await fetch(`${API_BASE}/api/files/${fileKey}/download`, {
    headers: { Authorization: 'Bearer ' + token },
  })
  if (!response.ok) throw new Error('다운로드 실패')
  const blob = await response.blob()
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = 'download'
  a.click()
  window.URL.revokeObjectURL(url)
}
