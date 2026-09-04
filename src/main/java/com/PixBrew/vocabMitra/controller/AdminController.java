package com.PixBrew.vocabMitra.controller;

import com.PixBrew.vocabMitra.dto.VocabAddRequest;
import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.service.VocabsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VocabsService vocabsService;

    //add new vocabulary
    @PostMapping("/addVocab")
    public ResponseEntity<VocabResponse> addVocab(@RequestBody VocabAddRequest vocabAddRequest){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(vocabsService.addVocablary(vocabAddRequest));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //    patch update method
    @PatchMapping("/update")
    public ResponseEntity<VocabResponse> updateVocab(@RequestParam long id, @RequestBody Map< String, Object> updates){
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(vocabsService.updateVocabDetails(id, updates));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/allVocabs")
    public ResponseEntity<Page<VocabResponse>> getAllVocabsForAdmin(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy) {
        try{
            return ResponseEntity.ok(vocabsService.getAllVocabs(page, size, sortBy));
        } catch (Exception e) {
            throw new RuntimeException("exception in getAllVocabsForAdmin()" + e.getMessage());
        }
    }
}
