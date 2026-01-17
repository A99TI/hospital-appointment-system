package com.hospital.system.appointments.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class PasswordUpdateRequest {

    @NotEmpty(message = "Old password is mandatory")
    @Size(min = 6, max = 50, message = "Old password must be between at 6 and 50 characters long ")
    private String oldPassword;

    @NotEmpty(message = "New password is mandatory")
    @Size(min = 6, max = 50, message = "New password must be between at 6 and 50 characters long ")
    private String newPassword;

    @NotEmpty(message = "New password confirmation is mandatory")
    @Size(min = 6, max = 50, message = "New password confirmation must be between at 6 and 50 characters long ")
    private String newPasswordConfirmation;

    public PasswordUpdateRequest(String oldPassword, String newPassword, String newPasswordConfirmation) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordConfirmation = newPasswordConfirmation;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirmation() {
        return newPasswordConfirmation;
    }

    public void setNewPasswordConfirmation(String newPasswordConfirmation) {
        this.newPasswordConfirmation = newPasswordConfirmation;
    }
}
