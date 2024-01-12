package com.example.demo.Controller;

import com.example.demo.Service.BoardService;
import com.example.demo.Service.CommentService;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/boards")
public class BoardController {
    @Autowired
    private CommentService commentService;

    private final BoardService boardService;


    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/article")
    public String BoardPost() {

        return "Board/article";
    }

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

    @PostMapping("/article/{id}/updateavil")
    public String updatePost(
            @PathVariable("id") Long id, @RequestParam("password") String password
            ) {
        Board board = boardService.readBoard(id);
        if (board != null && board.getPassword().equals(password)) {
            return "redirect:/boards/article/{id}/update";
        }
        return "redirect:/boards/article/{id}";
    }


    @GetMapping("/article/{id}/update")
    public String update(@PathVariable("id") Long id,Model model){
        Board board = boardService.readBoard(id);

        model.addAttribute("article",board);

        return "Board/article/update";
    }

    @PostMapping("/article/{id}/update")
    public String update(  @PathVariable("id") Long id,
                           @RequestParam("title") String title,
                           @RequestParam("context") String context,
                           @RequestParam("category") String category,
                           @RequestParam("password") String password){

        boardService.updatePost(id,title, category, context, password);

        return "redirect:/boards/article/{id}";
    }
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


    @PostMapping("/article/{id}/delete")
    public String deletePost(@PathVariable("id") Long id, @RequestParam("password") String password) {
        Board board = boardService.readBoard(id);
        if (board != null && board.getPassword().equals(password)) {
            boardService.delete(id);
            return "redirect:/boards/article/all";
        }
        return "redirect:/boards/article/{id}";
    }

    //전체 보기

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











}
