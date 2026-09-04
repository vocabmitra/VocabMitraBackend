package com.PixBrew.vocabMitra.service.impl;


import com.PixBrew.vocabMitra.dto.VocabAddRequest;
import com.PixBrew.vocabMitra.dto.VocabCardResponseDto;
import com.PixBrew.vocabMitra.dto.VocabResponse;
import com.PixBrew.vocabMitra.entity.UserVocabProgressAndBookMarks;
import com.PixBrew.vocabMitra.entity.Users;
import com.PixBrew.vocabMitra.entity.Vocabs;
import com.PixBrew.vocabMitra.repository.UserVocabProgressAndBookMarkRepo;
import com.PixBrew.vocabMitra.repository.UsersRepository;
import com.PixBrew.vocabMitra.repository.VocabsRepository;
import com.PixBrew.vocabMitra.service.UsersService;
import com.PixBrew.vocabMitra.service.VocabsService;
import com.PixBrew.vocabMitra.type.VocabType;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VocabsServiceImpl implements VocabsService {

    private final VocabsRepository vocabsRepository;
    private final ModelMapper modelMapper;
    private final UserVocabProgressAndBookMarkRepo progressAndBookMarkRepo;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    @Override
    public VocabResponse addVocablary(VocabAddRequest vocabAddRequest) {
        try {
            //check if vocab already present
            String vocab = vocabAddRequest.getVocab();
            String meaning = vocabAddRequest.getMeaning();
            if(vocabsRepository.existsByVocabAndMeaning(vocab, meaning)){
                throw new IllegalArgumentException("Vocabulary is already present");
            }

            Vocabs latestVocab = Vocabs.builder()
                    .vocab(vocabAddRequest.getVocab())
                    .vocabType(vocabAddRequest.getVocabType())
                    .trick(vocabAddRequest.getTrick())
                    .meaning(vocabAddRequest.getMeaning())
                    .example(vocabAddRequest.getExample())
                    .useCaseTag(vocabAddRequest.getUseCaseTag().toUpperCase())
                    .message(vocabAddRequest.getMessage())
                    .build();
            Vocabs savedVocab = vocabsRepository.save(latestVocab);
            return modelMapper.map(savedVocab, VocabResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<VocabResponse> getAllVocabs(int page, int size, String sortBy) {
        try{
//            1. create pageable request
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
//            2. fetch the vocabs from repository
            Page<Vocabs> vocabsPage = vocabsRepository.findAll(pageable);
//            3. map the page of entities into page of DTO
            return vocabsPage.map(vocabs -> modelMapper.map(vocabs, VocabResponse.class));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public VocabResponse updateVocabDetails(long id, Map<String, Object> updates) {
        try {
//        finding the vocab via id in DB
            Vocabs existingVocab = vocabsRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Vocabulary does not exits with id: " + id));
            updates.forEach((field, value) -> {
                // Adding a null check here is a good safety net in case the frontend sends a key with a null value
                if (value == null) {
                    return;
                }

                switch (field) {
                    case "vocab":
                        existingVocab.setVocab((String) value);
                        break;

                    case "vocabType":
                        // Enums need to be explicitly parsed from the String value
                        existingVocab.setVocabType(VocabType.valueOf(value.toString().toUpperCase()));
                        break;

                    case "useCaseTag":
                        existingVocab.setUseCaseTag((String) value);
                        break;

                    case "trick":
                        existingVocab.setTrick((String) value);
                        break;

                    case "meaning":
                        existingVocab.setMeaning((String) value);
                        break;

                    case "example":
                        existingVocab.setExample((String) value);
                        break;

                    case "message":
                        existingVocab.setMessage((String) value);

                    default:
                        throw new IllegalArgumentException("Invalid Field for updates: " + field);
                }
            });
            existingVocab.setUpdatedAt(LocalDateTime.now());
            Vocabs updatedVocab = vocabsRepository.save(existingVocab);
            return modelMapper.map(updatedVocab, VocabResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Field mismatch in update request" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Page<VocabCardResponseDto> getAllVocabsForuser(Authentication authentication, int page, int size, String sortBy) {

        String username = authentication.getName();
        Users user = usersService.findUserByUsername(username);
        Long id = user.getId();
        //1. Fetch the requested page of vocabularies
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        Page<Vocabs> vocabsPage = vocabsRepository.findAll(pageable);
        //2. get the id's of the vocabularies in the page
        List<Long> vocabIdsOnPage = vocabsPage
                .stream()
                .map(Vocabs::getId)
                .collect(Collectors.toList());
        // 3. Fetch the user's progress ONLY for these 10 words in a single query
        List<UserVocabProgressAndBookMarks> userProgress = progressAndBookMarkRepo
                .findByUserIdAndVocabsIdIn(id, vocabIdsOnPage);
        // 4. Convert the progress list into a Map for lightning-fast lookups (VocabId -> Progress)
        Map<Long, UserVocabProgressAndBookMarks> progressMap = userProgress
                .stream()
                .collect(Collectors
                        .toMap(p -> p.getVocabs().getId(), p->p));
        // 5. Map the final Page of DTOs
        return vocabsPage.map(vocabs -> {
            VocabCardResponseDto vocabCardResponse = new VocabCardResponseDto();
            vocabCardResponse.setVocabResponse(modelMapper.map(vocabs, VocabResponse.class));
            //check if the user has interacted with this specific word or not
            if(progressMap.containsKey(vocabs.getId())) {
                UserVocabProgressAndBookMarks progress = progressMap.get(vocabs.getId());
                vocabCardResponse.setBookmarked(progress.isBookmarked());
                vocabCardResponse.setLearned(progress.isLearned());
            } else {
                // Default state if the user has never clicked anything on this word
                vocabCardResponse.setBookmarked(false);
                vocabCardResponse.setLearned(false);
            }
            return vocabCardResponse;
        });
    }

    @Override
    public Vocabs findById(Long vocabId) {
        Vocabs vocab= vocabsRepository.findById(vocabId)
                .orElseThrow(() -> new IllegalArgumentException("Vocab does not exist by id : " + vocabId));
        return vocab;
    }

    @Override
    public List<VocabResponse> getVocabsByQuery(String qVocab) {
        // 1. Fetching matching entities from the database
        List<Vocabs> matchingVocabs = vocabsRepository.findByVocabContainingIgnoreCase(qVocab);
        // 2. Map the entities to DTOs
        return matchingVocabs.stream()
                .map(vocabs -> modelMapper.map(vocabs, VocabResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabCardResponseDto> getVocabsByUseCaseTag(Authentication auth, String useCaseTag, int page, int size, String sortBy) {

        String username = auth.getName();;
        Users user = usersService.findUserByUsername(username);
        long userId = user.getId();

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        // 1. Fetch the page of vocabs by tag
        Page<Vocabs> vocabsPage = vocabsRepository.findByExactTagInList(useCaseTag, pageable);

        // 🔥 PRO-TIP: Prevent SQL Crashes!
        // If no words exist for this tag, return an empty page immediately.
        if (!vocabsPage.hasContent()) {
            return Page.empty(pageable); // Skips all the progress math!
        }

        // 2. Get the IDs of the vocabularies in the page
        List<Long> vocabIdsOnPage = vocabsPage.stream()
                .map(Vocabs::getId)
                .collect(Collectors.toList());

        // 3. Fetch the user's progress ONLY for these words in a single query
        List<UserVocabProgressAndBookMarks> userProgress = progressAndBookMarkRepo
                .findByUserIdAndVocabsIdIn(userId, vocabIdsOnPage);

        // 4. Convert the progress list into a Map for lightning-fast lookups (VocabId -> Progress)
        Map<Long, UserVocabProgressAndBookMarks> progressMap = userProgress.stream()
                .collect(Collectors.toMap(p -> p.getVocabs().getId(), p -> p));

        // 5. Map the final Page of DTOs and return it
        return vocabsPage.map(vocab -> {
            VocabCardResponseDto vocabCardResponse = new VocabCardResponseDto();

            vocabCardResponse.setVocabResponse(modelMapper.map(vocab, VocabResponse.class));

            // Check if the user has interacted with this specific word
            if (progressMap.containsKey(vocab.getId())) {
                UserVocabProgressAndBookMarks progress = progressMap.get(vocab.getId());
                vocabCardResponse.setBookmarked(progress.isBookmarked());
                vocabCardResponse.setLearned(progress.isLearned());
            } else {
                // Default state if the user has never clicked anything on this word
                vocabCardResponse.setBookmarked(false);
                vocabCardResponse.setLearned(false);
            }
            return vocabCardResponse;
        });
    }
}
