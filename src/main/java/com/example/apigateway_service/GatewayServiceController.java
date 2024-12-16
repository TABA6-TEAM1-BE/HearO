package com.example.apigateway_service;

import com.netflix.discovery.EurekaClient;
import com.netflix.appinfo.InstanceInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@RestController
@RequestMapping("/gateway")
@Slf4j
public class GatewayServiceController {

    @Autowired
    private EurekaClient eurekaClient;

    private final RestTemplate restTemplate = new RestTemplate();

    // 특정 인스턴스를 DOWN으로 설정
    @PostMapping("/{serviceName}/{instanceId}/down")
    public String setInstanceDown(@PathVariable String serviceName, @PathVariable String instanceId) {
        Optional<InstanceInfo> instanceInfo = getInstanceInfo(serviceName, instanceId);
        if (instanceInfo.isEmpty()) {
            return "Instance not found: " + instanceId;
        }

        String url = "http://" + instanceInfo.get().getIPAddr() + ":" + instanceInfo.get().getPort() + "/service/down";
        log.info("url:" +url);
        String response = restTemplate.postForObject(url, null, String.class);

        return "Instance " + instanceId + " is set to DOWN. Response: " + response;
    }

    // 특정 인스턴스를 UP으로 설정
    @PostMapping("/{serviceName}/{instanceId}/up")
    public String setInstanceUp(@PathVariable String serviceName, @PathVariable String instanceId) {
        Optional<InstanceInfo> instanceInfo = getInstanceInfo(serviceName, instanceId);
        if (instanceInfo.isEmpty()) {
            return "Instance not found: " + instanceId;
        }

        String url = "http://" + instanceInfo.get().getIPAddr() + ":" + instanceInfo.get().getPort() + "/service/up";
        String response = restTemplate.postForObject(url, null, String.class);

        return "Instance " + instanceId + " is set to UP. Response: " + response;
    }

    // Eureka에서 특정 서비스와 인스턴스를 조회
    private Optional<InstanceInfo> getInstanceInfo(String serviceName, String instanceId) {
        eurekaClient.getApplication(serviceName).getInstances().forEach(i->log.info(i.toString()));

        return eurekaClient.getApplication(serviceName).getInstances().stream()
                .filter(instance -> instance.getInstanceId().equals(instanceId))
                .findFirst();
    }
}

