package ru.andreyszdlv.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "t_temp_friends")
@Data
public class TempFriend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "t_user_id", nullable = false)
    private Long userId;

    @Column(name = "t_friend_id", nullable = false)
    private Long friendId;
}