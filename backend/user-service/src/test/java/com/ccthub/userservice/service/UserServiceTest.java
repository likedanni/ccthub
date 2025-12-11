package com.ccthub.userservice.service;

import com.ccthub.userservice.dto.RegisterRequest;
import com.ccthub.userservice.model.User;
import com.ccthub.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Test
    void registerSuccess() {
        var repo = Mockito.mock(UserRepository.class);
        when(repo.findByPhone("13800000000")).thenReturn(Optional.empty());
        when(repo.save(any(User.class))).thenAnswer(inv -> {
            var u = inv.getArgument(0, User.class);
            u.setId(1L);
            return u;
        });

        var svc = new UserService(repo);
        var req = new RegisterRequest();
        req.phone = "13800000000";
        req.password = "p@ssw0rd";

        var user = svc.registerUser(req);
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getPhone()).isEqualTo("13800000000");
    }

    @Test
    void registerDuplicatePhone() {
        var repo = Mockito.mock(UserRepository.class);
        var existing = new User();
        existing.setId(2L);
        existing.setPhone("13800000000");
        when(repo.findByPhone("13800000000")).thenReturn(Optional.of(existing));

        var svc = new UserService(repo);
        var req = new RegisterRequest();
        req.phone = "13800000000";
        req.password = "p@ssw0rd";

        assertThatThrownBy(() -> svc.registerUser(req)).isInstanceOf(IllegalArgumentException.class);
    }
}
