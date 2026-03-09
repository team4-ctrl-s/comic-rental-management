## Git 협업 컨벤션

현재 프로젝트 규모와 진행 단계에서는 **`main` + 작업 브랜치(feature branch)** 전략으로 협업합니다.  
모든 작업은 브랜치를 따서 진행하고, 완료 후 **Pull Request(PR)** 로 병합합니다.

### 1. 브랜치 전략

- `main`
  - 최종 제출/실행 가능한 안정 버전만 유지합니다.
  - 직접 push 하지 않습니다.
- 작업 브랜치
  - 모든 기능 개발, 버그 수정, 문서 작업은 별도 브랜치에서 진행합니다.
  - 작업 완료 후 PR을 통해 `main` 브랜치에 merge 합니다.
  - merge 완료 후 작업 브랜치는 삭제합니다.

---

### 2. 브랜치 네이밍 규칙

형식

`prefix/작업내용`

작성 규칙

- prefix는 아래 표의 값을 사용.
- 작업내용은 **영어 소문자 + 하이픈(-)** 으로 작성.
- 브랜치 1개에는 **하나의 작업 주제만**.
- 너무 넓은 이름보다 기능 단위로 구체적으로 작성.

| prefix | 의미 | 예시 |
| --- | --- | --- |
| `feat` | 새로운 기능 추가 | `feat/comic-add` |
| `fix` | 버그 수정 | `fix/rent-duplicate-check` |
| `refactor` | 기능 변화 없는 구조 개선 | `refactor/comic-repository` |
| `docs` | 문서 수정 | `docs/readme-git-convention` |
| `test` | 테스트 코드 추가/수정 | `test/member-repository` |
| `chore` | 설정, 의존성, 기타 작업 | `chore/mysql-config` |

브랜치 예시

- `feat/comic-add`
- `feat/comic-list`
- `feat/member-add`
- `feat/rent-comic`
- `feat/return-comic`
- `fix/return-validation`
- `refactor/db-util`
- `docs/readme-update`

---

### 3. 커밋 메시지 규칙

형식

`type: 작업 내용`

예시

- `feat: 만화책 등록 기능 추가`
- `feat: 회원 목록 조회 기능 구현`
- `fix: 대여 중복 체크 오류 수정`
- `refactor: ComicRepository 조회 메서드 분리`
- `docs: README에 Git 협업 규칙 추가`
- `chore: MySQL JDBC 설정 추가`

커밋 타입

| type | 의미 |
| --- | --- |
| `feat` | 새로운 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 기능 변화 없는 코드 개선 |
| `docs` | 문서 수정 |
| `style` | 코드 포맷팅, 들여쓰기, 세미콜론 등 스타일 수정 |
| `test` | 테스트 코드 추가/수정 |
| `chore` | 설정, 의존성, 빌드 관련 작업 |

커밋 작성 규칙

- 커밋 메시지는 **무엇을 변경했는지 바로 알 수 있게** 작성.
- `수정`, `업데이트`, `작업중` 같은 모호한 메시지는 X.
- 기능 추가와 리팩토링은 가능하면 분리해서 커밋.
- 실행 가능하고 리뷰 가능한 단위로 자주 커밋.
- 커밋 1개에는 1개의 의미 있는 변경만 담는 것을 원칙.

---

### 4. PR(Pull Request) 규칙

- PR 제목도 커밋 규칙과 동일하게 작성합니다.
  - 예: `feat: 만화책 상세 조회 기능 추가`
- PR 본문에는 아래 내용을 간단히 작성합니다.
  - 작업 내용
  - 테스트 결과
  - 리뷰 포인트
- 팀원 확인 후 merge 하는 것을 원칙으로 합니다.
- merge 완료 후 작업 브랜치는 삭제합니다.

PR 작성 예시

- 작업 내용: 만화책 등록 기능 구현
- 테스트 결과: 등록 후 `comic-list`에서 정상 조회 확인
- 리뷰 포인트: 입력값 검증 방식 확인 요청

---

### 5. 작업 순서

1. 작업할 기능 또는 이슈를 확인.
2. 브랜치를 생성.
3. 기능 개발 후 의미 단위로 커밋.
4. PR을 생성.
5. 리뷰 후 `main` 브랜치에 merge.
6. 작업 브랜치를 삭제.

---

### 7. 프로젝트 기준 예시

브랜치 예시

- `feat/comic-add`
- `feat/comic-detail`
- `feat/comic-update`
- `feat/member-add`
- `feat/member-list`
- `feat/rent-comic`
- `feat/return-comic`
- `feat/rental-list`
- `fix/rental-status-check`
- `refactor/rental-repository`
- `docs/readme-update`

커밋 예시

- `feat: comic-add 명령어 처리 추가`
- `feat: member-add 기능 구현`
- `feat: rent 명령어로 대여 처리 구현`
- `feat: rental-list 출력 기능 추가`
- `fix: 대여 중인 만화책 중복 대여 방지`
- `fix: 이미 반납된 대여 건 예외 처리`
- `refactor: Repository 공통 JDBC 처리 정리`
- `docs: README에 브랜치 및 커밋 컨벤션 추가`

> 위 규칙은 팀 협업을 위한 기본 기준이며, 프로젝트 진행 중 팀 합의에 따라 보완할 수 있습니다.