package com.example.record_service.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    @Id
    private String id;

    private String recordIdx;

    private String userIdx;

    private String deviceType;

    private LocalDateTime time;     // 음성파일 받고 Ai 모델에게 전달 시 시간

    private LocalDateTime resultTime;   // 모델 결과값 반환 시 시간

    private boolean checked;    // 알림 확인 유무

    public Record(String userIdx, String deviceType, LocalDateTime time) {
        this.userIdx = userIdx;
        this.deviceType = deviceType;
        this.time = time;
        this.checked = false;
    }
}
