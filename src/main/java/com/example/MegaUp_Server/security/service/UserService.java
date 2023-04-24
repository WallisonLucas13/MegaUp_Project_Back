package com.example.MegaUp_Server.security.service;

import com.example.MegaUp_Server.security.dto.UserDto;
import com.example.MegaUp_Server.security.enums.RoleName;
import com.example.MegaUp_Server.security.model.AuthenticationResponse;
import com.example.MegaUp_Server.security.model.RoleModel;
import com.example.MegaUp_Server.security.model.UserModel;
import com.example.MegaUp_Server.security.model.UserViewModel;
import com.example.MegaUp_Server.security.repository.UserRepository;
import com.example.MegaUp_Server.services.CreateAttachmentFile;
import com.example.MegaUp_Server.services.SendMailService;
import com.itextpdf.text.DocumentException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Autowired
    private UserRepository repository;

    private static String keyCode;

    private final com.example.MegaUp_Server.security.service.JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    @Autowired
    private CreateAttachmentFile createAttachmentFile;

    @Autowired
    private SendMailService sendMailService;

    @Value("${ADMIN_KEY}")
    private String ADMIN_KEY;

    @Transactional
    public void register(UserDto dto){

        UserModel userModel = dto.toUser();

        boolean exist = repository.existsByUsername(userModel.getUsername());

        if(exist){
            throw new RuntimeException();
        }

        userModel.setPassword(new BCryptPasswordEncoder().encode(userModel.getPassword()));

        if(dto.getWritePermission() == null){
            dto.setWritePermission("false");
        }

        if(dto.getWritePermission().equals("true")) {
            userModel.setRoles(List.of(generateRoleUser()));
        }else{
            userModel.setRoles(List.of(generateRoleRead()));
        }

        repository.save(userModel);

    }

    @Transactional
    public AuthenticationResponse login(UserDto modelDto) throws IllegalArgumentException, DocumentException, IOException, InterruptedException {

        UserModel model = modelDto.toUser();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    model.getUsername(),
                    model.getPassword()
                )
        );

        UserModel userModel = repository.findByUsername(model.getUsername())
                .orElseThrow(IllegalArgumentException::new);

        List<String> roles = userModel.getRoles().stream()
                .map(r -> r.getRoleName().name()).toList();

        if(roles.size() == 2){

                if(!modelDto.getChaveAccess().equals(ADMIN_KEY)){
                    throw new IllegalArgumentException();
                };

                keyCode = this.sendMailService.createMailAndSend();

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000*60*3);
                            keyCode = "";
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
        }

        return AuthenticationResponse
                .builder()
                .token(jwtService.generateToken(userModel))
                .build();
    }

    @Transactional
    public boolean loginWithToken(AuthenticationResponse auth) throws IllegalArgumentException{

        UserDetails userDetails = repository.findByUsername(jwtService.extractUsername(auth.getToken()))
                .orElseThrow(() -> new IllegalArgumentException(""));
        return jwtService.isTokenValid(auth.getToken(), userDetails);
    }
    @Transactional
    public boolean deleteByUsername(String username) throws UsernameNotFoundException{

            UserModel userModel = repository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(""));

            if (filterUsersByRoles(userModel.getRoles())) {
                repository.deleteById(userModel.getId());
                return true;
            }
            return false;
    }

    @Transactional
    public boolean codeKeyAccessVerify(String code) throws IllegalAccessException{

        if(keyCode.isEmpty()){
            throw new IllegalAccessException("");
        }

        return keyCode.equals(code);
    }

    @Transactional
    public List<UserViewModel> findAllUsers(){
        return repository.findAll().stream()
                .filter(user -> filterUsersByRoles(user.getRoles()))
                .map(user -> new UserViewModel(user.getUsername(), viewRoles(user.getRoles())))
                .collect(Collectors.toList());
    }

    private String viewRoles(List<RoleModel> roles){

        List<RoleModel> list = roles.stream()
                .filter(role -> role.getRoleName().name().equals("ROLE_READ")).toList();

        if(list.isEmpty()){
            return "Leitura/Escrita";
        }

        return "Leitura";
    }

    private boolean filterUsersByRoles(List<RoleModel> roles){
        return roles.stream()
                .filter(role -> role.getRoleName().name().equals("ROLE_ADMIN"))
                .collect(Collectors.toList())
                .isEmpty();
    }

    private RoleModel generateRoleUser(){
        RoleModel roleModel = new RoleModel();
        roleModel.setRoleName(RoleName.ROLE_USER);
        return roleModel;
    }

    private RoleModel generateRoleRead(){
        RoleModel roleModel = new RoleModel();
        roleModel.setRoleName(RoleName.ROLE_READ);
        return roleModel;
    }

}
