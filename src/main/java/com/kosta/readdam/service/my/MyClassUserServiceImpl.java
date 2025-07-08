// src/main/java/com/kosta/readdam/service/my/MyClassUserServiceImpl.java
package com.kosta.readdam.service.my;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassUserDto;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassUser;
import com.kosta.readdam.repository.ClassRepository;
import com.kosta.readdam.repository.ClassUserRepository;

@Service
public class MyClassUserServiceImpl implements MyClassUserService {

    @Autowired
    private ClassUserRepository classUserRepo;

    @Autowired
    private ClassRepository classRepo;

    @Override
    public List<ClassUserDto> getOngoingClasses(String username) {
        LocalDate today = LocalDate.now();
        return classUserRepo.findByUser_Username(username).stream()
            .filter(cu -> {
                LocalDate end = cu.getClassEntity().getEndDate();
                return end != null && !end.isBefore(today);
            })
            .map(cu -> {
                ClassDto dto = cu.getClassEntity().toDto();
                int cnt = (int) classUserRepo.countByClassEntity_ClassId(dto.getClassId());
                dto.setCurrentParticipants(cnt);
                return new ClassUserDto(dto, cu.getJoinDate(), cu.getLeftDate());
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ClassUserDto> getPastClasses(String username) {
        LocalDate today = LocalDate.now();
        return classUserRepo.findByUser_Username(username).stream()
            .filter(cu -> {
                LocalDate end = cu.getClassEntity().getEndDate();
                return end != null && end.isBefore(today);
            })
            .map(cu -> {
                ClassDto dto = cu.getClassEntity().toDto();
                int cnt = (int) classUserRepo.countByClassEntity_ClassId(dto.getClassId());
                dto.setCurrentParticipants(cnt);
                return new ClassUserDto(dto, cu.getJoinDate(), cu.getLeftDate());
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ClassDto> getCreatedClasses(String username) {
        return classRepo.findByLeader_Username(username).stream()
            .map(entity -> {
                ClassDto dto = entity.toDto();
                int cnt = (int) classUserRepo.countByClassEntity_ClassId(dto.getClassId());
                dto.setCurrentParticipants(cnt);
                return dto;
            })
            .collect(Collectors.toList());
    }
}
