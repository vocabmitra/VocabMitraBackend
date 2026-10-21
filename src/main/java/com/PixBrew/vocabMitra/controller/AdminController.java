package com.PixBrew.vocabMitra.controller;

import com.PixBrew.vocabMitra.dto.SubscriptionPlanAdminRequestDto;
import com.PixBrew.vocabMitra.dto.SubscriptionPlanAdminResponseDto;
import com.PixBrew.vocabMitra.dto.VocabAddRequest;
import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.service.SubscriptionPlansService;
import com.PixBrew.vocabMitra.service.VocabsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final VocabsService vocabsService;
    private final SubscriptionPlansService subscriptionPlansService;

    //add new vocabulary
    @PostMapping("/addVocab")
    public ResponseEntity<VocabResponse> addVocab(@Valid @RequestBody VocabAddRequest vocabAddRequest){
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
//    Subscription Related Management

    @GetMapping("/allSubscriptionPlans")
    public ResponseEntity<List<SubscriptionPlanAdminResponseDto>> allSubscriptionPlans() {
        return ResponseEntity.ok(subscriptionPlansService.getAllSubscriptionPlans());
    }

    @PostMapping("/addSubscriptionPlan")
    public ResponseEntity<SubscriptionPlanAdminResponseDto> addSubscriptionPlan(Authentication auth,
                                                                                @Valid @RequestBody SubscriptionPlanAdminRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionPlansService.createSubscriptionPlan(auth, requestDto));
    }
    @PatchMapping("/updateSubscriptionPlan")
    public ResponseEntity<SubscriptionPlanAdminResponseDto> updateSubscriptionPlan(Authentication auth,
                                                                                   @RequestParam long subscriptionPlanId,
                                                                                   @RequestBody Map<String, Object> updates) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subscriptionPlansService.updateExistingSubscriptionPlan(auth, subscriptionPlanId, updates));
    }
    @DeleteMapping("/deleteSubscriptionPlan")
    public ResponseEntity<Void> deleteSubscriptionPlan(@RequestParam long subscriptionPlanId) {
        subscriptionPlansService.deleteSubscriptionPLan(subscriptionPlanId);
        return ResponseEntity.noContent().build();
    }
}
