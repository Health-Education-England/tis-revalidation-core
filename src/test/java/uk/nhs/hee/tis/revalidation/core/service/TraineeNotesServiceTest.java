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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNoteDto;
import uk.nhs.hee.tis.revalidation.core.entity.TraineeNote;
import uk.nhs.hee.tis.revalidation.core.repository.TraineeNotesRepository;

@ExtendWith(MockitoExtension.class)
class TraineeNotesServiceTest {

  private final Faker faker = new Faker();
  @InjectMocks
  private TraineeNotesService traineeNotesService;
  @Mock
  private TraineeNotesRepository traineeNotesRepository;
  private String id;
  private String gmcId;
  private String text;
  private LocalDateTime createdDate;
  private LocalDateTime updatedDate;

  @BeforeEach
  public void setup() {
    setupMockData();
  }

  @Test
  void shouldReturnAllNotesForATrainee() throws Exception {
    when(traineeNotesRepository.findAllByGmcIdOrderByUpdatedDateDesc(gmcId))
        .thenReturn(List.of(prepareTraineeNote()));
    var notes = traineeNotesService.getTraineeNotes(gmcId);
    assertThat(notes.getNotes().size(), is(1));
    final var notesDto = notes.getNotes().get(0);
    assertThat(notesDto.getId(), is(id));
    assertThat(notesDto.getGmcId(), is(gmcId));
    assertThat(notesDto.getText(), is(text));
    assertThat(notesDto.getCreatedDate(), is(createdDate));
    assertThat(notesDto.getUpdatedDate(), is(updatedDate));
  }

  @Test
  void shouldSaveTraineeNote() {
    when(traineeNotesRepository.save(any(TraineeNote.class))).thenReturn(prepareTraineeNote());

    final var traineeNoteDto = TraineeNoteDto.builder()
        .gmcId(gmcId)
        .text(text)
        .createdDate(createdDate)
        .build();
    final var returnedTraineeNote = traineeNotesService.saveTraineeNote(traineeNoteDto);

    verify(traineeNotesRepository, times(1)).save(any(TraineeNote.class));

    assertThat(returnedTraineeNote.getGmcId(), is(gmcId));
    assertThat(returnedTraineeNote.getText(), is(text));
    assertThat(returnedTraineeNote.getCreatedDate(), is(createdDate));
    assertThat(returnedTraineeNote.getUpdatedDate(), is(updatedDate));
  }

  private void setupMockData() {
    id = faker.number().digits(7);
    gmcId = faker.number().digits(8);
    text = faker.lorem().characters(255);
    createdDate = now().minusDays(10);
    updatedDate = now();
  }

  private TraineeNote prepareTraineeNote() {
    return TraineeNote.builder()
        .id(id)
        .gmcId(gmcId)
        .text(text)
        .createdDate(createdDate)
        .updatedDate(updatedDate)
        .build();
  }
}
