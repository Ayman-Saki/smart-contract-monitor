package com.intrenship.SmartContractMonitor.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Summary extends BaseEntity {

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String modelName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "event_id",
            nullable = false,
            unique = true
    )
    private Event event;


}
