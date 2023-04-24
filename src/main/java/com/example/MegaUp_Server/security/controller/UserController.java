package com.example.MegaUp_Server.security.controller;

import com.example.MegaUp_Server.security.dto.UserDto;
import com.example.MegaUp_Server.security.model.AuthenticationResponse;
import com.example.MegaUp_Server.security.model.CodeKeyModel;
import com.example.MegaUp_Server.security.model.UserViewModel;
import com.example.MegaUp_Server.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody UserDto dto){

        try{
            return ResponseEntity.status(HttpStatus.OK).body(service.login(dto));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/login/access")
    public ResponseEntity<AuthenticationResponse> loginWithToken(@RequestBody AuthenticationResponse auth){

        try{
            if(service.loginWithToken(auth)){
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        catch(IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody UserDto dto){

        try{
            service.register(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch(RuntimeException e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserViewModel>> users(){
        return ResponseEntity.status(HttpStatus.OK).body(service.findAllUsers());
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<String> delete(@PathVariable("username") String username){

        try {
            if (service.deleteByUsername(username)) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        }catch(UsernameNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/user/access")
    public ResponseEntity<String> verify(@RequestBody CodeKeyModel codeKeyModel){

        try {
            if (service.codeKeyAccessVerify(codeKeyModel.getCode())) {
                return ResponseEntity.status(HttpStatus.OK).build();
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        catch(IllegalAccessException e){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
    }
}
