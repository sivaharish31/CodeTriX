package com.codetrix.ctf.service;

import com.codetrix.ctf.dto.*;
import com.codetrix.ctf.entity.CtfChallenge;
import com.codetrix.ctf.exception.CtfException;
import com.codetrix.ctf.repository.CtfChallengeRepository;
import com.codetrix.ctf.repository.CtfSubmissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CtfChallengeService {

    private final CtfChallengeRepository challengeRepository;
    private final CtfSubmissionRepository submissionRepository;
    private final CtfAttachmentService attachmentService;

    public List<CtfChallengeResponse> getActiveChallenges(Long teamId) {
        List<CtfChallenge> challenges = challengeRepository.findByActiveTrueOrderByPointsAsc();
        Set<Long> solvedIds = submissionRepository.findSolvedChallengeIdsByTeamId(teamId)
            .stream().collect(Collectors.toSet());

        return challenges.stream()
            .map(c -> CtfChallengeResponse.from(c, solvedIds.contains(c.getId())))
            .collect(Collectors.toList());
    }

    public CtfChallengeResponse getChallenge(Long id, Long teamId) {
        CtfChallenge challenge = challengeRepository.findById(id)
            .orElseThrow(() -> CtfException.challengeNotFound(id));

        if (!challenge.getActive()) {
            throw CtfException.challengeNotActive(id);
        }

        boolean solved = submissionRepository.existsByTeamIdAndChallengeIdAndCorrectTrue(teamId, id);
        return CtfChallengeResponse.from(challenge, solved);
    }

    @Transactional(readOnly = true)
    public List<CtfChallengeAdminResponse> getAllChallengesAdmin() {
        return challengeRepository.findAllByOrderByPointsAsc().stream()
            .map(CtfChallengeAdminResponse::from)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CtfChallengeAdminResponse getChallengeAdmin(Long id) {
        CtfChallenge challenge = challengeRepository.findById(id)
            .orElseThrow(() -> CtfException.challengeNotFound(id));
        return CtfChallengeAdminResponse.from(challenge);
    }

    @Transactional
    public CtfChallengeAdminResponse createChallenge(CreateCtfChallengeRequest request, MultipartFile attachment) {
        if (challengeRepository.existsByTitle(request.getTitle())) {
            throw CtfException.duplicateTitle(request.getTitle());
        }

        CtfChallenge challenge = CtfChallenge.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .category(request.getCategory())
            .difficulty(request.getDifficulty())
            .points(request.getPoints())
            .flag(normalizeFlag(request.getFlag()))
            .active(request.getActive() != null ? request.getActive() : true)
            .build();

        if (attachment != null && !attachment.isEmpty()) {
            CtfAttachmentService.AttachmentInfo info = attachmentService.saveAttachment(attachment);
            challenge.setAttachmentFilename(info.originalFilename());
            challenge.setAttachmentPath(info.storedPath());
            challenge.setAttachmentContentType(info.contentType());
        }

        challenge = challengeRepository.save(challenge);
        log.info("Created CTF challenge: {} ({})", challenge.getTitle(), challenge.getCategory());

        return CtfChallengeAdminResponse.from(challenge);
    }

    @Transactional
    public CtfChallengeAdminResponse updateChallenge(Long id, UpdateCtfChallengeRequest request, MultipartFile attachment) {
        CtfChallenge challenge = challengeRepository.findById(id)
            .orElseThrow(() -> CtfException.challengeNotFound(id));

        if (request.getTitle() != null && !request.getTitle().equals(challenge.getTitle())) {
            if (challengeRepository.existsByTitleAndIdNot(request.getTitle(), id)) {
                throw CtfException.duplicateTitle(request.getTitle());
            }
            challenge.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            challenge.setDescription(request.getDescription());
        }
        if (request.getCategory() != null) {
            challenge.setCategory(request.getCategory());
        }
        if (request.getDifficulty() != null) {
            challenge.setDifficulty(request.getDifficulty());
        }
        if (request.getPoints() != null) {
            challenge.setPoints(request.getPoints());
        }
        if (request.getFlag() != null) {
            challenge.setFlag(normalizeFlag(request.getFlag()));
        }
        if (request.getActive() != null) {
            challenge.setActive(request.getActive());
        }

        if (attachment != null && !attachment.isEmpty()) {
            if (challenge.getAttachmentPath() != null) {
                attachmentService.deleteAttachment(challenge.getAttachmentPath());
            }
            CtfAttachmentService.AttachmentInfo info = attachmentService.saveAttachment(attachment);
            challenge.setAttachmentFilename(info.originalFilename());
            challenge.setAttachmentPath(info.storedPath());
            challenge.setAttachmentContentType(info.contentType());
        }

        challenge = challengeRepository.save(challenge);
        log.info("Updated CTF challenge: {} (id={})", challenge.getTitle(), id);

        return CtfChallengeAdminResponse.from(challenge);
    }

    @Transactional
    public void deleteChallenge(Long id) {
        CtfChallenge challenge = challengeRepository.findById(id)
            .orElseThrow(() -> CtfException.challengeNotFound(id));

        if (challenge.getAttachmentPath() != null) {
            attachmentService.deleteAttachment(challenge.getAttachmentPath());
        }

        challengeRepository.delete(challenge);
        log.info("Deleted CTF challenge: {} (id={})", challenge.getTitle(), id);
    }

    @Transactional
    public void removeAttachment(Long id) {
        CtfChallenge challenge = challengeRepository.findById(id)
            .orElseThrow(() -> CtfException.challengeNotFound(id));

        if (challenge.getAttachmentPath() != null) {
            attachmentService.deleteAttachment(challenge.getAttachmentPath());
            challenge.setAttachmentFilename(null);
            challenge.setAttachmentPath(null);
            challenge.setAttachmentContentType(null);
            challengeRepository.save(challenge);
        }
    }

    CtfChallenge getChallengeEntity(Long id) {
        return challengeRepository.findById(id)
            .orElseThrow(() -> CtfException.challengeNotFound(id));
    }

    private String normalizeFlag(String flag) {
        return flag.trim();
    }
}
