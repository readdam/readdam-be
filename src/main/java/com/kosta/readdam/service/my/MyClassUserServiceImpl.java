// src/main/java/com/kosta/readdam/service/my/MyClassUserServiceImpl.java
package com.kosta.readdam.service.my;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.ClassDto;
import com.kosta.readdam.dto.ClassUserDto;
import com.kosta.readdam.dto.PagedResponse;
import com.kosta.readdam.entity.ClassEntity;
import com.kosta.readdam.entity.ClassUser;
import com.kosta.readdam.repository.klass.ClassRepository;
import com.kosta.readdam.repository.klass.ClassUserRepository;
import com.kosta.readdam.util.PageInfo2;

@Service
public class MyClassUserServiceImpl implements MyClassUserService {

	@Autowired
	private ClassUserRepository classUserRepo;

	@Autowired
	private ClassRepository classRepo;

	@Override
	public PagedResponse<ClassUserDto> getOngoingClasses(String username, int page, int size) {
		Page<ClassUser> p = classUserRepo
				.findByUser_UsernameAndClassEntity_EndDateGreaterThanEqualOrderByClassEntity_EndDateDesc(username,
						LocalDate.now(), PageRequest.of(page, size));

		List<ClassUserDto> dtos = p.getContent().stream().map(cu -> {
			ClassDto classDto = cu.getClassEntity().toDto();
			int cnt = (int) classUserRepo.countByClassEntity_ClassId(classDto.getClassId());
			classDto.setCurrentParticipants(cnt);
			// 직접 생성자 호출
			return new ClassUserDto(classDto, cu.getJoinDate(), cu.getLeftDate());
		}).collect(Collectors.toList());

		return new PagedResponse<>(dtos, PageInfo2.from(p));
	}

	@Override
	public PagedResponse<ClassUserDto> getPastClasses(String username, int page, int size) {
		Page<ClassUser> p = classUserRepo
				.findByUser_UsernameAndClassEntity_EndDateLessThanOrderByClassEntity_EndDateDesc(username,
						LocalDate.now(), PageRequest.of(page, size));

		List<ClassUserDto> dtos = p.getContent().stream().map(cu -> {
			ClassDto classDto = cu.getClassEntity().toDto();
			int cnt = (int) classUserRepo.countByClassEntity_ClassId(classDto.getClassId());
			classDto.setCurrentParticipants(cnt);
			return new ClassUserDto(classDto, cu.getJoinDate(), cu.getLeftDate());
		}).collect(Collectors.toList());

		return new PagedResponse<>(dtos, PageInfo2.from(p));
	}

	@Override
	public PagedResponse<ClassDto> getCreatedClasses(String username, int page, int size) {
		Page<ClassEntity> p = classRepo.findByLeader_UsernameOrderByCreatedAtDesc(username, PageRequest.of(page, size));
		List<ClassDto> dtos = p.getContent().stream().map(ClassEntity::toDto).peek(
				dto -> dto.setCurrentParticipants((int) classUserRepo.countByClassEntity_ClassId(dto.getClassId())))
				.collect(Collectors.toList());
		return new PagedResponse<>(dtos, PageInfo2.from(p));
	}
}
