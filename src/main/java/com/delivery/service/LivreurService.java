package com.delivery.service;

import com.delivery.dto.request.RequestDTOs.*;
import com.delivery.dto.response.ResponseDTOs.*;
import com.delivery.entity.User;
import com.delivery.enums.Role;
import com.delivery.exception.DeliveryException;
import com.delivery.repository.ColisRepository;
import com.delivery.repository.UserRepository;
import com.delivery.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class LivreurService {

    private final UserRepository userRepository;
    private final ColisRepository colisRepository;
    private final FileStorageUtil fileStorageUtil;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllLivreurs() {
        return userRepository.findByRole(Role.ROLE_LIVREUR)
                .stream().map(AuthService::mapToUserResponse).collect(Collectors.toList());
    }

    public List<UserResponse> getLivreursActifs() {
        return userRepository.findAllLivreursActifs()
                .stream().map(AuthService::mapToUserResponse).collect(Collectors.toList());
    }

    public UserResponse getLivreurById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé avec l'id: " + id));
        return AuthService.mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateLivreur(Long id, UpdateProfileRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));

        if (request.getNom() != null) user.setNom(request.getNom());
        if (request.getPrenom() != null) user.setPrenom(request.getPrenom());
        if (request.getTelephone() != null) {
            if (userRepository.existsByTelephone(request.getTelephone())
                    && !user.getTelephone().equals(request.getTelephone())) {
                throw DeliveryException.conflict("Ce numéro est déjà utilisé");
            }
            user.setTelephone(request.getTelephone());
        }
        if (request.getNewPassword() != null && !request.getNewPassword().isBlank()) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw DeliveryException.badRequest("Mot de passe actuel incorrect");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return AuthService.mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse toggleActif(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));
        user.setActif(!user.isActif());
        return AuthService.mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse uploadPhoto(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));

        if (user.getPhotoUrl() != null) {
            fileStorageUtil.deleteFile(user.getPhotoUrl());
        }

        String photoUrl = fileStorageUtil.saveFile(file, "photos");
        user.setPhotoUrl(photoUrl);
        return AuthService.mapToUserResponse(userRepository.save(user));
    }

    @Transactional
    public void updateFcmToken(Long id, String fcmToken) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));
        user.setFcmToken(fcmToken);
        userRepository.save(user);
    }

    @Transactional
    public void deleteLivreur(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> DeliveryException.notFound("Livreur non trouvé"));
        long colisEnCours = colisRepository.countByLivreurIdAndStatus(id,
                com.delivery.enums.ColisStatus.EN_COURS);
        if (colisEnCours > 0) {
            throw DeliveryException.badRequest("Impossible de supprimer un livreur avec des colis en cours");
        }
        userRepository.delete(user);
        log.info("Livreur deleted: {}", id);
    }
    public UserResponse creerLivreur(RegisterLivreurRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DeliveryException("Email déjà utilisé",
                    org.
                            springframework.http.HttpStatus.BAD_REQUEST);
        }
        User user = User.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .telephone(request.getTelephone())
                .role(Role.ROLE_LIVREUR)
                .actif(true)
                .build();
        User saved = userRepository.save(user);
        return AuthService.mapToUserResponse(saved);
    }
}
