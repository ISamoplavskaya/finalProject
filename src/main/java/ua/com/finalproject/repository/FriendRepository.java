package ua.com.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.com.finalproject.entity.Friend;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findByUserUsername(String username);

    @Query("SELECT f FROM Friend f WHERE f.user.username = :username AND f.birthday = :tomorrow")
    List<Friend> findFriendsWithBirthdayTomorrowForUser(@Param("username") String username, @Param("tomorrow") LocalDate tomorrow);
}