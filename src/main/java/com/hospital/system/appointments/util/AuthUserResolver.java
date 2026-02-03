package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.User;

public interface AuthUserResolver {
    User getAuthenticatedUser();
    User getNonAdminUser(long userId);
}
