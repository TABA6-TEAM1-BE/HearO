package com.example.apigateway_service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ResponseBodyCaptureWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream byteArrayOutputStream;
    private ServletOutputStream servletOutputStream;

    public ResponseBodyCaptureWrapper(HttpServletResponse response) {
        super(response);
        this.byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (servletOutputStream == null) {
            servletOutputStream = new ResponseBodyServletOutPutStream(byteArrayOutputStream, super.getOutputStream());
        }
        return servletOutputStream;
    }

    // 가로챈 Response Body 데이터를 String으로 반환
    public String getCapturedResponseBody() {
        return new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
    }
}
