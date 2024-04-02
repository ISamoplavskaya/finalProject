package ua.com.finalproject.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.com.finalproject.entity.Friend;
import ua.com.finalproject.entity.User;
import ua.com.finalproject.service.EmailService;
import ua.com.finalproject.service.FriendService;
import ua.com.finalproject.service.UserService;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BirthdayReminderScheduler {
    private final UserService userService;
    private final FriendService friendService;
    private final EmailService emailService;


    //    @Scheduled(cron = "0 0 0 * * ?") // запускати о 00:00 щодня
    @Scheduled(fixedDelay = 20L, timeUnit = TimeUnit.MINUTES)
    public void remindFriendsAboutBirthdays() {
        log.info("Starting birthday reminder scheduler");
        List<User> users = userService.getAllUsers();
        for (User user : users) {
            log.info("Checking friends for user: {}", user.getUsername());
            List<Friend> friendsWithBirthdayTomorrow = friendService.getFriendsWithBirthdayTomorrowForUser(user.getUsername());
            if (!friendsWithBirthdayTomorrow.isEmpty()) {
                sendBirthdayNotification(user, friendsWithBirthdayTomorrow);
            }
        }
    }

    public void sendBirthdayNotification(User user, List<Friend> friends) {
        log.info("Sending birthday notification to user: {}", user.getUsername());
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Your friends have birthdays tomorrow:\n");
        for (Friend friend : friends) {
            messageBuilder.append("- ")
                    .append(friend.getName())
                    .append("\n");
        }
        messageBuilder.append("Don't forget to congratulate");
        String message = messageBuilder.toString();
        emailService.sendEmail(user.getEmail(), "A reminder about a friend's birthday", message);
        log.info("Birthday notification sent successfully to user: {}", user.getUsername());
    }
}
