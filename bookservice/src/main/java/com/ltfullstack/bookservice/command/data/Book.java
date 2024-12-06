package com.ltfullstack.bookservice.command.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "books")
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue (strategy = GenerationType.UUID) //uuid là một chuỗi dài và không giống bất kỳ chuỗi nào
    private String id;
    private String name;
    private String author;
    private boolean isReady;
}
