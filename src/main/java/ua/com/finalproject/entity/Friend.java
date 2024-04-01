package ua.com.finalproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "giftIdeas")
@Entity
@Table(name = "friends")
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthday")
    private LocalDate birthday;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "friend", cascade = CascadeType.ALL)
    private List<GiftIdea> giftIdeas = new ArrayList<>();

    public void addGiftIdea(GiftIdea giftIdea) {
        giftIdea.setFriend(this);
        giftIdeas.add(giftIdea);
    }

    public void removeGiftIdea(GiftIdea giftIdea) {
        giftIdea.setFriend(null);
        giftIdeas.remove(giftIdea);
    }
}