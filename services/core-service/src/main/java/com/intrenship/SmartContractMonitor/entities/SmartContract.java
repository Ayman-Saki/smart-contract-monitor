package com.intrenship.SmartContractMonitor.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "smart_contracts")
public class SmartContract extends BaseEntity{

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 255)
    private String address ;

    @Column(nullable = false)
    private String blockchainType;

    @OneToMany(
            mappedBy = "smartContract",
            cascade = CascadeType.ALL
    )
    private List<Event> events = new ArrayList<>();


}
