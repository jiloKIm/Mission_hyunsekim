package com.example.demo.Service;

import com.example.demo.Repository.CommentRepository;
import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public void createComment(Board board, String password, String text){
        Comment comment = new Comment();
        comment.setPassword(password);
        comment.setText(text);
        comment.setBoard(board);
        commentRepository.save(comment); // 댓글 저장
    }

    public List<Comment> findAll(Long id) {
        List<Comment> comments = commentRepository.findByBoardId(id);

        return comments;


    }
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }



    public Comment readComment(Long id) {
        return  commentRepository.findById(id).orElse(null);
    }

}

