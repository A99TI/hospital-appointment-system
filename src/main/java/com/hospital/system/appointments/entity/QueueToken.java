package com.hospital.system.appointments.entity;

import com.hospital.system.appointments.enums.QueueTokenStatus;
import jakarta.persistence.*;

import java.time.LocalDate;

@Table(name = "queueToken")
@Entity
public class QueueToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Column(nullable = false)
    private Integer tokenNumber;

    @Column(nullable = false)
    private LocalDate queueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QueueTokenStatus status;

    public QueueToken() {
    }

    public QueueToken(Doctor doctor, Appointment appointment, Integer tokenNumber, LocalDate queueDate, QueueTokenStatus status) {
        this.doctor = doctor;
        this.appointment = appointment;
        this.tokenNumber = tokenNumber;
        this.queueDate = queueDate;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Integer getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(Integer tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public LocalDate getQueueDate() {
        return queueDate;
    }

    public void setQueueDate(LocalDate queueDate) {
        this.queueDate = queueDate;
    }

    public QueueTokenStatus getStatus() {
        return status;
    }

    public void setStatus(QueueTokenStatus status) {
        this.status = status;
    }
}
