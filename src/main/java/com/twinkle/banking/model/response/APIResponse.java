package com.twinkle.banking.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class APIResponse {

    private int responseCode;
    private Object body;
    private Object error;

    public APIResponse(int responseCode, Object body, Object error) {
        this.responseCode = responseCode;
        this.body = body;
        this.error = error;
    }
}
