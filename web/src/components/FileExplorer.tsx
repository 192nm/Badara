import type { FileItem } from '../types'

type Props = {
  files: FileItem[]
  onDownload: (fileKey: string) => Promise<void>
  onShare: (fileKey: string) => Promise<void>
}

export function FileExplorer({ files, onDownload, onShare }: Props) {
  return (
    <div className="card">
      <h3>내 파일</h3>
      <ul>
        {files.map((file) => (
          <li key={file.fileKey}>
            <div>
              <strong>{file.filename}</strong>
              <span>{Math.round(file.size / 1024)} KB · {file.syncStatus}</span>
            </div>
            <div className="actions">
              <button onClick={() => onDownload(file.fileKey)}>다운로드</button>
              <button onClick={() => onShare(file.fileKey)}>공유 링크</button>
            </div>
          </li>
        ))}
      </ul>
    </div>
  )
}
