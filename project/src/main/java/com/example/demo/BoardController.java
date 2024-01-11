package com.example.demo;

import com.example.demo.board.Board;
import com.example.demo.board.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/board")
public class BoardController {
    private final BoardService boardService;

    public BoardController (BoardService boardService) {
        this.boardService= boardService;
    }
    @GetMapping
    public String listBoard(Model model){
        List<Board> Blist=  boardService.readBoardAll();
        model.addAttribute("board", Blist);
        return "board/view";

    }
    @GetMapping("/edit")
    public String BoardEdit() {
        return "board/edit";
    }
    @PostMapping("/edit")
    public String BoardCreate(
            @RequestParam("title") String title,
            @RequestParam("category") String  category,
            @RequestParam("context") String context,
            @RequestParam("password") String password
    ) {
        boardService.createPost(title,category,context,password);
        return "redirect:/board";
    }
    @GetMapping("/{id}")
    public String viewPost(@PathVariable("id") Long id,Model model) {
        model.addAttribute("board", boardService.readBoard(id));
        return "board/post";
    }
    
}
