package com.hospital.system.appointments.util;

import com.hospital.system.appointments.entity.User;

public interface FindNonAdminUser {
    User getNonAdminUser(long userId);
}
