package com.example.record_service.repository;

import com.example.record_service.entity.Record;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecordRepository extends MongoRepository<Record, String> {

    // ai 모델 결과 받고 저장 시에 recordIdx 로 기록 찾음
    public Optional<Record> findByRecordIdx(String recordIdx);

    // checked 가 false 인 Record 반환 -> 클라이언트가 아직 확인하지 못한 알림
    public List<Record> findAllByUserIdxAndCheckedIsFalse(String userIdx);

    // 해당 날짜의 deviceType 별로 Record 조회
    public List<Record> findAllByUserIdxAndDeviceTypeAndTimeBetween(String userIdx, String deviceType, LocalDateTime startDate, LocalDateTime endDate);
}