/*
 * The MIT License (MIT)
 *
 * Copyright 2020 Crown Copyright (Health Education England)
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.ASC;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.DESC;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.EMPTY_STRING;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.PAGE_NUMBER;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.PAGE_NUMBER_VALUE;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.SEARCH_QUERY;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.SORT_COLUMN;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.SORT_ORDER;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.SUBMISSION_DATE;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.UNDER_NOTICE;
import static uk.nhs.hee.tis.revalidation.core.controller.DoctorsForDbController.UNDER_NOTICE_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.core.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.core.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.core.service.DoctorsForDbService;

@RunWith(SpringRunner.class)
@WebMvcTest(DoctorsForDbController.class)
public class DoctorsForDbControllerTest {

  private static final String DOCTORS_API_URL = "/api/doctors";
  private final Faker faker = new Faker();

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper mapper;

  @MockBean
  private DoctorsForDbService doctorsForDbService;

  private String gmcRef1;
  private String gmcRef2;
  private String firstName1;
  private String firstName2;
  private String lastName1;
  private String lastName2;
  private LocalDate submissionDate1;
  private LocalDate submissionDate2;
  private LocalDate dateAdded1;
  private LocalDate dateAdded2;
  private UnderNotice underNotice1;
  private UnderNotice underNotice2;
  private String sanction1;
  private String sanction2;
  private RecommendationStatus doctorStatus1;
  private RecommendationStatus doctorStatus2;

  /**
   * Set up data for testing.
   */
  @Before
  public void setup() {
    gmcRef1 = faker.number().digits(8);
    gmcRef2 = faker.number().digits(8);
    firstName1 = faker.name().firstName();
    firstName2 = faker.name().firstName();
    lastName1 = faker.name().lastName();
    lastName2 = faker.name().lastName();
    submissionDate1 = now();
    submissionDate2 = now();
    dateAdded1 = now().minusDays(5);
    dateAdded2 = now().minusDays(5);
    underNotice1 = UnderNotice.YES;
    underNotice2 = UnderNotice.ON_HOLD;
    sanction1 = faker.lorem().characters(2);
    sanction2 = faker.lorem().characters(2);
    doctorStatus1 = RecommendationStatus.STARTED;
    doctorStatus2 = RecommendationStatus.SUBMITTED_TO_GMC;
  }

  @Test
  public void shouldReturnTraineeDoctorsInformation() throws Exception {
    final var gmcDoctorDto = prepareGmcDoctor();
    final var requestDto = TraineeRequestDto.builder().sortOrder(ASC).sortColumn(SUBMISSION_DATE)
        .searchQuery(EMPTY_STRING).build();
    when(doctorsForDbService.getAllTraineeDoctorDetails(requestDto)).thenReturn(gmcDoctorDto);
    this.mockMvc.perform(get(DOCTORS_API_URL)
        .param(SORT_ORDER, ASC)
        .param(SORT_COLUMN, SUBMISSION_DATE)
        .param(UNDER_NOTICE, UNDER_NOTICE_VALUE)
        .param(PAGE_NUMBER, PAGE_NUMBER_VALUE)
        .param(SEARCH_QUERY, EMPTY_STRING))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDto)));
  }

  @Test
  public void shouldReturnDataWhenSortOrderAndSortColumnAreEmpty() throws Exception {
    final var gmcDoctorDto = prepareGmcDoctor();
    final var requestDto = TraineeRequestDto.builder().sortOrder(DESC).sortColumn(SUBMISSION_DATE)
        .searchQuery(EMPTY_STRING).build();
    when(doctorsForDbService.getAllTraineeDoctorDetails(requestDto)).thenReturn(gmcDoctorDto);
    this.mockMvc.perform(get(DOCTORS_API_URL)
        .param(SORT_ORDER, "")
        .param(SORT_COLUMN, ""))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDto)));
  }

  @Test
  public void shouldReturnDataWhenSortOrderAndSortColumnAreInvalid() throws Exception {
    final var gmcDoctorDto = prepareGmcDoctor();
    final var requestDto = TraineeRequestDto.builder().sortOrder(DESC).sortColumn(SUBMISSION_DATE)
        .searchQuery(EMPTY_STRING).build();
    when(doctorsForDbService.getAllTraineeDoctorDetails(requestDto)).thenReturn(gmcDoctorDto);
    this.mockMvc.perform(get(DOCTORS_API_URL)
        .param(SORT_ORDER, "aa")
        .param(SORT_COLUMN, "date"))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDto)));
  }

  @Test
  public void shouldReturnUnderNoticeTraineeDoctorsInformation() throws Exception {
    final var gmcDoctorDto = prepareGmcDoctor();
    final var requestDto = TraineeRequestDto.builder()
        .sortOrder(ASC).sortColumn(SUBMISSION_DATE).underNotice(true).searchQuery(EMPTY_STRING)
        .build();
    when(doctorsForDbService.getAllTraineeDoctorDetails(requestDto)).thenReturn(gmcDoctorDto);
    this.mockMvc.perform(get(DOCTORS_API_URL)
        .param(SORT_ORDER, ASC)
        .param(SORT_COLUMN, SUBMISSION_DATE)
        .param(UNDER_NOTICE, String.valueOf(true)))
        .andExpect(status().isOk())
        .andExpect(content().json(mapper.writeValueAsString(gmcDoctorDto)));
  }

  private TraineeSummaryDto prepareGmcDoctor() {
    final var doctorsForDb = buildDoctorsForDbList();
    return TraineeSummaryDto.builder()
        .traineeInfo(doctorsForDb)
        .countTotal(doctorsForDb.size())
        .countUnderNotice(1L)
        .build();
  }

  private List<TraineeInfoDto> buildDoctorsForDbList() {
    final var doctor1 = TraineeInfoDto.builder()
        .gmcReferenceNumber(gmcRef1)
        .doctorFirstName(firstName1)
        .doctorLastName(lastName1)
        .submissionDate(submissionDate1)
        .dateAdded(dateAdded1)
        .underNotice(underNotice1.value())
        .sanction(sanction1)
        .doctorStatus(doctorStatus1.name())
        .build();

    final var doctor2 = TraineeInfoDto.builder()
        .gmcReferenceNumber(gmcRef2)
        .doctorFirstName(firstName2)
        .doctorLastName(lastName2)
        .submissionDate(submissionDate2)
        .dateAdded(dateAdded2)
        .underNotice(underNotice2.value())
        .sanction(sanction2)
        .doctorStatus(doctorStatus2.name())
        .build();
    return of(doctor1, doctor2);
  }
}
