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

package uk.nhs.hee.tis.revalidation.core.service;

import static java.time.LocalDate.now;
import static java.util.List.of;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeCoreDto;

@RunWith(MockitoJUnitRunner.class)
public class TraineeCoreServiceTest {

  private static final String API_REVALIDATION = "/api/revalidation";
  private final Faker faker = new Faker();

  @InjectMocks
  private TraineeCoreService traineeCoreService;

  @Mock
  private RestTemplate restTemplate;

  @Mock
  private ResponseEntity responseEntity;

  private String gmcId1, gmcId2;
  private LocalDate cctDate1, cctDate2;
  private String programmeMembershipType1, programmeMembershipType2;
  private String programmeName1, programmeName2;
  private String currentGrade1, currentGrade2;
  private TraineeCoreDto trainee1, trainee2;

  @Before
  public void setup() {
    ReflectionTestUtils.setField(traineeCoreService, "tcsUrl", API_REVALIDATION);
    setupData();
  }

  @Test
  public void shouldFetchTraineeInformationFromTcs() {
    final var url = String.format("%s/%s", API_REVALIDATION, gmcId1);
    when(restTemplate.exchange(url, HttpMethod.GET, null,
        new ParameterizedTypeReference<Map<String, TraineeCoreDto>>() {
        })).thenReturn(responseEntity);
    when(responseEntity.getBody()).thenReturn(Map.of(gmcId1, trainee1));
    final var traineeInformationFromCore =
        traineeCoreService.getTraineeInformationFromCore(of(gmcId1));
    assertThat(traineeInformationFromCore.size(), is(1));

    final var traineeCoreDTO = traineeInformationFromCore.get(gmcId1);
    assertThat(traineeCoreDTO, is(notNullValue()));
    assertThat(traineeCoreDTO.getGmcId(), is(gmcId1));
    assertThat(traineeCoreDTO.getCctDate(), is(cctDate1));
    assertThat(traineeCoreDTO.getCurrentGrade(), is(currentGrade1));
    assertThat(traineeCoreDTO.getProgrammeMembershipType(), is(programmeMembershipType1));
    assertThat(traineeCoreDTO.getProgrammeName(), is(programmeName1));
  }

  @Test
  public void shouldFetchMultipleTraineeInformationFromTcs() {
    final var url = String.format("%s/%s,%s", API_REVALIDATION, gmcId1, gmcId2);
    when(restTemplate.exchange(url, HttpMethod.GET, null,
        new ParameterizedTypeReference<Map<String, TraineeCoreDto>>() {
        })).thenReturn(responseEntity);
    when(responseEntity.getBody()).thenReturn(Map.of(gmcId1, trainee1, gmcId2, trainee2));
    final var traineeInformationFromCore =
        traineeCoreService.getTraineeInformationFromCore(of(gmcId1, gmcId2));
    assertThat(traineeInformationFromCore.size(), is(2));

    var traineeCoreDTO = traineeInformationFromCore.get(gmcId1);
    assertThat(traineeCoreDTO, is(notNullValue()));
    assertThat(traineeCoreDTO.getGmcId(), is(gmcId1));
    assertThat(traineeCoreDTO.getCctDate(), is(cctDate1));
    assertThat(traineeCoreDTO.getCurrentGrade(), is(currentGrade1));
    assertThat(traineeCoreDTO.getProgrammeMembershipType(), is(programmeMembershipType1));
    assertThat(traineeCoreDTO.getProgrammeName(), is(programmeName1));

    traineeCoreDTO = traineeInformationFromCore.get(gmcId2);
    assertThat(traineeCoreDTO, is(notNullValue()));
    assertThat(traineeCoreDTO.getGmcId(), is(gmcId2));
    assertThat(traineeCoreDTO.getCctDate(), is(cctDate2));
    assertThat(traineeCoreDTO.getCurrentGrade(), is(currentGrade2));
    assertThat(traineeCoreDTO.getProgrammeMembershipType(), is(programmeMembershipType2));
    assertThat(traineeCoreDTO.getProgrammeName(), is(programmeName2));
  }

  @Test
  public void shouldReturnEmptyWhenNoRecordFound() {
    final var url = String.format("%s/%s", API_REVALIDATION, gmcId1);
    when(restTemplate.exchange(url, HttpMethod.GET, null,
        new ParameterizedTypeReference<Map<String, TraineeCoreDto>>() {
        })).thenReturn(responseEntity);
    when(responseEntity.getBody()).thenReturn(Map.of());
    final var traineeInformationFromCore =
        traineeCoreService.getTraineeInformationFromCore(of(gmcId1));
    assertThat(traineeInformationFromCore.size(), is(0));
  }

  public void setupData() {
    gmcId1 = faker.number().digits(8);
    gmcId2 = faker.number().digits(8);

    cctDate1 = now();
    cctDate2 = now();

    programmeMembershipType1 = faker.lorem().characters(10);
    programmeMembershipType2 = faker.lorem().characters(10);

    programmeName1 = faker.lorem().sentence(3);
    programmeName2 = faker.lorem().sentence(3);

    currentGrade1 = faker.lorem().characters(5);
    currentGrade2 = faker.lorem().characters(5);

    trainee1 = new TraineeCoreDto(gmcId1, cctDate1, programmeMembershipType1, programmeName1,
        currentGrade1);
    trainee2 = new TraineeCoreDto(gmcId2, cctDate2, programmeMembershipType2, programmeName2,
        currentGrade2);
  }
}
