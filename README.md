# Badara

Badara는 **Spring Boot + React + Android Kotlin** 기반의 클라우드 저장소 MVP입니다.

## 구성

- `/backend`: 인증(JWT), 파일 메타데이터(RDB), 권한, 공유 링크, 업로드/다운로드 API
- `/web`: 파일 탐색기, 업로드, 검색, 공유 링크 UI
- `/android`: 동일 API를 사용하는 Android 앱 스캐폴드
- `docker-compose.yml`: PostgreSQL + MinIO

## 1차 릴리스(MVP) 범위

- 개인 저장소
- 파일 업로드/다운로드
- 파일 목록/검색
- 공유 링크 생성(만료 시간, 다운로드 횟수 제한)

## 백엔드 실행

```bash
cd /home/runner/work/Badara/Badara

docker compose up -d

cd backend
export DB_URL='jdbc:postgresql://localhost:5432/badara'
export DB_USERNAME='badara'
export DB_PASSWORD='badara'
export STORAGE_MODE='s3'
export S3_ENDPOINT='http://localhost:9000'
export S3_ACCESS_KEY='minioadmin'
export S3_SECRET_KEY='minioadmin'
export S3_BUCKET='badara-files'
export JWT_SECRET='change-this-secret-key-change-this-secret-key'
gradle bootRun
```

기본 관리자 계정:

- username: `admin`
- password: `admin1234`

## 웹 실행

```bash
cd /home/runner/work/Badara/Badara/web
npm install
npm run dev
```

기본 API URL은 `http://localhost:8080`이며 필요 시 `VITE_API_BASE`를 설정합니다.

## Android 실행

```bash
cd /home/runner/work/Badara/Badara/android
# Android Studio에서 프로젝트 열기
```

에뮬레이터 API 기본 URL은 `http://10.0.2.2:8080`입니다.

## UX 정책(초기 적용)

- 공통 동기화 상태: `SYNCING / SYNCED / FAILED`
- 업로드 실패 시 재시도 가능한 상태 노출
- 임시 저장 및 오프라인 큐는 다음 단계에서 확장

## 보안 정책

- JWT 기반 인증
- 파일 owner 접근 제어
- 공유 링크 만료 시간/다운로드 횟수 제한

## 향후 확장

- 팀 공유 권한 모델
- 파일 버전 관리
- 휴지통/복원
- 오프라인 임시 저장 큐 고도화
