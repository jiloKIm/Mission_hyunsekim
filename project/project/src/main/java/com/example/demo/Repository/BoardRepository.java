package com.example.demo.Repository;

import com.example.demo.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository <Board,Long>{
    List<Board> findByCategory(String category);

}
