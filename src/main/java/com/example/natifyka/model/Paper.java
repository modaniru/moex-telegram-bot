package com.example.natifyka.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@Entity
@Table
@NoArgsConstructor
@SuperBuilder
public class Paper {
    @Id
    @GeneratedValue
    private Long id;
    private String engine;
    private String market;
    private String boardGroups;
    private String security;
    private Long observedCount;
    private Double coefficient;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Subscriber subscriber;

    public String getDescription(){
        return id + ".\nengine: " + engine +
                "\nmarket: " + market +
                "\nboard groups: " + boardGroups +
                "\nsecurity: " + security +
                "\nobserved count: " + observedCount +
                "\ncoefficient: " + coefficient + "\n";
    }
}
