/*
 * The MIT License (MIT)
 *
 * Copyright 2021 Crown Copyright (Health Education England)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package uk.nhs.hee.tis.revalidation.core.service;

import static java.time.LocalDateTime.now;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNoteDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNotesDto;
import uk.nhs.hee.tis.revalidation.core.entity.TraineeNote;
import uk.nhs.hee.tis.revalidation.core.repository.TraineeNotesRepository;

@Slf4j
@Transactional
@Service
public class TraineeNotesService {

  @Autowired
  private TraineeNotesRepository traineeNotesRepository;

  /**
   * Get trainee notes details.
   *
   * @param gmcId to retrieve notes
   */
  public TraineeNotesDto getTraineeNotes(final String gmcId) {
    log.info("Retrieving trainee notes for gmcId: {}", gmcId);
    final var notes = traineeNotesRepository.findAllByGmcIdOrderByCreatedDateDesc(gmcId);
    return TraineeNotesDto.builder()
        .gmcId(gmcId)
        .notes(notes)
        .build();
  }

  /**
   * Get trainee notes details by note id.
   *
   * @param id to retrieve notes
   */
  public Optional<TraineeNote> getTraineeNotesByNoteId(final String id) {
    log.info("Retrieving trainee notes for note Id: {}", id);
    return traineeNotesRepository.findById(id);
  }

  /**
   * Save trainee note.
   *
   * @param traineeNoteDto to persist note
   */
  public TraineeNote saveTraineeNote(final TraineeNoteDto traineeNoteDto) {
    log.info("In service, received request to save trainee note: {}", traineeNoteDto);
    final var traineeNote = TraineeNote.builder()
        .gmcId(traineeNoteDto.getGmcId())
        .text(traineeNoteDto.getText())
        .createdDate(now())
        .updatedDate(now())
        .build();

    return traineeNotesRepository.save(traineeNote);
  }

  /**
   * Edit trainee note.
   *
   * @param traineeNoteDto to edit note
   */
  public TraineeNote editTraineeNote(final TraineeNoteDto traineeNoteDto) {
    log.info("In service, received request to edit trainee note: {}", traineeNoteDto);

    Optional<TraineeNote> optionalNote = getTraineeNotesByNoteId(traineeNoteDto.getId());
    if (optionalNote.isPresent()) {
      TraineeNote existingNote = optionalNote.get();

      final var traineeNote = TraineeNote.builder()
          .id(existingNote.getId())
          .gmcId(existingNote.getGmcId())
          .text(traineeNoteDto.getText())
          .createdDate(existingNote.getCreatedDate())
          .updatedDate(now())
          .build();

      return traineeNotesRepository.save(traineeNote);
    }
    else {
      return saveTraineeNote(traineeNoteDto);
    }
  }
}
