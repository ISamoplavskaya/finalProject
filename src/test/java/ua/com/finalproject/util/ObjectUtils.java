package ua.com.finalproject.util;

import lombok.experimental.UtilityClass;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.GiftIdea;
import ua.com.finalproject.entity.Role;
import ua.com.finalproject.entity.User;

import java.time.LocalDate;
import java.util.ArrayList;

@UtilityClass
public class ObjectUtils {
    public static User getUser(String username) {
        return User.builder()
                .username(username)
                .password("p1")
                .role(Role.ROLE_USER)
                .friends(new ArrayList<>())
                .email(username + "@").build();
    }

    public static Friend getFriend(String name, User user) {
        return Friend.builder()
                .name(name)
                .birthday(LocalDate.parse("2000-01-01"))
                .user(user)
                .giftIdeas(new ArrayList<>())
                .build();
    }

    public static GiftIdea getGiftIdea(String giftName, Friend friend) {
        return GiftIdea.builder()
                .giftName(giftName)
                .description("description")
                .price(100.00)
                .friend(friend)
                .build();
    }
}
