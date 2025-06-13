package com.kosta.readdam.service.my;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.AlertDto;
import com.kosta.readdam.entity.Alert;
import com.kosta.readdam.repository.AlertRepository;

@Service
public class MyAlertServiceImpl implements MyAlertService {

    @Autowired
    private AlertRepository alertRepository;

    @Override
    public List<AlertDto> getMyAlerts(String receiverUsername) throws Exception {
        List<Alert> alerts = alertRepository.findByReceiverUsernameOrderByAlertIdDesc(receiverUsername);
        return alerts.stream()
                .map(Alert::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void checkAlert(Integer alertId) throws Exception {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new Exception("해당 알림이 존재하지 않습니다."));
        alert.setChecked(true);
        alertRepository.save(alert);
    }


}
