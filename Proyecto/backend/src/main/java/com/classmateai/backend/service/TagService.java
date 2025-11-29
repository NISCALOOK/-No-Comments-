package com.classmateai.backend.service;

import com.classmateai.backend.entity.Tag;
import com.classmateai.backend.entity.Transcription;
import com.classmateai.backend.repository.TagRepository;
import com.classmateai.backend.repository.TranscriptionTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TranscriptionTagRepository transcriptionTagRepository;

    @Transactional
    public void saveTagsForTranscription(Transcription transcription, List<String> tagNames) {
        if (tagNames == null || tagNames.isEmpty()) {
            return;
        }

        for (String tagName : tagNames) {
            // Limitar longitud del tag a 100 caracteres
            String limitedTagName = tagName.length() > 100 ? tagName.substring(0, 100) : tagName.toLowerCase();
            
            Tag tag = tagRepository.findByName(limitedTagName).orElse(null);
            if (tag == null) {
                tag = new Tag();
                tag.setName(limitedTagName);
                tag = tagRepository.save(tag);
            }

            // Guardar relación transcripción-tag
            if (!transcriptionTagRepository.existsByTranscriptionIdAndTagId(transcription.getId(), tag.getId())) {
                transcriptionTagRepository.saveTranscriptionTag(transcription.getId(), tag.getId());
            }
        }
    }

    @Transactional(readOnly = true)
    public List<String> getTagsForTranscription(Long transcriptionId) {
        return transcriptionTagRepository.findTagNamesByTranscriptionId(transcriptionId);
    }
}