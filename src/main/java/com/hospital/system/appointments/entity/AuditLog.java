package com.hospital.system.appointments.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "auditLogs")
@Entity
public class AuditLog {

    @Id
    @GeneratedValue(strategy =  GenerationType.AUTO)
    @Column(nullable = false)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User User;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private LocalDateTime timeStamp;

    @Column(nullable = false)
    private String ipAddress;
}
