package com.ayds.Cloudmerce.model.dto.callaborator;

import java.util.List;

public record UserUpdateDTO (
        Long idUser,
        RoleDTO role,
        List<Long> permissions
) {
}
