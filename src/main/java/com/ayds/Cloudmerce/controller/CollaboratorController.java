package com.ayds.Cloudmerce.controller;

import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.dto.callaborator.PermissionDTO;
import com.ayds.Cloudmerce.model.dto.callaborator.UserUpdateDTO;
import com.ayds.Cloudmerce.service.CallaboratorService;
import com.ayds.Cloudmerce.service.CartResponseService;
import com.ayds.Cloudmerce.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/callaborator")
public class CollaboratorController {

    @Autowired
    private CallaboratorService collaboratorService;
    @Autowired
    private CartResponseService cartResponseService;
    @Autowired
    private RoleService roleService;

    @PutMapping
    public ResponseEntity<Object>updateUserPermissionRole(@RequestBody UserUpdateDTO updateDTO) {
        this.collaboratorService.updateUserPermissionRole(updateDTO);
        return this.cartResponseService.responseSuccess(updateDTO,"all permission", HttpStatus.OK);
    }

    @GetMapping("/permissions")
    public ResponseEntity<Object> getAllPermissions() {
        List<PermissionDTO> permissions = this.collaboratorService.getAllPermision();
        return this.cartResponseService.responseSuccess(permissions,"all permission", HttpStatus.OK);
    }

    @GetMapping("/users/{roleId}")
    public ResponseEntity<Object>  getUsersByRole(@PathVariable Long roleId) {
        return this.cartResponseService.responseSuccess(this.collaboratorService.getUsersByRoleId(roleId),"all permission", HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Object>  getAllRoles() {
        return this.cartResponseService.responseSuccess(this.roleService.findAllRoles(),"all permission", HttpStatus.OK);
    }

}
