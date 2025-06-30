package com.genai.codeiumapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MyProgressDailyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private double salesInKg;
    private double amountOfPackagingPurchased;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_userId")
    private User user;
    private LocalDate date;
}
