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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNotesDto;
import uk.nhs.hee.tis.revalidation.core.service.TraineeNotesService;

@Slf4j
@RestController
@RequestMapping("/api/trainee")
public class TraineeNotesController {

  @Autowired
  private TraineeNotesService traineeNotesService;

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

}
