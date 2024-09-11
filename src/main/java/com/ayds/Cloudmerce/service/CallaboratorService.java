package com.ayds.Cloudmerce.service;

import com.ayds.Cloudmerce.model.dto.UserDto;
import com.ayds.Cloudmerce.model.dto.callaborator.PermissionDTO;
import com.ayds.Cloudmerce.model.dto.callaborator.UserUpdateDTO;
import com.ayds.Cloudmerce.model.entity.PermissionEntity;
import com.ayds.Cloudmerce.model.entity.RoleEntity;
import com.ayds.Cloudmerce.model.entity.UserEntity;
import com.ayds.Cloudmerce.model.entity.UserPermissionEntity;
import com.ayds.Cloudmerce.repository.PermissionRepository;
import com.ayds.Cloudmerce.repository.RoleRepository;
import com.ayds.Cloudmerce.repository.UserPermissionRepository;
import com.ayds.Cloudmerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CallaboratorService {

    private final PermissionRepository permissionRepository;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final UserPermissionRepository userPermissionRepository;

    @Transactional
    public UserUpdateDTO updateUserPermissionRole(UserUpdateDTO updateDTO) {
        UserEntity user = userRepository.findById(updateDTO.idUser())
                .orElseThrow(() -> new RuntimeException("User not found"));
        this.updateUserRole(user,updateDTO.role().id());
        if (updateDTO.role().name().equalsIgnoreCase("ayudante")){
            userPermissionRepository.deleteAllByUserId(updateDTO.idUser());
            // Obtener las entidades de permisos
            List<PermissionEntity> permissions = permissionRepository.findAllById(updateDTO.permissions());
            // Crear las entidades de UserPermission
            List<UserPermissionEntity> permissionsToAdd = permissions.stream()
                    .map(permission -> {
                        UserPermissionEntity userPermission = new UserPermissionEntity();
                        userPermission.setUser(user);
                        userPermission.setPermission(permission);
                        return userPermission;
                    })
                    .collect(Collectors.toList());
            // Guardar las nuevas entidades de UserPermission
            userPermissionRepository.saveAll(permissionsToAdd);

        }
        return updateDTO;
    }


    public void updateUserRole(UserEntity user, Long newRoleId) {

        // Encontrar el nuevo rol por su ID
        RoleEntity newRole = roleRepository.findById(newRoleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        // Actualizar el rol del usuario
        user.setRole(newRole);

        // Guardar los cambios
        userRepository.save(user);
    }

    public List<UserDto> getUsersByRoleId(Long roleId) {
        return userRepository.findAllByRoleId(roleId).stream().map(this::toUserDto).toList();
    }

    public List<PermissionDTO> getAllPermision(){
        return this.permissionRepository.findAll().stream().map(this::convertToPermissionDTO).toList();
    }

    private PermissionDTO convertToPermissionDTO(PermissionEntity permissionEntity) {
       return new PermissionDTO(permissionEntity.getId(),permissionEntity.getName(),permissionEntity.getPriorityLevel());
    }

    private UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAddress(), user.getNit(),
                user.getCreatedAt(), user.getGoogleAuthKey() != null, user.getRole().getName(),
                user.getPaymentPreference().getName(),
                user.getUserPermissions() == null
                        ? List.of()
                        : user.getUserPermissions()
                        .stream()
                        .map(UserPermissionEntity::getPermission)
                        .map(PermissionEntity::getName)
                        .toList());
    }



}
