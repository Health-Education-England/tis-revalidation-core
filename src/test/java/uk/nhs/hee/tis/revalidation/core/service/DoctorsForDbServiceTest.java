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
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static uk.nhs.hee.tis.revalidation.core.entity.UnderNotice.ON_HOLD;
import static uk.nhs.hee.tis.revalidation.core.entity.UnderNotice.YES;

import com.github.javafaker.Faker;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeCoreDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.core.entity.DoctorsForDb;
import uk.nhs.hee.tis.revalidation.core.entity.RecommendationStatus;
import uk.nhs.hee.tis.revalidation.core.entity.UnderNotice;
import uk.nhs.hee.tis.revalidation.core.repository.DoctorsForDbRepository;

@RunWith(MockitoJUnitRunner.class)
public class DoctorsForDbServiceTest {

  private final Faker faker = new Faker();

  @InjectMocks
  private DoctorsForDbService doctorsForDbService;

  @Mock
  private DoctorsForDbRepository repository;

  @Mock
  private TraineeCoreService traineeCoreService;

  @Mock
  private TraineeCoreDto coreDto1;

  @Mock
  private TraineeCoreDto coreDto2;

  @Mock
  private TraineeCoreDto coreDto3;

  @Mock
  private TraineeCoreDto coreDto4;

  @Mock
  private TraineeCoreDto coreDto5;

  @Mock
  private Page page;

  private DoctorsForDb doc1;
  private DoctorsForDb doc2;
  private DoctorsForDb doc3;
  private DoctorsForDb doc4;
  private DoctorsForDb doc5;
  private String gmcRef1;
  private String gmcRef2;
  private String gmcRef3;
  private String gmcRef4;
  private String gmcRef5;
  private String fname1;
  private String fname2;
  private String fname3;
  private String fname4;
  private String fname5;
  private String lname1;
  private String lname2;
  private String lname3;
  private String lname4;
  private String lname5;
  private LocalDate subDate1;
  private LocalDate subDate2;
  private LocalDate subDate3;
  private LocalDate subDate4;
  private LocalDate subDate5;
  private LocalDate addedDate1;
  private LocalDate addedDate2;
  private LocalDate addedDate3;
  private LocalDate addedDate4;
  private LocalDate addedDate5;
  private UnderNotice un1;
  private UnderNotice un2;
  private UnderNotice un3;
  private UnderNotice un4;
  private UnderNotice un5;
  private String sanction1;
  private String sanction2;
  private String sanction3;
  private String sanction4;
  private String sanction5;
  private RecommendationStatus status1;
  private RecommendationStatus status2;
  private RecommendationStatus status3;
  private RecommendationStatus status4;
  private RecommendationStatus status5;
  private LocalDate curriculumEndDate1;
  private LocalDate curriculumEndDate2;
  private LocalDate curriculumEndDate3;
  private LocalDate curriculumEndDate4;
  private LocalDate curriculumEndDate5;
  private String progName1;
  private String progName2;
  private String progName3;
  private String progName4;
  private String progName5;
  private String memType1;
  private String memType2;
  private String memType3;
  private String memType4;
  private String memType5;
  private String grade1;
  private String grade2;
  private String grade3;
  private String grade4;
  private String grade5;

  @Before
  public void setup() {
    ReflectionTestUtils.setField(doctorsForDbService, "pageSize", 20);
    setupData();
  }

  @Test
  public void shouldReturnListOfAllDoctors() {

    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    when(repository.findAll(pageableAndSortable, "")).thenReturn(page);
    when(traineeCoreService
        .getTraineeInformationFromCore(of(gmcRef1, gmcRef2, gmcRef3, gmcRef4, gmcRef5)))
        .thenReturn(
            Map.of(gmcRef1, coreDto1, gmcRef2, coreDto2, gmcRef3, coreDto3, gmcRef4, coreDto4,
                gmcRef5, coreDto5));
    when(coreDto1.getCurriculumEndDate()).thenReturn(curriculumEndDate1);
    when(coreDto1.getProgrammeName()).thenReturn(progName1);
    when(coreDto1.getProgrammeMembershipType()).thenReturn(memType1);
    when(coreDto1.getCurrentGrade()).thenReturn(grade1);

    when(coreDto2.getCurriculumEndDate()).thenReturn(curriculumEndDate2);
    when(coreDto2.getProgrammeName()).thenReturn(progName2);
    when(coreDto2.getProgrammeMembershipType()).thenReturn(memType2);
    when(coreDto2.getCurrentGrade()).thenReturn(grade2);

    when(coreDto3.getCurriculumEndDate()).thenReturn(curriculumEndDate3);
    when(coreDto3.getProgrammeName()).thenReturn(progName3);
    when(coreDto3.getProgrammeMembershipType()).thenReturn(memType3);
    when(coreDto3.getCurrentGrade()).thenReturn(grade3);

    when(coreDto4.getCurriculumEndDate()).thenReturn(curriculumEndDate4);
    when(coreDto4.getProgrammeName()).thenReturn(progName4);
    when(coreDto4.getProgrammeMembershipType()).thenReturn(memType4);
    when(coreDto4.getCurrentGrade()).thenReturn(grade4);

    when(coreDto5.getCurriculumEndDate()).thenReturn(curriculumEndDate5);
    when(coreDto5.getProgrammeName()).thenReturn(progName5);
    when(coreDto5.getProgrammeMembershipType()).thenReturn(memType5);
    when(coreDto5.getCurrentGrade()).thenReturn(grade5);

    when(page.get()).thenReturn(Stream.of(doc1, doc2, doc3, doc4, doc5));
    when(page.getTotalPages()).thenReturn(1);
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2L);
    when(repository.count()).thenReturn(5L);
    final var requestDto = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .pageNumber(1)
        .searchQuery("")
        .build();

    final var allDoctors = doctorsForDbService.getAllTraineeDoctorDetails(requestDto);

    final var doctorsForDb = allDoctors.getTraineeInfo();
    assertThat(allDoctors.getCountTotal(), is(5L));
    assertThat(allDoctors.getCountUnderNotice(), is(2L));
    assertThat(allDoctors.getTotalPages(), is(1L));
    assertThat(doctorsForDb, hasSize(5));

    assertThat(doctorsForDb.get(0).getGmcReferenceNumber(), is(gmcRef1));
    assertThat(doctorsForDb.get(0).getDoctorFirstName(), is(fname1));
    assertThat(doctorsForDb.get(0).getDoctorLastName(), is(lname1));
    assertThat(doctorsForDb.get(0).getSubmissionDate(), is(subDate1));
    assertThat(doctorsForDb.get(0).getDateAdded(), is(addedDate1));
    assertThat(doctorsForDb.get(0).getUnderNotice(), is(un1.name()));
    assertThat(doctorsForDb.get(0).getSanction(), is(sanction1));
    assertThat(doctorsForDb.get(0).getDoctorStatus(), is(status1.name()));
    assertThat(doctorsForDb.get(0).getCurriculumEndDate(), is(curriculumEndDate1));
    assertThat(doctorsForDb.get(0).getProgrammeName(), is(progName1));
    assertThat(doctorsForDb.get(0).getProgrammeMembershipType(), is(memType1));
    assertThat(doctorsForDb.get(0).getCurrentGrade(), is(grade1));

    assertThat(doctorsForDb.get(1).getGmcReferenceNumber(), is(gmcRef2));
    assertThat(doctorsForDb.get(1).getDoctorFirstName(), is(fname2));
    assertThat(doctorsForDb.get(1).getDoctorLastName(), is(lname2));
    assertThat(doctorsForDb.get(1).getSubmissionDate(), is(subDate2));
    assertThat(doctorsForDb.get(1).getDateAdded(), is(addedDate2));
    assertThat(doctorsForDb.get(1).getUnderNotice(), is(un2.name()));
    assertThat(doctorsForDb.get(1).getSanction(), is(sanction2));
    assertThat(doctorsForDb.get(1).getDoctorStatus(), is(status2.name()));
    assertThat(doctorsForDb.get(1).getCurriculumEndDate(), is(curriculumEndDate2));
    assertThat(doctorsForDb.get(1).getProgrammeName(), is(progName2));
    assertThat(doctorsForDb.get(1).getProgrammeMembershipType(), is(memType2));
    assertThat(doctorsForDb.get(1).getCurrentGrade(), is(grade2));

    assertThat(doctorsForDb.get(2).getGmcReferenceNumber(), is(gmcRef3));
    assertThat(doctorsForDb.get(2).getDoctorFirstName(), is(fname3));
    assertThat(doctorsForDb.get(2).getDoctorLastName(), is(lname3));
    assertThat(doctorsForDb.get(2).getSubmissionDate(), is(subDate3));
    assertThat(doctorsForDb.get(2).getDateAdded(), is(addedDate3));
    assertThat(doctorsForDb.get(2).getUnderNotice(), is(un3.name()));
    assertThat(doctorsForDb.get(2).getSanction(), is(sanction3));
    assertThat(doctorsForDb.get(2).getDoctorStatus(), is(status3.name()));
    assertThat(doctorsForDb.get(2).getCurriculumEndDate(), is(curriculumEndDate3));
    assertThat(doctorsForDb.get(2).getProgrammeName(), is(progName3));
    assertThat(doctorsForDb.get(2).getProgrammeMembershipType(), is(memType3));
    assertThat(doctorsForDb.get(2).getCurrentGrade(), is(grade3));

    assertThat(doctorsForDb.get(3).getGmcReferenceNumber(), is(gmcRef4));
    assertThat(doctorsForDb.get(3).getDoctorFirstName(), is(fname4));
    assertThat(doctorsForDb.get(3).getDoctorLastName(), is(lname4));
    assertThat(doctorsForDb.get(3).getSubmissionDate(), is(subDate4));
    assertThat(doctorsForDb.get(3).getDateAdded(), is(addedDate4));
    assertThat(doctorsForDb.get(3).getUnderNotice(), is(un4.name()));
    assertThat(doctorsForDb.get(3).getSanction(), is(sanction4));
    assertThat(doctorsForDb.get(3).getDoctorStatus(), is(status4.name()));
    assertThat(doctorsForDb.get(3).getCurriculumEndDate(), is(curriculumEndDate4));
    assertThat(doctorsForDb.get(3).getProgrammeName(), is(progName4));
    assertThat(doctorsForDb.get(3).getProgrammeMembershipType(), is(memType4));
    assertThat(doctorsForDb.get(3).getCurrentGrade(), is(grade4));

    assertThat(doctorsForDb.get(4).getGmcReferenceNumber(), is(gmcRef5));
    assertThat(doctorsForDb.get(4).getDoctorFirstName(), is(fname5));
    assertThat(doctorsForDb.get(4).getDoctorLastName(), is(lname5));
    assertThat(doctorsForDb.get(4).getSubmissionDate(), is(subDate5));
    assertThat(doctorsForDb.get(4).getDateAdded(), is(addedDate5));
    assertThat(doctorsForDb.get(4).getUnderNotice(), is(un5.name()));
    assertThat(doctorsForDb.get(4).getSanction(), is(sanction5));
    assertThat(doctorsForDb.get(4).getDoctorStatus(), is(status5.name()));
    assertThat(doctorsForDb.get(4).getCurriculumEndDate(), is(curriculumEndDate5));
    assertThat(doctorsForDb.get(4).getProgrammeName(), is(progName5));
    assertThat(doctorsForDb.get(4).getProgrammeMembershipType(), is(memType5));
    assertThat(doctorsForDb.get(4).getCurrentGrade(), is(grade5));
  }

  @Test
  public void shouldReturnListOfUnderNoticeDoctors() {

    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    when(repository.findAllByUnderNoticeIn(pageableAndSortable, "", YES, ON_HOLD)).thenReturn(page);
    when(traineeCoreService.getTraineeInformationFromCore(of(gmcRef1, gmcRef2)))
        .thenReturn(Map.of(gmcRef1, coreDto1, gmcRef2, coreDto2));
    when(coreDto1.getCurriculumEndDate()).thenReturn(curriculumEndDate1);
    when(coreDto1.getProgrammeName()).thenReturn(progName1);
    when(coreDto1.getProgrammeMembershipType()).thenReturn(memType1);
    when(coreDto1.getCurrentGrade()).thenReturn(grade1);

    when(coreDto2.getCurriculumEndDate()).thenReturn(curriculumEndDate2);
    when(coreDto2.getProgrammeName()).thenReturn(progName2);
    when(coreDto2.getProgrammeMembershipType()).thenReturn(memType2);
    when(coreDto2.getCurrentGrade()).thenReturn(grade2);

    when(page.get()).thenReturn(Stream.of(doc1, doc2));
    when(page.getTotalPages()).thenReturn(1);
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2L);
    when(repository.count()).thenReturn(5L);
    final var requestDto = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .underNotice(true)
        .pageNumber(1)
        .searchQuery("")
        .build();
    final var allDoctors = doctorsForDbService.getAllTraineeDoctorDetails(requestDto);
    final var doctorsForDb = allDoctors.getTraineeInfo();
    assertThat(allDoctors.getCountTotal(), is(5L));
    assertThat(allDoctors.getCountUnderNotice(), is(2L));
    assertThat(allDoctors.getTotalPages(), is(1L));
    assertThat(doctorsForDb, hasSize(2));

    assertThat(doctorsForDb.get(0).getGmcReferenceNumber(), is(gmcRef1));
    assertThat(doctorsForDb.get(0).getDoctorFirstName(), is(fname1));
    assertThat(doctorsForDb.get(0).getDoctorLastName(), is(lname1));
    assertThat(doctorsForDb.get(0).getSubmissionDate(), is(subDate1));
    assertThat(doctorsForDb.get(0).getDateAdded(), is(addedDate1));
    assertThat(doctorsForDb.get(0).getUnderNotice(), is(un1.name()));
    assertThat(doctorsForDb.get(0).getSanction(), is(sanction1));
    assertThat(doctorsForDb.get(0).getDoctorStatus(), is(status1.name()));
    assertThat(doctorsForDb.get(0).getCurriculumEndDate(), is(curriculumEndDate1));
    assertThat(doctorsForDb.get(0).getProgrammeName(), is(progName1));
    assertThat(doctorsForDb.get(0).getProgrammeMembershipType(), is(memType1));
    assertThat(doctorsForDb.get(0).getCurrentGrade(), is(grade1));

    assertThat(doctorsForDb.get(1).getGmcReferenceNumber(), is(gmcRef2));
    assertThat(doctorsForDb.get(1).getDoctorFirstName(), is(fname2));
    assertThat(doctorsForDb.get(1).getDoctorLastName(), is(lname2));
    assertThat(doctorsForDb.get(1).getSubmissionDate(), is(subDate2));
    assertThat(doctorsForDb.get(1).getDateAdded(), is(addedDate2));
    assertThat(doctorsForDb.get(1).getUnderNotice(), is(un2.name()));
    assertThat(doctorsForDb.get(1).getSanction(), is(sanction2));
    assertThat(doctorsForDb.get(1).getDoctorStatus(), is(status2.name()));
    assertThat(doctorsForDb.get(1).getCurriculumEndDate(), is(curriculumEndDate2));
    assertThat(doctorsForDb.get(1).getProgrammeName(), is(progName2));
    assertThat(doctorsForDb.get(1).getProgrammeMembershipType(), is(memType2));
    assertThat(doctorsForDb.get(1).getCurrentGrade(), is(grade2));
  }

  @Test
  public void shouldReturnEmptyListOfDoctorsWhenNoRecordFound() {
    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    when(repository.findAll(pageableAndSortable, "")).thenReturn(page);
    when(page.get()).thenReturn(Stream.of());
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(0L);
    final var requestDto = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .pageNumber(1)
        .searchQuery("")
        .build();
    final var allDoctors = doctorsForDbService.getAllTraineeDoctorDetails(requestDto);
    final var doctorsForDb = allDoctors.getTraineeInfo();
    assertThat(allDoctors.getCountTotal(), is(0L));
    assertThat(allDoctors.getCountUnderNotice(), is(0L));
    assertThat(allDoctors.getTotalPages(), is(0L));
    assertThat(doctorsForDb, hasSize(0));
  }

  @Test
  public void shouldReturnListOfAllDoctorsWhoMatchSearchQuery() {

    final Pageable pageableAndSortable = PageRequest.of(1, 20, by(DESC, "submissionDate"));
    when(repository.findAll(pageableAndSortable, "query")).thenReturn(page);
    when(traineeCoreService.getTraineeInformationFromCore(of(gmcRef1, gmcRef4)))
        .thenReturn(Map.of(gmcRef1, coreDto1, gmcRef4, coreDto4));
    when(coreDto1.getCurriculumEndDate()).thenReturn(curriculumEndDate1);
    when(coreDto1.getProgrammeName()).thenReturn(progName1);
    when(coreDto1.getProgrammeMembershipType()).thenReturn(memType1);
    when(coreDto1.getCurrentGrade()).thenReturn(grade1);

    when(coreDto4.getCurriculumEndDate()).thenReturn(curriculumEndDate4);
    when(coreDto4.getProgrammeName()).thenReturn(progName4);
    when(coreDto4.getProgrammeMembershipType()).thenReturn(memType4);
    when(coreDto4.getCurrentGrade()).thenReturn(grade4);

    when(page.get()).thenReturn(Stream.of(doc1, doc4));
    when(page.getTotalPages()).thenReturn(1);
    when(page.getTotalElements()).thenReturn(2L);
    when(repository.countByUnderNoticeIn(YES, ON_HOLD)).thenReturn(2L);
    when(repository.count()).thenReturn(5L);
    final var requestDto = TraineeRequestDto.builder()
        .sortOrder("desc")
        .sortColumn("submissionDate")
        .pageNumber(1)
        .searchQuery("query")
        .build();
    final var allDoctors = doctorsForDbService.getAllTraineeDoctorDetails(requestDto);
    final var doctorsForDb = allDoctors.getTraineeInfo();
    assertThat(allDoctors.getCountTotal(), is(5L));
    assertThat(allDoctors.getCountUnderNotice(), is(2L));
    assertThat(allDoctors.getTotalPages(), is(1L));
    assertThat(allDoctors.getTotalResults(), is(2L));
    assertThat(doctorsForDb, hasSize(2));

    assertThat(doctorsForDb.get(0).getGmcReferenceNumber(), is(gmcRef1));
    assertThat(doctorsForDb.get(0).getDoctorFirstName(), is(fname1));
    assertThat(doctorsForDb.get(0).getDoctorLastName(), is(lname1));
    assertThat(doctorsForDb.get(0).getSubmissionDate(), is(subDate1));
    assertThat(doctorsForDb.get(0).getDateAdded(), is(addedDate1));
    assertThat(doctorsForDb.get(0).getUnderNotice(), is(un1.name()));
    assertThat(doctorsForDb.get(0).getSanction(), is(sanction1));
    assertThat(doctorsForDb.get(0).getDoctorStatus(), is(status1.name()));
    assertThat(doctorsForDb.get(0).getCurriculumEndDate(), is(curriculumEndDate1));
    assertThat(doctorsForDb.get(0).getProgrammeName(), is(progName1));
    assertThat(doctorsForDb.get(0).getProgrammeMembershipType(), is(memType1));
    assertThat(doctorsForDb.get(0).getCurrentGrade(), is(grade1));

    assertThat(doctorsForDb.get(1).getGmcReferenceNumber(), is(gmcRef4));
    assertThat(doctorsForDb.get(1).getDoctorFirstName(), is(fname4));
    assertThat(doctorsForDb.get(1).getDoctorLastName(), is(lname4));
    assertThat(doctorsForDb.get(1).getSubmissionDate(), is(subDate4));
    assertThat(doctorsForDb.get(1).getDateAdded(), is(addedDate4));
    assertThat(doctorsForDb.get(1).getUnderNotice(), is(un4.name()));
    assertThat(doctorsForDb.get(1).getSanction(), is(sanction4));
    assertThat(doctorsForDb.get(1).getDoctorStatus(), is(status4.name()));
    assertThat(doctorsForDb.get(1).getCurriculumEndDate(), is(curriculumEndDate4));
    assertThat(doctorsForDb.get(1).getProgrammeName(), is(progName4));
    assertThat(doctorsForDb.get(1).getProgrammeMembershipType(), is(memType4));
    assertThat(doctorsForDb.get(1).getCurrentGrade(), is(grade4));
  }

  private void setupData() {
    gmcRef1 = faker.number().digits(8);
    gmcRef2 = faker.number().digits(8);
    gmcRef3 = faker.number().digits(8);
    gmcRef4 = faker.number().digits(8);
    gmcRef5 = faker.number().digits(8);

    fname1 = faker.name().firstName();
    fname2 = faker.name().firstName();
    fname3 = faker.name().firstName();
    fname4 = faker.name().firstName();
    fname5 = faker.name().firstName();

    lname1 = faker.name().lastName();
    lname2 = faker.name().lastName();
    lname3 = faker.name().lastName();
    lname4 = faker.name().lastName();
    lname5 = faker.name().lastName();

    subDate1 = now();
    subDate2 = now();
    subDate3 = now();
    subDate4 = now();
    subDate5 = now();

    addedDate1 = now().minusDays(5);
    addedDate2 = now().minusDays(5);
    addedDate3 = now().minusDays(5);
    addedDate4 = now().minusDays(5);
    addedDate5 = now().minusDays(5);

    un1 = faker.options().option(UnderNotice.class);
    un2 = faker.options().option(UnderNotice.class);
    un3 = faker.options().option(UnderNotice.class);
    un4 = faker.options().option(UnderNotice.class);
    un5 = faker.options().option(UnderNotice.class);

    sanction1 = faker.lorem().characters(2);
    sanction2 = faker.lorem().characters(2);
    sanction3 = faker.lorem().characters(2);
    sanction4 = faker.lorem().characters(2);
    sanction5 = faker.lorem().characters(2);

    status1 = RecommendationStatus.NOT_STARTED;
    status2 = RecommendationStatus.NOT_STARTED;
    status3 = RecommendationStatus.NOT_STARTED;
    status4 = RecommendationStatus.NOT_STARTED;
    status5 = RecommendationStatus.NOT_STARTED;

    curriculumEndDate1 = now();
    curriculumEndDate2 = now();
    curriculumEndDate3 = now();
    curriculumEndDate4 = now();
    curriculumEndDate5 = now();

    progName1 = faker.lorem().sentence(3);
    progName2 = faker.lorem().sentence(3);
    progName3 = faker.lorem().sentence(3);
    progName4 = faker.lorem().sentence(3);
    progName5 = faker.lorem().sentence(3);

    memType1 = faker.lorem().characters(8);
    memType2 = faker.lorem().characters(8);
    memType3 = faker.lorem().characters(8);
    memType4 = faker.lorem().characters(8);
    memType5 = faker.lorem().characters(8);

    grade1 = faker.lorem().characters(5);
    grade2 = faker.lorem().characters(5);
    grade3 = faker.lorem().characters(5);
    grade4 = faker.lorem().characters(5);
    grade5 = faker.lorem().characters(5);

    doc1 = new DoctorsForDb(gmcRef1, fname1, lname1, subDate1, addedDate1, un1, sanction1, status1,
        now(), "HAA");
    doc2 = new DoctorsForDb(gmcRef2, fname2, lname2, subDate2, addedDate2, un2, sanction2, status2,
        now(), "HAA");
    doc3 = new DoctorsForDb(gmcRef3, fname3, lname3, subDate3, addedDate3, un3, sanction3, status3,
        now(), "HAA");
    doc4 = new DoctorsForDb(gmcRef4, fname4, lname4, subDate4, addedDate4, un4, sanction4, status4,
        now(), "HAA");
    doc5 = new DoctorsForDb(gmcRef5, fname5, lname5, subDate5, addedDate5, un5, sanction5, status5,
        now(), "HAA");
  }
}
