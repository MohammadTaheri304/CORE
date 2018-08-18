package io.zino.core.transaction.servlet.account.dto;

public class ChangeAccountStatusDTO {
    private boolean active;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
