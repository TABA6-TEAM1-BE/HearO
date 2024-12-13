package com.example.record_service.controller;

import com.example.record_service.entity.Record;
import com.example.record_service.repository.UserServiceClient;
import com.example.record_service.service.AuthService;
import com.example.record_service.service.RecordService;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class RecordController {
    private final RecordService recordService;
    private final AuthService authService;
    private final UserServiceClient userServiceClient;

    @PostMapping("/test/auth")
    public ResponseEntity<String> test(@RequestHeader("X-User-Idx") String id) {
        // 인증 성공 후 처리 로직
        return ResponseEntity.ok("Test successfully");
    }

    /*
    @GetMapping("/list")
    public ResponseEntity<?> getRecords(@RequestHeader("X-User-Idx") String idx) {
        String username = userServiceClient.getMemberById(idx).getBody().getUsername();
        List<Record> records =recordService.getRecordsByUsername(username);
        return ResponseEntity.ok(records);
    }

     */

    // 음성파일 백앤드에서 받아서 recordIdx, time 저장 후 ai 넘겨줌
    @PostMapping("/input")
    public ResponseEntity<?> fileInput(@RequestHeader("X-User-Idx") String idx, @RequestParam("file") MultipartFile file, HttpServletRequest request) {
        log.info("[FileInput] Received X-User-Sub header: {}", idx);

        // 로그: 모든 헤더 출력
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }

        try {
            return recordService.fileInput(idx, file);
        } catch (Exception e) {
            log.error("Error processing file input: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing the file.");
        }
    }

    // 알림 미확인 Record 조회 -> checked 가 false
    @GetMapping("/unchecked")
    public ResponseEntity<?> getUncheckedRecords(@RequestHeader("X-User-Idx") String idx) {
        log.info("[Unchecked] Received X-User-Sub header: {}", idx);

        try {   // Redis 에서 username 조회
            String userIdx = userServiceClient.getMemberById(idx).getBody().getIdx();
            log.info("userIdx from redis: {}", userIdx);

            // 미확인 Record 조회
            log.info("{}", recordService.getUncheckedRecordsByUsername(userIdx));
            return recordService.getUncheckedRecordsByUsername(userIdx);
        } catch (FeignException.Unauthorized e){
            log.info("Unauthorized: no info in redis session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        } catch (Exception e) {
            log.error("Error retrieving unchecked records: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving unchecked records.");
        }
    }

    // 해당 날짜의 deviceType 별로 Record 조회
    @GetMapping("/device-type/{deviceType}/date")
    public ResponseEntity<?> getRecordsByDeviceType(@RequestHeader("X-User-Idx") String idx, @PathVariable String deviceType, @RequestParam("date") String recordDate) {
        log.info("[DeviceType] Received X-User-Sub header: {}", idx);

        try {   // Redis 에서 username 조회
            String userIdx = userServiceClient.getMemberById(idx).getBody().getIdx();
            log.info("UserIdx: {}", userIdx);
            // 문자열을 LocalDate 로 변환
            LocalDate date = LocalDate.parse(recordDate);

            // Record 조회
            return recordService.getRecordsByDeviceTypeAndDate(userIdx, deviceType, date);
        } catch (FeignException.Unauthorized e){
            log.info("Unauthorized: no info in redis session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        } catch (Exception e) {
            log.error("Error retrieving records by device type and date: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while retrieving records.");
        }
    }

    // 클라이언트가 알림 확인 시 checked -> true 로 업데이트
    @PostMapping("/{recordIdx}/checked")
    public ResponseEntity<?> updateCheckedStatus(@RequestHeader("X-User-Idx") String idx, @PathVariable("recordIdx") String recordIdx) {
        log.info("[Update Checked] Received X-User-Sub header: {}", idx);

        try {   // Redis 에서 userIdx 조회.
            String userIdx = userServiceClient.getMemberById(idx).getBody().getIdx();
            Optional<Record> foundRecord = recordService.getRecordByRecordIdx(recordIdx);

            if(foundRecord.isPresent()) {
                if(foundRecord.get().getUserIdx().equals(userIdx)) {
                    // Record 상태 업데이트
                    return recordService.updateCheckedStatus(recordIdx);
                }else{
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("you are not authorized to update this record.");
                }
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Record not found.");
            }
        }catch (FeignException.Unauthorized e){
            log.info("unauthorized: no info in redis session");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("unauthorized");
        }
        catch (Exception e) {
            log.error("Error updating checked status for recordIdx {}: {}", recordIdx, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the record status.");
        }
    }


    /*
    private ResponseEntity<?> validateUserAndHandleErrors(String idx) {
        try {
            validateUser(idx);
            return null; // 검증 성공 시 null 반환
        } catch (RuntimeException e) {
            log.error("Validation error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 401 에러 (Invalid User)
        }
    }

    private void validateUser(String idx) {
        if (!authService.validateUser(idx)) {
            throw new RuntimeException("Invalid user: authentication failed for idx " + idx);
        }
    }


     */

}

