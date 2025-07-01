package com.kosta.readdam.service.klass;

import java.util.Map;

public interface ClassLikeService {
	Map<String, Object> getLikeStatus(Integer classId, String username) throws Exception;
	Map<String, Object> toggleLike(Integer classId, String username) throws Exception;

}
