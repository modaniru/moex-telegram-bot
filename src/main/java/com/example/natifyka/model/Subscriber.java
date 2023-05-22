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
    private Long id;
    private Long chatId;
    private boolean isActive;
    @OneToMany(mappedBy = "subscriber")
    private List<Paper> papers;
}
