package com.hospital.system.appointments.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
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

    @Column(nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false, length = 200)
    private String specialisation;

    @Column(nullable = true, length = 100)
    private String roomNumber;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean active = true;

    public Doctor(User user, String fullName, String specialisation, String roomNumber, Boolean active) {
        this.user = user;
        this.fullName = fullName;
        this.specialisation = specialisation;
        this.roomNumber = roomNumber;
        this.active = active;
    }
}