package com.example.demo.Controller;

import com.example.demo.entity.Board;
import com.example.demo.Repository.BoardRepository;
import com.example.demo.Service.CommentService;
import com.example.demo.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/boards/article")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BoardRepository boardRepository;
    @PostMapping("/{boardId}/comment")
    public String addComment(@PathVariable("boardId") Long boardId, @RequestParam String password, @RequestParam String text) {
        Board board= boardRepository.findById(boardId).orElse(null);
        if (board==null) {
            return "redirect:/boards/article/{boardId}" ;
        }

        // 댓글 생성 및 저장
        commentService.createComment(board, password, text);

        return "redirect:/boards/article/" + boardId;
    }

    @PostMapping("/{boardId}/comment/{commentId}/delete")
    public String deleteComment(@PathVariable("commentId") Long commentId,
                                @PathVariable("boardId") Long boardId,
                                @RequestParam("password") String password) {
        Comment comment = commentService.readComment(commentId);
        if (comment != null && comment.getPassword().equals(password)) {
            commentService.delete(commentId);
            return "redirect:/boards/article/" + boardId;
        }
        return "redirect:/boards/article/" + boardId;
    }





}
