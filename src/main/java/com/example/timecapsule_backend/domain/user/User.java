package com.example.timecapsule_backend.domain.user;

import com.example.timecapsule_backend.domain.base.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    public User(Long id, String email, Role role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public User(String username, String email, String password, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String username, String email, String password, LocalDate birthDate, String phoneNumber, Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    public void updateMyPage(String username, LocalDate birthDate, String phoneNumber) {
        this.username = username;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;

    }
}
