package com.ayds.Cloudmerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ayds.Cloudmerce.model.dto.PasswordChangeDto;
import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PatchMapping("/{userId}/password")
    public ResponseEntity<UserDto> changePassword(@PathVariable @Positive @NotNull Long userID,
            @RequestBody @Valid PasswordChangeDto user) {
        UserDto dbUser = userService.changeUserPassword(userID, user.password(), user.repeatedPassword());

        return ResponseEntity.ok(dbUser);
    }
}
