package com.example.usermanagement.controller;

import com.example.usermanagement.dto.CommonAttributeRequestDto;
import com.example.usermanagement.entity.CommonAttribute;
import com.example.usermanagement.service.CommonAttributeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/common/attributes")
@RequiredArgsConstructor
@Slf4j
public class CommonAttributeController {

    private final CommonAttributeService commonAttributeService;

    @PostMapping
    public ResponseEntity<CommonAttribute> add(@Valid @RequestBody CommonAttributeRequestDto dto) {
        log.info("Add common attribute request: {} - {}", dto.getKey(), dto.getLanguage());
        CommonAttribute created = commonAttributeService.add(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonAttribute> update(
            @PathVariable Long id,
            @Valid @RequestBody CommonAttributeRequestDto dto) {
        log.info("Update common attribute request for id: {}", id);
        CommonAttribute updated = commonAttributeService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Delete common attribute request for id: {}", id);
        commonAttributeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonAttribute> getById(@PathVariable Long id) {
        CommonAttribute attribute = commonAttributeService.getById(id);
        return ResponseEntity.ok(attribute);
    }

    @GetMapping
    public ResponseEntity<List<CommonAttribute>> getAll() {
        List<CommonAttribute> attributes = commonAttributeService.getAll();
        return ResponseEntity.ok(attributes);
    }
}
