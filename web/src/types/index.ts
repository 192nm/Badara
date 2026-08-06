export type FileItem = {
  fileKey: string
  filename: string
  contentType: string
  size: number
  createdAt: string
  syncStatus: 'SYNCED' | 'UPLOADING' | 'FAILED'
}

export type ShareResponse = {
  token: string
  expiresAt: string
  maxDownloads: number
}
