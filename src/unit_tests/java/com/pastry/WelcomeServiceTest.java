package com.pastry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WelcomeServiceTest {
    @Mock
    private UserRepository userRepository;

    @Test
    void createWelcomeMessageUsesTheNameFromTheRepository() {
        when(userRepository.findNameById(42L)).thenReturn("Maria");
        WelcomeService welcomeService = new WelcomeService(userRepository);

        String message = welcomeService.createWelcomeMessage(42L);

        assertEquals("Welcome, Maria!", message);
        verify(userRepository).findNameById(42L);
    }
}
