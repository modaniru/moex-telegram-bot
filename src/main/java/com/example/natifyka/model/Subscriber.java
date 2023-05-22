package com.example.natifyka.model;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@Entity
@Table
@SuperBuilder
@NoArgsConstructor
public class Subscriber {
    @Id
    private long id;
    private long chatId;
    private boolean active;
    @OneToMany(mappedBy = "subscriber")
    private List<Paper> papers;

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", active=" + active +
                '}';
    }
}
