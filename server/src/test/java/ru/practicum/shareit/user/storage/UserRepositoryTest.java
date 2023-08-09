package ru.practicum.shareit.user.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.TypedQuery;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {
    private final TestEntityManager em;
    private final UserRepository userRepository;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void verifyRepositoryByPersistingAnUser() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setName("test");

        Assertions.assertNull(user.getId());
        User newUser = userRepository.save(user);
        Assertions.assertNotNull(newUser.getId());

        TypedQuery<User> query = em.getEntityManager().createQuery("select u from User u where u.id = :id", User.class);
        User foundUser = query.setParameter("id", newUser.getId()).getSingleResult();

        Assertions.assertEquals(foundUser.getId(), newUser.getId());
    }
}
