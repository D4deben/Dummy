package iisc.ac.in.User;
import iisc.ac.in.User.User;
import iisc.ac.in.User.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User request) {
        if (userRepository.findByEmail(request.getEmail()).orElse(null) != null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body(null);
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Integer userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {

        try {

            URL url = new URL("http://host.docker.internal:8082/wallets/"+userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            URL url = new URL("http://host.docker.internal:8081/bookings/users/"+userId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userRepository.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllUsers() {

        try {

            URL url = new URL("http://host.docker.internallocalhost:8082/wallets");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            URL url = new URL("http://host.docker.internal:8081/bookings");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("DELETE");
            connection.getResponseCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userRepository.deleteAll();
        return ResponseEntity.ok().build();
    }

    static class UserRequest {
        private String name;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }


    }
}
