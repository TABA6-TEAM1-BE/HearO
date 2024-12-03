package com.example.apigateway_service;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;

import java.io.IOException;
import java.io.OutputStream;

public class ResponseBodyServletOutPutStream extends ServletOutputStream {
    private final OutputStream outputStream;
    private final ServletOutputStream originalOutputStream;

    public ResponseBodyServletOutPutStream(OutputStream outputStream, ServletOutputStream originalOutputStream) {
        this.outputStream = outputStream;
        this.originalOutputStream = originalOutputStream;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b); // 데이터를 ByteArrayOutputStream에 기록
        originalOutputStream.write(b); // 원래 응답 스트림에도 데이터 전달
    }

    @Override
    public boolean isReady() {
        return originalOutputStream.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        originalOutputStream.setWriteListener(writeListener);
    }

}
