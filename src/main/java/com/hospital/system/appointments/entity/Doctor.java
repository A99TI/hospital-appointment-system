package com.hospital.system.appointments.entity;


import jakarta.persistence.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Table(name = "doctors")
@Entity
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 100)
    private String specialisation;

    @Column(nullable = false, length = 100)
    private String roomNumber;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean active;

}
