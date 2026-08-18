package com.pastry;

/**
 * Creates welcome messages using user information supplied by a repository.
 */
public class WelcomeService {
    private final UserRepository userRepository;

    public WelcomeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String createWelcomeMessage(long userId) {
        String name = userRepository.findNameById(userId);
        return "Welcome, " + name + "!";
    }
}
