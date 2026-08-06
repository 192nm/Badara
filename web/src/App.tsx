import { useEffect, useMemo, useState } from 'react'
import { createShareLink, downloadFile, listFiles, login, uploadFile } from './api/client'
import { FileExplorer } from './components/FileExplorer'
import { LoginForm } from './components/LoginForm'
import type { FileItem } from './types'

export function App() {
  const [token, setToken] = useState<string | null>(null)
  const [files, setFiles] = useState<FileItem[]>([])
  const [query, setQuery] = useState('')
  const [status, setStatus] = useState('대기 중')
  const [pendingUploads, setPendingUploads] = useState<string[]>(() => {
    try {
      return JSON.parse(localStorage.getItem('badara_pending_uploads') ?? '[]') as string[]
    } catch {
      return []
    }
  })

  const filtered = useMemo(
    () => files.filter((file) => file.filename.toLowerCase().includes(query.toLowerCase())),
    [files, query],
  )

  useEffect(() => {
    localStorage.setItem('badara_pending_uploads', JSON.stringify(pendingUploads))
  }, [pendingUploads])

  useEffect(() => {
    if (!token) return
    void (async () => {
      const data = await listFiles(token)
      setFiles(data)
    })()
  }, [token])

  useEffect(() => {
    const update = () => setStatus(navigator.onLine ? 'SYNCED' : 'OFFLINE')
    update()
    window.addEventListener('online', update)
    window.addEventListener('offline', update)
    return () => {
      window.removeEventListener('online', update)
      window.removeEventListener('offline', update)
    }
  }, [])

  if (!token) {
    return <LoginForm onLogin={async (username, password) => setToken(await login(username, password))} />
  }

  return (
    <main className="layout">
      <h1>Badara Cloud Storage</h1>
      <p>동기화 상태: {status}</p>
      {pendingUploads.length > 0 && <p>임시 업로드 큐: {pendingUploads.join(', ')}</p>}
      <div className="card">
        <input placeholder="파일 검색" value={query} onChange={(e) => setQuery(e.target.value)} />
        <input
          type="file"
          onChange={async (event) => {
            const file = event.target.files?.[0]
            if (!file) return
            if (!navigator.onLine) {
              setPendingUploads((prev) => [...prev, file.name])
              setStatus('OFFLINE - 임시 저장됨')
              return
            }
            setStatus('UPLOADING')
            try {
              const uploaded = await uploadFile(token, file)
              setFiles((prev) => [uploaded, ...prev])
              setStatus('SYNCED')
              setPendingUploads((prev) => prev.filter((name) => name !== file.name))
            } catch {
              setStatus('FAILED - 재시도 필요')
              setPendingUploads((prev) => [...prev, file.name])
            }
          }}
        />
      </div>

      <FileExplorer
        files={filtered}
        onDownload={async (fileKey) => {
          setStatus('다운로드 중')
          await downloadFile(token, fileKey)
          setStatus('SYNCED')
        }}
        onShare={async (fileKey) => {
          const link = await createShareLink(token, fileKey)
          await navigator.clipboard.writeText(`${window.location.origin}/share/${link.token}`)
          alert('공유 링크가 클립보드에 복사되었습니다.')
        }}
      />
    </main>
  )
}
