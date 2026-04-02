package com.user.stub;

import com.user.dto.response.BaseResponse;

public class BaseResponseStub {

    private BaseResponseStub() {
    }

    public static <T> BaseResponse<T> buildResponse(T data, String message) {
        return BaseResponse.<T>builder()
                .data(data)
                .message(message)
                .build();
    }
}