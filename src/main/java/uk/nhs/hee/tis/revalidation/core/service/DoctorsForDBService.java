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

import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.PageRequest.of;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.domain.Sort.by;
import static uk.nhs.hee.tis.revalidation.core.entity.UnderNotice.ON_HOLD;
import static uk.nhs.hee.tis.revalidation.core.entity.UnderNotice.YES;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.nhs.hee.tis.revalidation.core.dto.DoctorsForDbDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeCoreDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeInfoDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.core.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.core.repository.DoctorsForDBRepository;

@Transactional
@Service
public class DoctorsForDBService {


  @Value("${app.reval.pagination.pageSize}")
  private int pageSize;

  @Autowired
  private DoctorsForDBRepository doctorsRepository;

  @Autowired
  private TraineeCoreService traineeCoreService;

  public TraineeSummaryDto getAllTraineeDoctorDetails(final TraineeRequestDto requestDTO) {
    final var paginatedDoctors = getSortedAndFilteredDoctorsByPageNumber(requestDTO);
    final var doctorsList = paginatedDoctors.get().collect(toList());
    final var gmcIds =
        doctorsList.stream().map(doc -> doc.getGmcReferenceNumber()).collect(toList());
    final var traineeCoreInfo = traineeCoreService.getTraineeInformationFromCore(gmcIds);
    final var traineeDoctors = doctorsList.stream().map(d ->
        convert(d, traineeCoreInfo.get(d.getGmcReferenceNumber()))).collect(toList());

    return TraineeSummaryDto.builder()
        .traineeInfo(traineeDoctors)
        .countTotal(getCountAll())
        .countUnderNotice(getCountUnderNotice())
        .totalPages(paginatedDoctors.getTotalPages())
        .totalResults(paginatedDoctors.getTotalElements())
        .build();
  }

  public void updateTrainee(final DoctorsForDbDto gmcDoctor) {
    final DoctorsForDB doctorsForDB = DoctorsForDB.convert(gmcDoctor);
    doctorsRepository.save(doctorsForDB);
  }

  private TraineeInfoDto convert(final DoctorsForDB doctorsForDB,
      final TraineeCoreDto traineeCoreDTO) {
    final var traineeInfoDTOBuilder = TraineeInfoDto.builder()
        .gmcReferenceNumber(doctorsForDB.getGmcReferenceNumber())
        .doctorFirstName(doctorsForDB.getDoctorFirstName())
        .doctorLastName(doctorsForDB.getDoctorLastName())
        .submissionDate(doctorsForDB.getSubmissionDate())
        .dateAdded(doctorsForDB.getDateAdded())
        .underNotice(doctorsForDB.getUnderNotice().name())
        .sanction(doctorsForDB.getSanction())
        .doctorStatus(doctorsForDB.getDoctorStatus().name()) //TODO update with legacy statuses
        .lastUpdatedDate(doctorsForDB.getLastUpdatedDate());

    if (traineeCoreDTO != null) {
      traineeInfoDTOBuilder
          .cctDate(traineeCoreDTO.getCctDate())
          .programmeName(traineeCoreDTO.getProgrammeName())
          .programmeMembershipType(traineeCoreDTO.getProgrammeMembershipType())
          .currentGrade(traineeCoreDTO.getCurrentGrade());
    }

    return traineeInfoDTOBuilder.build();

  }

  private Page<DoctorsForDB> getSortedAndFilteredDoctorsByPageNumber(
      final TraineeRequestDto requestDTO) {
    final var direction = "asc".equalsIgnoreCase(requestDTO.getSortOrder()) ? ASC : DESC;
    final var pageableAndSortable =
        of(requestDTO.getPageNumber(), pageSize, by(direction, requestDTO.getSortColumn()));
    if (requestDTO.isUnderNotice()) {
      return doctorsRepository
          .findAllByUnderNoticeIn(pageableAndSortable, requestDTO.getSearchQuery(), YES, ON_HOLD);
    }

    return doctorsRepository.findAll(pageableAndSortable, requestDTO.getSearchQuery());
  }

  //TODO: explore to implement cache
  private long getCountAll() {
    return doctorsRepository.count();
  }

  //TODO: explore to implement cache
  private long getCountUnderNotice() {
    return doctorsRepository.countByUnderNoticeIn(YES, ON_HOLD);
  }
}
