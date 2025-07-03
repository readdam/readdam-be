package com.kosta.readdam.controller.my;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kosta.readdam.config.auth.PrincipalDetails;
import com.kosta.readdam.dto.LibraryBookDto;
import com.kosta.readdam.dto.LibraryDto;
import com.kosta.readdam.service.my.MyLibraryService;

@RestController
@RequestMapping("/my")
public class MyLibraryController {
	
	@Autowired
	private MyLibraryService myLibraryService;
	
	@GetMapping("/myLibrary")
	public ResponseEntity<List<LibraryDto>> getMyLibraryList(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		
		try {
        String username = principalDetails.getUsername();
        List<LibraryDto> libraryList = myLibraryService.getMyLibraryList(username);
        return ResponseEntity.ok(libraryList);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
    }
	
	@PostMapping("/myLibraryAdd")
	public ResponseEntity<?> addLibrary(@AuthenticationPrincipal PrincipalDetails principalDetails, 
	                                    @RequestBody LibraryDto libraryDto) {
	    try {
	        myLibraryService.addLibrary(principalDetails.getUsername(), libraryDto);
	        return ResponseEntity.ok().build();
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	    }
	}


	@PostMapping("/myLibraryUpdate")
	public ResponseEntity<?> updateLibrary(@AuthenticationPrincipal PrincipalDetails principalDetails,
	                                       @RequestBody LibraryDto dto) {
	    try {
	        dto.setUsername(principalDetails.getUsername());
	        LibraryDto updated = myLibraryService.updateLibrary(dto);
	        return ResponseEntity.ok(updated);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>("서재 수정 실패", HttpStatus.BAD_REQUEST);
	    }
	}

	@PostMapping("/myLibraryDelete")
	public ResponseEntity<?> deleteLibrary(@RequestParam Integer libraryId) {
	    try {
	        myLibraryService.deleteLibrary(libraryId);
	        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>("서재 삭제 실패", HttpStatus.BAD_REQUEST);
	    }
	}
	
	@PostMapping("/myLibraryShow")
	public ResponseEntity<List<LibraryDto>> toggleShowAll(
	    @AuthenticationPrincipal PrincipalDetails principal,
	    @RequestBody Map<String, Integer> body
	) throws Exception {
	    String username = principal.getUsername();
	    Integer isShow = body.get("isShow");
	    if (isShow == null || (isShow != 0 && isShow != 1)) {
	        return ResponseEntity.badRequest().build();
	    }

	    List<LibraryDto> updated = myLibraryService.toggleShowAll(username, isShow);
	    return ResponseEntity.ok(updated);
	}
	
	@GetMapping("/myLibraryList")
	public ResponseEntity<List<LibraryDto>> getMyLibraries(Principal principal) {
	    List<LibraryDto> libs = myLibraryService.getMyLibraries(principal.getName());
	    return ResponseEntity.ok(libs);
	}

    /** 특정 서재에 책 추가 */
    @PostMapping("/{libraryId}/books")
    public ResponseEntity<Void> addBook(
            Principal principal,
            @PathVariable Integer libraryId,
            @RequestBody LibraryBookDto bookDto
    ) {
    	myLibraryService.addBookToLibrary(principal.getName(), libraryId, bookDto);
        return ResponseEntity.ok().build();
    }
}
