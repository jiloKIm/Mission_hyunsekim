package com.example.demo.board;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardService {

private final BoardRepository boardRepository;

public BoardService(BoardRepository boardRepository){

    this.boardRepository=boardRepository;
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

public Board readBoard(Long id){

    return boardRepository.findById(id).orElse(null);
}

public List<Board> readBoardAll(){

    return boardRepository.findAll();
}






}
