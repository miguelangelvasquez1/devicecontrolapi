package com.devicecontrolapi.util.consultasPrompt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class NaturalLanguageSearchController {
    
    @Autowired
    private NaturalLanguageSearchService searchService;
    
    @PostMapping("/natural-language")
    public ResponseEntity<Map<String, Object>> search(@RequestBody SearchRequest request) {
        try {
            Map<String, Object> result = searchService.searchWithNaturalLanguage(request.getPrompt());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of(
                    "success", false,
                    "error", e.getMessage()
                )
            );
        }
    }
    
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Natural Language Search API is working!");
    }
}

class SearchRequest {
    private String prompt;
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}