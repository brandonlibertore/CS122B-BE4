package com.github.klefstad_teaching.cs122b.gateway;

public class AuthResponseModel {

    private MyResultClass result;

    public MyResultClass getResult() {
        return result;
    }

    public AuthResponseModel setResult(MyResultClass result) {
        this.result = result;
        return this;
    }
}
