package com.example.demo.entity;

import com.example.demo.entity.Board;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;
    @ManyToOne
    @JoinColumn(name="board_id")
    private Board board;
    private String password;
    private String text;

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPassword(String password) {
        this.password= password;
    }

    public void setText(String text) {
        this.text = text;
    }

}
