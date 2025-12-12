package com.kosta.readdam.service.klass;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kosta.readdam.dto.ClassListDto;
import com.kosta.readdam.repository.ClassListRepositoryCustom;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClassListServiceImpl implements ClassListService {
    private final ClassListRepositoryCustom classRepository;

    @Override
    public Page<ClassListDto> getClassList(String keyword, String status, String period, Pageable pageable) {
        LocalDate now = LocalDate.now();
        LocalDate from = null;

        // 기간 필터 해석
        if (period != null) {
            switch (period) {
                case "week": from = now.minusWeeks(1); break;
                case "3month": from = now.minusMonths(3); break;
                case "6month": from = now.minusMonths(6); break;
            }
        }

        // repository에서 Page 반환
        Page<ClassListDto> resultPage = classRepository.searchClasses(keyword, status, from, now, pageable);

        // No 컬럼 역순 번호 부여
        long total = resultPage.getTotalElements();
        int startNo = (int) (total - pageable.getOffset());

        for (int i = 0; i < resultPage.getContent().size(); i++) {
            resultPage.getContent().get(i).setNo(startNo - i);
        }

        return resultPage;
    }
}
