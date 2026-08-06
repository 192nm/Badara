import { useState } from 'react'

type Props = {
  onLogin: (username: string, password: string) => Promise<void>
}

export function LoginForm({ onLogin }: Props) {
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin1234')
  const [loading, setLoading] = useState(false)

  return (
    <form
      className="card"
      onSubmit={async (event) => {
        event.preventDefault()
        setLoading(true)
        try {
          await onLogin(username, password)
        } finally {
          setLoading(false)
        }
      }}
    >
      <h2>Badara 로그인</h2>
      <input value={username} onChange={(event) => setUsername(event.target.value)} placeholder="아이디" />
      <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" placeholder="비밀번호" />
      <button disabled={loading}>{loading ? '로그인 중...' : '로그인'}</button>
    </form>
  )
}
