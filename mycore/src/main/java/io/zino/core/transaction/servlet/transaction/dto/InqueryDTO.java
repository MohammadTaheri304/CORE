package io.zino.core.transaction.servlet.transaction.dto;

public class InqueryDTO {
    private String extuid;
    private boolean verify;

    public String getExtuid() {
        return extuid;
    }

    public void setExtuid(String extuid) {
        this.extuid = extuid;
    }

    public boolean isVerify() {
        return verify;
    }

    public void setVerify(boolean verify) {
        this.verify = verify;
    }
}
