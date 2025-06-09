package com.example.looksy_ytta.model;

import com.example.looksy_ytta.model.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data 
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all fields
@Entity // Marks this class as a JPA entity
@Table(name = "users") // Specifies the table name (avoid 'user' as it's a reserved keyword in some DBs)
public class User {

    @Id // Marks id as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrementing ID
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255) // Password will be encoded, so a longer length is good
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Enumerated(EnumType.STRING) // Store enum as String in DB
    @Column(nullable = false, length = 20) // Adjust length as needed for 'USER' or 'ADMIN'
    private Role role; // We'll define Role enum shortly



}