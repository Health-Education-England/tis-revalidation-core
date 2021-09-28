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

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNotesDto;
import uk.nhs.hee.tis.revalidation.core.entity.TraineeNote;
import uk.nhs.hee.tis.revalidation.core.service.TraineeNotesService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TraineeNotesController.class)
class TraineeNotesControllerTest {

  private final Faker faker = new Faker();
  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper mapper;
  @MockBean
  private TraineeNotesService traineeNotesService;
  private TraineeNote traineeNote;
  private String id;
  private String gmcId;
  private String text;
  private List<TraineeNote> notes;

  /**
   * Set up data for testing.
   */
  @BeforeEach
  public void setup() {
    id = faker.number().digits(11);
    gmcId = faker.number().digits(8);
    text = faker.lorem().characters();

    traineeNote = TraineeNote.builder()
        .id(id)
        .gmcId(gmcId)
        .text(text)
        .createdDate(now())
        .updatedDate(now())
        .build();
    notes = List.of(traineeNote);
  }

  @Test
  void shouldReturnAllNotesForATrainee() throws Exception {
    when(traineeNotesService.getTraineeNotes(gmcId)).thenReturn(prepareTraineeNotesDto());
    this.mockMvc.perform(get("/api/trainee/{gmcId}/notes", gmcId))
        .andExpect(status().isOk())
        .andDo(print())
        .andExpect(content().json(mapper.writeValueAsString(prepareTraineeNotesDto())));

  }

  private TraineeNotesDto prepareTraineeNotesDto() {
    return TraineeNotesDto.builder()
        .gmcId(gmcId)
        .notes(notes)
        .build();
  }

}
