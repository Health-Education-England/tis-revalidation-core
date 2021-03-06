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

package uk.nhs.hee.tis.revalidation.core.entity;

import static java.time.LocalDate.now;
import static uk.nhs.hee.tis.revalidation.core.entity.RecommendationStatus.NOT_STARTED;

import io.swagger.annotations.ApiModel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.nhs.hee.tis.revalidation.core.dto.DoctorsForDbDto;

/**
 * Entity for doctors information from GMC.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "doctorsForDb")
@ApiModel(description = "Trainee doctors's core profile data")
public class DoctorsForDb {

  @Id
  private String gmcReferenceNumber;
  private String doctorFirstName;
  private String doctorLastName;
  private LocalDate submissionDate;
  private LocalDate dateAdded;
  private UnderNotice underNotice;
  private String sanction;
  private RecommendationStatus doctorStatus;
  private LocalDate lastUpdatedDate;
  private String designatedBodyCode;

  /**
   * Convert doctorsForDB DTO to entity.
   *
   * @param doctorsForDbDto dto to be converted
   */
  public static final DoctorsForDb convert(final DoctorsForDbDto doctorsForDbDto) {
    return DoctorsForDb.builder()
        .gmcReferenceNumber(doctorsForDbDto.getGmcReferenceNumber())
        .doctorFirstName(doctorsForDbDto.getDoctorFirstName())
        .doctorLastName(doctorsForDbDto.getDoctorLastName())
        .submissionDate(doctorsForDbDto.getSubmissionDate())
        .dateAdded(doctorsForDbDto.getDateAdded())
        .underNotice(UnderNotice.fromString(doctorsForDbDto.getUnderNotice()))
        .sanction(doctorsForDbDto.getSanction())
        .doctorStatus(NOT_STARTED)
        .designatedBodyCode(doctorsForDbDto.getDesignatedBodyCode())
        .lastUpdatedDate(now())
        .build();
  }
}
