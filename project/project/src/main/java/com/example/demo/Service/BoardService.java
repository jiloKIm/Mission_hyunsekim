package com.example.demo.Service;
import com.example.demo.Repository.BoardRepository;
import com.example.demo.entity.Board;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    public void createPost(
            String title,
            String category,
            String context,
            String password
    ){
        Board board= new Board();
        board.setTitle(title);
        board.setCategory(category);
        board.setContext(context);
        board.setPassword(password);
        boardRepository.save(board);

    }
    public void updatePost(Long id,
            String title,
            String category,
            String context,
            String password
    ){
        Board board= boardRepository.findById(id).orElse(null);
        board.setTitle(title);
        board.setCategory(category);
        board.setContext(context);
        board.setPassword(password);
        boardRepository.save(board);

    }
    public Board readBoard(Long id){

        return boardRepository.findById(id).orElse(null);

    }
    public List<Board> findAllByCategory(String category) {
        return boardRepository.findByCategory(category);
    }

    public List<Board> findAll() {


        return boardRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }







}
