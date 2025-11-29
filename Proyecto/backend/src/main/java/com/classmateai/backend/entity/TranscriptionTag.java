package com.classmateai.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Table(name = "transcription_tag")
public class TranscriptionTag implements Serializable {

    @EmbeddedId
    private TranscriptionTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("transcriptionId")
    @JoinColumn(name = "transcription_id")
    private Transcription transcription;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static class TranscriptionTagId implements Serializable {
        private Long transcriptionId;
        private Long tagId;

        public TranscriptionTagId() {}

        public TranscriptionTagId(Long transcriptionId, Long tagId) {
            this.transcriptionId = transcriptionId;
            this.tagId = tagId;
        }
        public Long getTranscriptionId() { return transcriptionId; }
        public void setTranscriptionId(Long transcriptionId) { this.transcriptionId = transcriptionId; }
        public Long getTagId() { return tagId; }
        public void setTagId(Long tagId) { this.tagId = tagId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TranscriptionTagId that = (TranscriptionTagId) o;
            return java.util.Objects.equals(transcriptionId, that.transcriptionId) &&
                   java.util.Objects.equals(tagId, that.tagId);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(transcriptionId, tagId);
        }
    }
}
