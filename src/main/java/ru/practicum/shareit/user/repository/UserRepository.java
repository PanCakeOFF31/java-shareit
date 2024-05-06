package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select new ru.practicum.shareit.user.dto.UserResponseDto(u.id, u.name, u.email) " +
            "from User as u " +
            "where u.id = :id")
    Optional<UserResponseDto> findUserResponseDtoById(@Param("id") final long userId);

    @Query("select new ru.practicum.shareit.user.dto.UserBookingDto(u.id) " +
            "from User as u " +
            "where u.id = :id")
    Optional<UserBookingDto> findUserBookingDtoById(@Param("id") final long userId);

    Optional<User> findUserByEmail(final String email);
}
