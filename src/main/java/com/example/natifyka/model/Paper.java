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
    private long id;
    private String engine;
    private String market;
    private String boardGroups;
    private String security;
    private long observedCount;
    private double coefficient;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Subscriber subscriber;

    public String getDescription() {
        return "Идентификатор: " + id +
                "\nEngine: " + engine +
                "\nMarket: " + market +
                "\nBoard: " + boardGroups +
                "\nSecurity: " + security +
                "\nObserved value: " + observedCount +
                "\nCoefficient: " + coefficient + "\n\n";
    }
}
