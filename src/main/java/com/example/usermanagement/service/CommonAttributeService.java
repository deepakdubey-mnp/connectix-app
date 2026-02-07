package com.example.usermanagement.service;

import com.example.usermanagement.dto.CommonAttributeRequestDto;
import com.example.usermanagement.entity.CommonAttribute;
import com.example.usermanagement.exception.CommonAttributeAlreadyExistsException;
import com.example.usermanagement.exception.CommonAttributeNotFoundException;
import com.example.usermanagement.repository.CommonAttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonAttributeService {

    private final CommonAttributeRepository commonAttributeRepository;

    @Transactional
    public CommonAttribute add(CommonAttributeRequestDto dto) {
        log.info("Adding common attribute: {} - {}", dto.getKey(), dto.getLanguage());

        if (commonAttributeRepository.findByKeyAndLanguage(dto.getKey(), dto.getLanguage()).isPresent()) {
            throw new CommonAttributeAlreadyExistsException(
                    "Common attribute with key '" + dto.getKey() + "' and language '" + dto.getLanguage() + "' already exists");
        }

        Long newId = commonAttributeRepository.findTopByOrderByIdDesc()
                .map(c -> c.getId() + 1)
                .orElse(1L);

        CommonAttribute attribute = CommonAttribute.builder()
                .id(newId)
                .key(dto.getKey())
                .language(dto.getLanguage())
                .value(dto.getValue())
                .build();
        CommonAttribute saved = commonAttributeRepository.save(attribute);
        log.info("Common attribute added with id: {}", saved.getId());
        return saved;
    }

    @Transactional
    public CommonAttribute update(Long id, CommonAttributeRequestDto dto) {
        log.info("Updating common attribute with id: {}", id);

        CommonAttribute existing = commonAttributeRepository.findById(id)
                .orElseThrow(() -> new CommonAttributeNotFoundException("Common attribute not found with id: " + id));

        commonAttributeRepository.findByKeyAndLanguage(dto.getKey(), dto.getLanguage())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new CommonAttributeAlreadyExistsException(
                            "Common attribute with key '" + dto.getKey() + "' and language '" + dto.getLanguage() + "' already exists");
                });

        existing.setKey(dto.getKey());
        existing.setLanguage(dto.getLanguage());
        existing.setValue(dto.getValue());

        CommonAttribute updated = commonAttributeRepository.save(existing);
        log.info("Common attribute updated with id: {}", updated.getId());
        return updated;
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting common attribute with id: {}", id);

        if (!commonAttributeRepository.existsById(id)) {
            throw new CommonAttributeNotFoundException("Common attribute not found with id: " + id);
        }
        commonAttributeRepository.deleteById(id);
        log.info("Common attribute deleted with id: {}", id);
    }

    public CommonAttribute getById(Long id) {
        return commonAttributeRepository.findById(id)
                .orElseThrow(() -> new CommonAttributeNotFoundException("Common attribute not found with id: " + id));
    }

    public List<CommonAttribute> getAll() {
        return commonAttributeRepository.findAll();
    }
}
