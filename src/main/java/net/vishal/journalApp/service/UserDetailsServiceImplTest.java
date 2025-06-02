//package net.vishal.journalApp.service;
//
//import net.vishal.journalApp.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach; // For setup if needed, but not strictly required with @InjectMocks
//import org.junit.jupiter.api.Test; // Import @Test
//import org.junit.jupiter.api.extension.ExtendWith; // Import ExtendWith
//import org.mockito.InjectMocks; // Import InjectMocks
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension; // Import MockitoExtension
//
//import static org.bson.assertions.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNotNull; // Example assertion
//
//@ExtendWith(MockitoExtension.class) // This is the key for JUnit 5
//public class UserDetailsServiceImplTest {
//
//    @InjectMocks // Mockito will try to inject 'userRepository' into this instance
//    private UserDetailsServiceImpl userDetailsService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    // You can use @BeforeEach if you have complex setup, but @InjectMocks handles simple injection
//    // @BeforeEach
//    // public void setUp() {
//    //    // userDetailsService = new UserDetailsServiceImpl(userRepository); // If not using @InjectMocks
//    // }
//
//    @Test // Mark this as a JUnit test method
//    public void loadUserByUserNameTest() {
//        // Verify that the mocks and service are initialized
//        assertNotNull(userDetailsService);
//        assertNotNull(userRepository);
//
//        // Now you can define behavior for userRepository and test userDetailsService
//        // Example:
//        // User user = new User(); // Assuming a User class
//        // user.setUsername("testuser");
//        // when(userRepository.findByUserName("testuser")).thenReturn(user);
//
//        // UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");
//        // assertEquals("testuser", userDetails.getUsername());
//    }
//}