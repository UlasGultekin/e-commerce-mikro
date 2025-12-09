package com.ulasgltkn.userservice.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String title;        // Ev, İş...
    private String fullAddress;
    private String city;
    private String country;
    private String zipCode;

    @Column(name = "is_default")
    private boolean isDefault;
    @CreationTimestamp
    private Date createDate;

    @UpdateTimestamp
    private Date updateDate;

}
