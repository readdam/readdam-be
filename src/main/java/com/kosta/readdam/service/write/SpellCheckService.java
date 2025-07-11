package com.kosta.readdam.service.write;

import com.kosta.readdam.dto.SpellCheckRequest;
import com.kosta.readdam.dto.SpellCheckResponse;

public interface SpellCheckService {
    SpellCheckResponse checkSpelling(SpellCheckRequest request) throws Exception;
}
