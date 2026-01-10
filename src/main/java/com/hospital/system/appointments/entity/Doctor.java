package com.hospital.system.appointments.entity;


import jakarta.persistence.*;

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
    private boolean active = true;

    public Doctor() {
    }

    public Doctor(User user, String fullName, String specialisation, String roomNumber, Boolean active) {
        this.user = user;
        this.fullName = fullName;
        this.specialisation = specialisation;
        this.roomNumber = roomNumber;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSpecialisation() {
        return specialisation;
    }

    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
