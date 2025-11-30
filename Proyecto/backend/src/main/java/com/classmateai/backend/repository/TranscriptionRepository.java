package com.classmateai.backend.repository;

import com.classmateai.backend.entity.Transcription;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TranscriptionRepository extends JpaRepository<Transcription, Long> {
    
<<<<<<< HEAD
    List<Transcription> findByUser_Id(Long userId);

    Optional<Transcription> findByIdAndUser_Id(Long transcriptionId, Long userId);
}
=======
    // Busca todas las transcripciones que tengan un 'user_id' específico
    List<Transcription> findByUser_Id(Long userId);

    // Busca una transcripción por su 'id' Y que además tenga el 'user_id' específico
    Optional<Transcription> findByIdAndUser_Id(Long transcriptionId, Long userId);
}
>>>>>>> b1c20e0c2f38419b1e4d501ef49ed331f4c02454
