package com.shopnolive.shopnolive.model.token;

public class Token {
    private boolean success;
    private String message;
    private String bearer_token;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBearer_token() {
        return bearer_token;
    }

    public void setBearer_token(String bearer_token) {
        this.bearer_token = bearer_token;
    }
}
