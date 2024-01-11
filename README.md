# 게시판 프로젝트

## 프로젝트 소개
'게시판 프로젝트'는 사용자가 익명으로 게시물을 작성하고 댓글을 통해 의견을 교환할 수 있는 시스템.

## 기능
- **게시판 엔터티**: 게시판에 대한 기본 정보를 관리합
- **카테고리**: 게시판은 '자유게시판', '개발 게시판', '사건사고 게시판', '일상 게시판'의 네 가지 카테고리로 구분
- **익명 게시물 작성**: 사용자는 익명으로 게시물을 작성
- **댓글 시스템**: 게시물에 대한 댓글을 통해 의견을 교환

## 게시판 엔터티

### 엔터티 생성 
엔터티 객체를 통해 데이터베이스에 저장을한다. 
```java
package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String context;
    private String category;
    private String password;
   
}
```


## 게시판  생성 기능 


### HTML Form
사용자가 폼에 데이터를 입력하고 '작성하기' 버튼을 클릭하면, 데이터는 `action="/boards/article"` 속성에 정의된 URL로 POST 요청을 통해 전송

```html
<form action="/boards/article" method="post" class="form-group">
    <!-- 입력 필드들... -->
</form>
```

### 스프링 부트 컨트롤러 (`@PostMapping`)
스프링 부트의 컨트롤러는 HTTP 요청을 처리한다. `@PostMapping("/article")` 어노테이션은 `/boards/article` 경로로 오는 POST 요청을 처리하도록 지정하고 전달된다. 폼 데이터는 `@RequestParam`을 통해 메소드의 파라미터로 전달된다. `@RequestParam` 은 (html-> 문서) !

```java
@PostMapping("/article")
public String createPost(
    @RequestParam("title") String title,
    @RequestParam("context") String context,
    @RequestParam("category") String category,
    @RequestParam("password") String password
){
    boardService.createPost(title, category, context, password);
    return "redirect:/boards/article/all";
}
```

### 서비스 레이어 (`BoardService`)
데이터 베이스와 연결하는 비서라고 생각하자. `BoardService` 클래스는 실제 비즈니스 로직을 담당하며, . 이 클래스에서는 `BoardRepository`를 사용하여 CRUD 를 편리하게 제공한다 . `createPost` 메소드는 컨트롤러로부터 전달받은 게시글 데이터를 사용하여 새 `Board` 인스턴스를 만들고, 이를 데이터베이스에 저장하게 한다. 

```java
public void createPost(String title, String category, String context, String password){
    Board board = new Board();
    board.setTitle(title);
    board.setCategory(category);
    board.setContext(context);
    board.setPassword(password);
    boardRepository.save(board);
}
```

단계 
1. HTML 폼을 통해 게시글 데이터를 입력.
2. 폼을 제출하면, 데이터는 `/boards/article` URL로 POST 요청을 통해 전송.
3.   컨트롤러의 `createPost` 메소드가 이 요청을 받아 처리한다.  이때, `@RequestParam`을 통해 폼 데이터가 메소드 파라미터로 전달하여 폼을 받을 수 있다.
4. 컨트롤러는 `BoardService`의 `createPost` 메소드를 호출하면서 게시글 데이터를 전달한다.
5. `BoardService`에서는 전달받은 데이터로 `Board` 엔터티를 생성하고, `boardRepository`를 통해 데이터베이스에 저장.

## 전체 게시판 기능 


### HTML 템플릿 (Thymeleaf 사용)
HTML 템플릿은 Thymeleaf를 사용하여 서버에서 전달된 데이터를 표시. Thymeleaf는 `th:each`를 사용하여 서버로부터 받은 게시물 목록을 반복해서 표시하며, `th:text`를 통해 각 게시물의 제목과 카테고리를 표시.

```html
<ul class="list-group">
    <li class="list-group-item" th:each="board : ${board}">
        <a th:href="@{/boards/article/{id}(id=${board.id})}" class="text-decoration-none">
            <h5 th:text="${board.title}">게시물 제목</h5>
            <p>카테고리: <span th:text="${board.category}"></span></p>
        </a>
    </li>
</ul>
```

### 스프링 MVC 컨트롤러
`@GetMapping("/article/all")` 어노테이션을 사용한 컨트롤러 메소드는 카테고리별 또는 모든 게시물을 조회하는 요청을 처리. `@RequestParam`을 사용하여 요청에서 카테고리를  옵션으로 선택한다.   이후에는 `BoardService`를 통해 해당 카테고리의 게시물을 찾거나 모든 게시물을 가져온다.

```java
@GetMapping("/article/all")
public String viewAllByCategory(@RequestParam(required = false) String category, Model model) {
    List<Board> boards;
    if (category != null && !category.isEmpty()) {
        boards = boardService.findAllByCategory(category);
    } else {
        boards = boardService.findAll();
    }
    model.addAttribute("board", boards);
    return "board/allist";
}
```

### 서비스 레이어 (`BoardService`)
`BoardService` 클래스는 `BoardRepository`를 사용하여 데이터베이스에서 게시물을 조회한다. `findAllByCategory` 메소드는 특정 카테고리의 게시물을 찾고, `findAll` 메소드는 모든 게시물을 내림차순(ID 기준)으로 조회한다.

```java
public List<Board> findAllByCategory(String category) {
    return boardRepository.findByCategory(category);
}

public List<Board> findAll() {
    return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
}
```

### 데이터 흐름 요약
1. 사용자는 웹 페이지에서 카테고리별 게시판 링크를 클릭하거나 '전체 게시판'을 요청.
2. 클릭한 링크에 따라, 스프링 MVC 컨트롤러의 `viewAllByCategory` 메소드가 호출. 이 메소드는 선택한 카테고리에 해당하는 게시물을 검색.
3. `BoardService`는 `BoardRepository`를 통해 데이터베이스에서 해당하는 게시물을 조회. `findAllByCategory`는 특정 카테고리의 게시물을, `findAll`은 모든 게시물을 내림차순으로  가져온다.(정렬 기능 재설명)
4. 조회된 게시물 목록은 모델을 통해 뷰에 전달된다. 



## 단일 게시물 조회 

### 스프링 MVC 컨트롤러 

`BoardController`
```java
@GetMapping("/article/{id}")
public String viewPost(@PathVariable("id") Long id, Model model) {
    Board board = boardService.readBoard(id);
    if (board != null) {
        model.addAttribute("board", board);
        List<Comment> comments = commentService.findAll(id);
        model.addAttribute("comments", comments);
        return "board/article/view";
    }
    return "redirect:/boards/article/all";
}
```

 `@PathVariable("id") Long id`는 URL에서 게시물의 ID를 추출하여, 호출하여 해당 ID의 게시물 데이터를 가져온다 그리고 , `commentService.findAll(id)`를 호출하여 해당 게시물의 모든 댓글을 가져오고  `model.addAttribute`를 사용하여 게시물과 댓글 데이터를 뷰에 전달하여 보여준다. 


### 2. HTML 뷰 - Thymeleaf 템플릿 사용


```html
<div class="container mt-4">
    <h1 th:text="${board.title}">게시판 제목</h1>
    <p>내용: <span th:text="${board.context}"></span></p>
    <p>카테고리: <span th:text="${board.category}"></span></p>
    <!-- ... 게시물 수정 및 삭제 폼 ... -->
    <h3>댓글</h3>
    <div class="list-group">
        <th:block th:each="comment : ${comments}">
            <!-- 댓글 내용 및 삭제 폼 -->
        </th:block>
    </div>
    <!-- ... 코멘트 작성 폼 ... -->
</div>
```

 `th:text` 속성을 사용하여 모델에서 전달받은 게시물의 제목, 내용, 카테고리를 표시하고  `th:each`를 사용하여 게시물에 대한 모든 댓글을 반복적으로 표시. 

### 3. `BoardService` - 데이터 처리

`BoardService`는 데이터베이스와의 상호작용한다 . 

```java
public Board readBoard(Long id){
    return boardRepository.findById(id).orElse(null);
}
```
`BoardRepository`의 `findById` 메소드를 사용하여 특정 ID의 게시물을 데이터베이스에서 조회하고  조회된 게시물을 반환.

 게시물이 존재하지 않으면 `null`을 반환.


## 정렬기능 

구글에서 검색하여  아이디 순 대로 정렬할 수 있는 레포짓토리를 만들었다. 


### 1. `BoardRepository`에 정렬 기능 추가

`BoardRepository`에 카테고리별로 정렬된 게시물을 가져오는 메서드를 추가하여  이 메서드는 `Sort` 객체를 사용하여 정렬 순서를 지정한다.

**BoardRepository.java:**

```java
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 카테고리별로 정렬된 게시물 리스트를 반환하는 메서드
    List<Board> findAllByCategory(String category, Sort sort);
}
```

### 2. 서비스 계층에서 정렬 로직 구현

`BoardService` 클래스에 `findAllByCategory` 메서드를 구현하여, 특정 카테고리에 속하는 게시물을 정렬 순서에 따라 가져온다

**BoardService.java:**

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BoardService {
    
    @Autowired
    private BoardRepository boardRepository;


    public List<Board> findAllByCategorySorted(String category, String sortField, Sort.Direction direction) {
        Sort sort = Sort.by(direction, sortField);
        return boardRepository.findAllByCategory(category, sort);
    }
}
```

### 3. 컨트롤러에서 정렬된 게시물 리스트 요청 처리

컨트롤러에서는 요청 매개변수를 사용하여 사용자가 선택한 정렬 기준에 따라 서비스 계층의 메서드를 호출.

**BoardController.java:**

```java
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/boards/article")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/all")
    public String viewAllByCategorySorted(@RequestParam(required = false) String category,
                                          @RequestParam(required = false, defaultValue = "createdAt") String sortField,
                                          @RequestParam(required = false, defaultValue = "DESC") Sort.Direction direction,
                                          Model model) {
        List<Board> boards = boardService.findAllByCategorySorted(category, sortField, direction);
        model.addAttribute("board", boards);
        return "board/allist";
    }
}
```





## 코멘트 생성기능 


### HTML 템플릿 (Thymeleaf 사용)

th:each: 이것은 반복문과 유사하게 작동. ${comments}는 DB안의  댓글 목록이며, th:each는 이 목록의 각 comment 객체에 대해 HTML 블록(여기서는 th:block)을 반복한다.


```html
<!-- 게시물 내용 표시 -->
<h1 th:text="${board.title}">게시판 제목</h1>
<!-- 댓글 목록 -->
<th:block th:each="comment : ${comments}">
    <!-- 댓글 내용 및 삭제 폼 -->
</th:block>
<!-- 새 댓글 작성 폼 -->
<form th:action="@{/boards/article/{id}/comment(id=${board.id})}" method="post">
    <!-- 입력 필드들... -->
</form>
```


### 스프링 MVC 컨트롤러 (`BoardController` 및 `CommentController`)
`BoardController`는 게시물의 조회, 수정, 삭제 `CommentController`는 댓글의 추가 및 삭제를 담당

- `BoardController` 내의 `@GetMapping("/article/{id}")` 메소드는 특정 게시물과 그에 속한 댓글을 조회
- `CommentController` 내의 `@PostMapping("/{boardId}/comment")` 메소드는 새 댓글을 게시물에 추가
- 댓글 삭제는 `@PostMapping("/{boardId}/comment/{commentId}/delete")` 메소드를 통해 처리

### 서비스 레이어 (`CommentService`)
1.`CommentService`는 댓글과 관련된 비즈니스 로직을 처리
    1.`createComment` 메소드는 새 댓글을 생성하고 저장하는 데 사용
    2.`findAll` 메소드는 특정 게시물에 대한 모든 댓글을 조회하는 데 사용



```java
public void createComment(Board board, String password, String text){
    // 댓글 객체 생성 및 저장
}

public List<Comment> findAll(Long id) {
    // 특정 게시물의 댓글 조회
}
```

### 데이터 흐름
1.  특정 게시물을 조회할 때, `BoardController`의 `viewPost` 메소드가 호출되어 해당 게시물 및 댓글을 로드.
2.  새 댓글을 작성하고 제출하면, `CommentController`의 `addComment` 메소드가 호출되어 댓글이 생성되고 저장.
3. 댓글을 삭제하려고 하면, `CommentController`의 `deleteComment` 메소드가 호출되어 해당 댓글이 삭제.
4. 이 모든 데이터는 Thymeleaf 템플릿을 통해 최종 사용자 인터페이스에 표시.



## 갱신기능 



#### HTML (Thymeleaf 템플릿)
사용자는 웹 인터페이스를 통해 게시물 수정을 요청하는데,  post 방식의 form 을 사용하였다. 


```html
<form th:action="@{/boards/article/{id}/update(id=${article.id})}" method="post" class="form-group">
</form>
```

### 2. BoardController
용자가 게시물 수정을 요청하면, BoardController의 @GetMapping("/article/{id}/update") 메소드가 호출.
이 메소드는 BoardService를 통해 ID에 해당하는 게시물을 데이터베이스에서 로드
로드된 게시물 데이터는 Thymeleaf 템플릿으로 전달되어 수정 폼에 표시

#### BoardController
```java
@GetMapping("/article/{id}/update")
public String update(@PathVariable("id") Long id, Model model) {
    Board board = boardService.readBoard(id);
    model.addAttribute("article", board);
    return "Board/article/update";
}
```

### 3. 게시물 수정 요청 처리 (BoardController & BoardService)
게시물을 수정하기 전에, 사용자가 입력한 비밀번호가 기존 게시물의 비밀번호와 일치하는지 확인이는 @PostMapping("/article/{id}/updateavil")에서 처리되며, 비밀번호가 일치하면 수정 폼으로 리다이렉트한다.비밀번호가 일치하지 않으면, 사용자는 원래 게시물 페이지로 리다이렉트된다.

#### BoardController
```java
@PostMapping("/article/{id}/update")
public String update(@PathVariable("id") Long id, 
                     @RequestParam("title") String title, 
                     @RequestParam("context") String context,
                     @RequestParam("category") String category, 
                     @RequestParam("password") String password) {
    boardService.updatePost(id, title, category, context, password);
    return "redirect:/boards/article/{id}";
}
```

#### BoardService
```java
public void updatePost(Long id, String title, String category, String context, String password) {
    Board board = boardRepository.findById(id).orElse(null);
    board.setTitle(title);
    board.setCategory(category);
    board.setContext(context);
    board.setPassword(password);
    boardRepository.save(board);
}
```



# 오류처리 

## 주요 오류 문제들  
주요 오류문제들은 경로문제나 경로 충돌문제에서 발생되어ㅓ다. 

1. 문제: `th:action`의 경로 문제
- **원인**: `th:action` 속성에서 경로 변수가 잘못 설정되어 있었습니다. 이로 인해 Thymeleaf가 URL을 올바르게 해석하지 못하는 문제 
- **수정 방법**: `th:action`의 경로를 `@{/boards/article/{id}/comment(id=${board.id})}`에서 `@{/boards/article/{boardId}/comment(boardId=${board.id})}`로 변경하여, Thymeleaf가 게시물 ID를 정확히 URL에 포함시키도록 수정

 2. 문제: `Board Controller` `CommentController`의 경로 중복 및 잘못된 HTTP 메소드 사용
- **원인**: `CommentController`의 `addComment` 메소드가 `GET` 요청을 처리하도록 설정되어 있었으며, 경로가 `/boards/article/{id}`로 중복
- **수정 방법**: `Board Controller`에 CommentService와 함께 쓰일 수 있도록 수정 처리 

3. 문제: `CommentService`에서 댓글을 데이터베이스에 저장하지 않음
- **원인**: `CommentService`의 `createComment` 메소드는 `Comment` 객체를 생성하고 설정했지만, 실제로 데이터베이스에 저장하는 부분이 빠짐. 

- **수정 방법**: `createComment` 메소드 내에서 `commentRepository.save(comment)`를 호출하여 생성된 `Comment` 객체를 데이터베이스에 저장하도록 수정. 

4. 문제: `CommentController`에서 게시물 조회 누락
- **원인**: `addComment` 메소드는 게시물 ID를 통해 `Board` 객체를 조회하지 않고 바로 댓글을 생성하려 했다. 게시물이 실제로 존재하는지 확인하지 않았음 
- **수정 방법**: `boardRepository.findById(boardId)`를 사용하여 먼저 `Board` 객체를 조회하고, 해당 객체가 존재하는 경우에만 댓글을 생성하도록 로직을 변경. 

5. **Form Action URL 불일치**:
   문제: HTML 폼의 `action` URL이 컨트롤러의 URL 패턴과 일치하지 않음.
   
   원인: HTML 폼의 `action` 속성이 `/boards/article/update`로 설정되어 있지만, 컨트롤러는 `/article/{id}/update` URL을 기대함.

   수정 방법:
   - HTML 폼의 `action` 속성을 Thymeleaf를 사용하여 동적으로 설정하고, `{id}` 자리에 `article.id`를 넣도록 함.
   - 예: `<form th:action="@{/article/{id}/update(id=${article.id})}" method="post">`



