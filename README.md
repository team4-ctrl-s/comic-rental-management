# 만화책 대여점 CLI 프로그램

Java, JDBC, MySQL을 이용해 구현한 **만화책 대여점 CLI 관리 프로그램**입니다.  
콘솔 환경에서 만화책 등록/조회/수정/삭제, 회원 등록/조회, 대여/반납/대여 목록 조회 기능을 제공합니다.

---

## 1. 프로젝트 소개

이 프로젝트는 만화책 대여점을 콘솔에서 관리할 수 있도록 만든 CLI 프로그램입니다.

사용자는 명령어를 입력하여 다음 작업을 수행할 수 있습니다.

- 만화책 등록 / 목록 조회 / 상세 조회 / 수정 / 삭제
- 회원 등록 / 목록 조회
- 만화책 대여 / 반납 / 대여 목록 조회
- 도움말 확인 / 프로그램 종료

모든 데이터는 **MySQL 데이터베이스**에 저장되며, **JDBC**를 통해 DB와 연동합니다.

---

## 2. 구현 기능 목록

### 만화책 기능
- 만화책 등록
- 만화책 목록 조회
- 만화책 상세 조회
- 만화책 수정
- 만화책 삭제
    - `is_deleted` 컬럼을 이용한 **Soft Delete 방식** 적용
    - 삭제된 만화책은 목록/상세/수정 대상에서 제외

### 회원 기능
- 회원 등록
- 회원 목록 조회

### 대여 기능
- 만화책 대여
- 만화책 반납
- 대여 목록 조회

### 기타 기능
- 명령어 파싱
- 잘못된 입력값 예외 처리
- DB 연결 및 JDBC CRUD 처리
- 도움말 출력
- 프로그램 종료

---

## 3. 사용 가능한 명령어

| 구분 | 명령어 | 설명 |
|---|---|---|
| 만화책 | `comic-add` | 만화책 등록 |
| 만화책 | `comic-list` | 만화책 목록 조회 |
| 만화책 | `comic-detail [id]` | 특정 만화책 상세 조회 |
| 만화책 | `comic-update [id]` | 특정 만화책 수정 |
| 만화책 | `comic-delete [id]` | 특정 만화책 삭제(soft delete) |
| 회원 | `member-add` | 회원 등록 |
| 회원 | `member-list` | 회원 목록 조회 |
| 대여 | `rent [comicId] [memberId]` | 만화책 대여 |
| 대여 | `return [rentalId]` | 만화책 반납 |
| 대여 | `rental-list` | 대여 목록 조회 |
| 기타 | `help` | 전체 명령어 목록 출력 |
| 기타 | `exit` | 프로그램 종료 |

---

## 4. 프로젝트 구조

```text
src/
├─ Main.java                     ← 프로그램 시작점 (main 메서드 실행)
├─ App.java                      ← 전체 프로그램 실행 및 명령어 처리 로직
├─ Rq.java                       ← 사용자 입력 명령어 파싱 유틸
├─ DBUtil.java                   ← JDBC DB 연결 생성 및 자원 반납 유틸
│
├─ comic/
│   ├─ Comic.java                ← 만화책 데이터 클래스 (Entity / DTO)
│   └─ ComicRepository.java      ← 만화책 관련 DB 처리 (JDBC CRUD)
│
├─ member/
│   ├─ Member.java               ← 회원 데이터 클래스 (Entity / DTO)
│   └─ MemberRepository.java     ← 회원 관련 DB 처리 (JDBC CRUD)
│
└─ rental/
    ├─ Rental.java               ← 대여 기록 데이터 클래스 (Entity / DTO)
    └─ RentalRepository.java     ← 대여 / 반납 관련 DB 처리 (JDBC)
```

---

## 5. 데이터 구조

### Comic (만화책)

```java
class Comic {
    int id;
    String title;
    int volume;
    String author;
    boolean isRented;
    boolean isDeleted;
    String regDate;
}
```

### Member (회원)

```java
class Member {
    int id;
    String name;
    String phone;
    String regDate;
}
```

### Rental (대여)

```java
class Rental {
    int id;
    int comicId;
    int memberId;
    String rentDate;
    String returnDate;
}
```

---

## 6. 데이터베이스 테이블 생성 쿼리

먼저 데이터베이스를 생성한 뒤 아래 테이블을 생성합니다.

```sql
CREATE DATABASE comic_rental;
USE comic_rental;

CREATE TABLE comic (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    volume INT NOT NULL,
    author VARCHAR(100) NOT NULL,
    is_rented BOOLEAN DEFAULT FALSE,
    is_deleted BOOLEAN DEFAULT FALSE,
    reg_date DATE NOT NULL
);

CREATE TABLE member (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20),
    reg_date DATE NOT NULL
);

CREATE TABLE rental (
    id INT AUTO_INCREMENT PRIMARY KEY,
    comic_id INT NOT NULL,
    member_id INT NOT NULL,
    rent_date DATE NOT NULL,
    return_date DATE,

    FOREIGN KEY (comic_id) REFERENCES comic(id),
    FOREIGN KEY (member_id) REFERENCES member(id)
);
```

---

## 7. 실행 방법

### 7-1. DB 준비
1. MySQL 서버를 실행합니다.
2. 위의 SQL을 실행해 `comic_rental` 데이터베이스와 테이블을 생성합니다.

### 7-2. DB 연결 정보 수정
`DBUtil.java`에서 본인 환경에 맞게 DB 접속 정보를 수정합니다.

예시:

```java
private static final String URL =
    "jdbc:mysql://localhost:3306/comic_rental?serverTimezone=Asia/Seoul&useSSL=false";
private static final String USER = "root";
private static final String PASSWORD = "1234";
```

### 7-3. MySQL JDBC 드라이버 준비
프로젝트 실행을 위해 **MySQL Connector/J**가 필요합니다.

예를 들어 아래와 같이 jar 파일을 준비합니다.

```text
project-root/
├─ lib/
│  └─ mysql-connector-j-8.3.0.jar
└─ src/
```

### 7-4. 컴파일 및 실행

#### Windows
```bash
cd src
javac -cp ".;../lib/mysql-connector-j-8.3.0.jar" Main.java App.java Rq.java DBUtil.java comic/*.java member/*.java rental/*.java
java -cp ".;../lib/mysql-connector-j-8.3.0.jar" Main
```

#### macOS / Linux
```bash
cd src
javac -cp ".:../lib/mysql-connector-j-8.3.0.jar" Main.java App.java Rq.java DBUtil.java comic/*.java member/*.java rental/*.java
java -cp ".:../lib/mysql-connector-j-8.3.0.jar" Main
```

> 사용하는 JDBC 드라이버 버전에 따라 jar 파일명은 달라질 수 있습니다.

---

## 8. 예시 실행 흐름

```text
== 만화책 대여점 프로그램 ==

명령어: comic-add
제목: 슬램덩크
권수: 1
작가: 이노우에 다케히코
=> 만화책이 등록되었습니다. (id=1)

명령어: member-add
이름: 홍길동
전화번호: 010-1234-5678
=> 회원이 등록되었습니다. (id=1)

명령어: rent 1 1
=> 대여 완료: [대여id=1] 슬램덩크 → 홍길동

명령어: rental-list
대여번호 | 만화책 | 회원 | 대여일 | 반납일 | 상태
...

명령어: return 1
=> 반납 완료: [대여id=1] 슬램덩크 / 홍길동
```

---

## 9. 삭제 정책

이 프로젝트의 만화책 삭제는 **Soft Delete** 방식으로 처리합니다.

- 실제로 데이터를 DB에서 제거하지 않고
- `comic.is_deleted = TRUE` 로 변경합니다.
- 삭제된 만화책은 `comic-list`, `comic-detail`, `comic-update` 대상에서 제외됩니다.
- 대여 이력은 `rental` 테이블에 그대로 남아 관리됩니다.

---

## 10. 주요 클래스 역할

### `Main.java`
프로그램 실행 진입점입니다.

### `App.java`
전체 명령어 흐름을 제어하고, 각 기능 메서드를 호출합니다.

### `Rq.java`
사용자가 입력한 명령어를 `actionName`과 `args`로 분리합니다.

### `DBUtil.java`
JDBC 드라이버 로드 및 MySQL DB 연결을 담당합니다.

### `ComicRepository.java`
만화책 등록, 조회, 수정, 삭제 관련 DB 작업을 담당합니다.

### `MemberRepository.java`
회원 등록, 목록 조회 등 회원 관련 DB 작업을 담당합니다.

### `RentalRepository.java`
만화책 대여, 반납, 대여 목록 조회와 같은 대여 관련 DB 작업을 담당합니다.

---

## 11. 개선 가능 포인트

- 회원 상세 조회 / 수정 / 삭제 기능 추가
- 대여 가능 목록 / 대여 중 목록 필터링
- 검색 기능 추가
- 입력 UI 개선
- 트랜잭션 처리 강화
- 예외 메시지 정교화

