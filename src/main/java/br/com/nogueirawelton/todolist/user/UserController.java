package br.com.nogueirawelton.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

  @Autowired
  private IUserRepository userRepository;
  
  @PostMapping("")
  public ResponseEntity<?> create(@RequestBody UserModel user) {
      var userAlreadyExists = this.userRepository.findByUsername(user.getUsername());

      if(userAlreadyExists != null) {
        System.out.println("User Already Exists!");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Already Exists");
      }

      var encryptedPassword = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
      user.setPassword(encryptedPassword);

      var createdUser = this.userRepository.save(user);
      return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
  }
}
