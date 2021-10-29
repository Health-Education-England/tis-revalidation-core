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

package uk.nhs.hee.tis.revalidation.core.controller;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNoteDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNotesDto;
import uk.nhs.hee.tis.revalidation.core.mapper.TraineeNoteMapper;
import uk.nhs.hee.tis.revalidation.core.service.TraineeNotesService;

@Slf4j
@RestController
@RequestMapping("/api/trainee")
public class TraineeNotesController {

  private TraineeNotesService traineeNotesService;

  private TraineeNoteMapper traineeNoteMapper;

  public TraineeNotesController(
      TraineeNotesService traineeNotesService,
      TraineeNoteMapper traineeNoteMapper
  ) {
    this.traineeNotesService = traineeNotesService;
    this.traineeNoteMapper = traineeNoteMapper;
  }

  /**
   * GET  /api/trainee/{gmcId}/notes : get trainee notes.
   *
   * @param gmcId trainee's gmc id to get notes
   * @return the ResponseEntity with status 200 (OK) and trainee's all notes in body
   */
  @GetMapping("/{gmcId}/notes")
  public ResponseEntity<TraineeNotesDto> getTraineeNotes(
      @PathVariable("gmcId") final String gmcId) {
    log.info("Received request to get notes for gmcId: {}", gmcId);
    if (Objects.nonNull(gmcId)) {
      final var traineeNoteDto = traineeNotesService.getTraineeNotes(gmcId);
      return ResponseEntity.ok().body(traineeNoteDto);
    }
    return ResponseEntity.ok().body(null);
  }

  /**
   * POST  /api/trainee/notes/add : add trainee note.
   *
   * @param traineeNoteDto trainee Note Dto to add note
   * @return the ResponseEntity with status 200 (OK)
   */
  @PostMapping("/notes/add")
  public ResponseEntity<TraineeNoteDto> createNote(
      @RequestBody final TraineeNoteDto traineeNoteDto
  ) {
    log.info("In controller, received request to create note: {}", traineeNoteDto);
    final var traineeNote = traineeNotesService.saveTraineeNote(traineeNoteDto);
    return ResponseEntity.ok().body(traineeNoteMapper.toDto(traineeNote));
  }

  /**
   * PUT  /api/trainee/notes/edit : edit trainee note.
   *
   * @param traineeNoteDto trainee Note Dto to edit note
   * @return the ResponseEntity with status 200 (OK)
   */
  @PutMapping("/notes/edit")
  public ResponseEntity<TraineeNoteDto> editNote(@RequestBody final TraineeNoteDto traineeNoteDto) {
    log.info("In controller, received request to edit note: {}", traineeNoteDto);
    if ((traineeNoteDto.getId()).isEmpty()) {
      return ResponseEntity.badRequest().header("error", "NoteId can not be null")
          .body(null);
    }
    final var traineeNote = traineeNotesService.editTraineeNote(traineeNoteDto);
    return ResponseEntity.ok().body(traineeNoteMapper.toDto(traineeNote));
  }
}
