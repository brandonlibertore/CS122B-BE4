package com.github.klefstad_teaching.cs122b.gateway;

public class MyResultClass {

    private Integer code;
    private String message;

    public Integer getCode() {
        return code;
    }

    public MyResultClass setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public MyResultClass setMessage(String message) {
        this.message = message;
        return this;
    }
}
