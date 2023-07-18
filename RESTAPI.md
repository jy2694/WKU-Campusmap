### User

* Sign In
  * URL : /auth/signin
  * Method : POST
  * Parameter : ID, PW
  * Return : Name(StudentId)
    * 실패 시 401 Unauthenticated 상태코드 반환
    * 로그인 성공 시 이름과 학번 반환
* Sign Out
  * URL : /auth/signout
  * Method : POST
  * Parameter : -
  * Return : -
    * 호출 시 세션 만료.
* Get Subjects
  * URL : /auth/subjects
  * Method : GET
  * Parameter : -
  * Return : Subject List
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 과목 객체를 리스트 형태로 반환 객체 내부의 값은 아래와 같음.

      | key           | description             |
      |---------------|-------------------------|
      | division      | 과목 구분(선전, 필전, 교필, 교선 등) |
      | subjectId     | 학수 번호                   |
      | subjectName   | 과목 이름                   |
      | subjectNum    | 과목 분반                   |
      | credit        | 학점                      |
      | time          | 시간                      |
      | prof          | 교수명                     |
      | studentNumber | 수강인원                    |
      | locations     | 강의실(List 형식 반환)         |

* Get TimeTable
  * URL : /auth/timetable
  * Method : GET
  * Parameter : -
  * Return : Two-dimensional Subject Array
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 행은 요일 번호(월요일;0번 ~ 금요일;4번), 열은 교시 번호(1교시;0번 ~ 9교시;8번)

* Get Today Timetable
  * URL : /auth/today
  * Method : GET
  * Parameter : -
  * Return : Subject Array
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * Index는 교시 번호(1교시;0번 ~ 9교시;8번)

***

### Article

* Create Article
  * URL : /article/post
  * Method : POST
  * Parameter : title, content, files
  * Return : Article 객체
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 성공적으로 등록되면 Article 객체가 반환
      * Article 객체 내부의 값은 아래와 같음.

        | key           | description     |
        |---------------|-----------------|
        | id            | 게시글의 고유 번호      |
        | title         | 게시글의 제목         |
        | content       | 게시글의 내용         |
        | createAt      | 게시글 등록 일자       |
        | author        | 게시글 등록자         |

* Update Article
  * URL : /article/update
  * Method : POST
  * Parameter : title, content, files, exists
  * Return : Article 객체
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 게시글 등록자와 상이한 계정일 경우 403 Forbidden 상태코드 반환
    * 성공적으로 수정되면 수정된 Article 객체가 반환

* Delete Article
  * URL : /article/delete
  * Method : POST
  * Parameter : id
  * Return : -
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 게시글 등록자와 상이한 계정일 경우 403 Forbidden 상태코드 반환
    * 정상적으로 삭제된 경우 202 Accept 상태코드 반환

* Get Attachments
  * URL : /article/attachment
  * Method : POST
  * Parameter : id
  * Return : String list
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 존재하지 않는 게시글일 경우 404 Not Found 상태코드 반환
    * 파일에 접근 가능한 URL 을 리스트 형태로 반환

* Get Article
  * URL : /article/view
  * Method : GET
  * Parameter : id
  * Return : Article 객체
    * 로그인 되어있지 않은 경우 401 Unauthenticated 상태코드 반환
    * 존재하지 않는 게시글일 경우 404 Not Found 상태코드 반환

***

### Attachment

* Get Attachment
  * URL : /files/로컬파일명
  * Method : GET
  * Parameter : -
  * Return : File
    * 존재하지 않는 파일일 경우 404 Not Found 상태코드 반환
    * 해당 파일을 즉시 다운로드