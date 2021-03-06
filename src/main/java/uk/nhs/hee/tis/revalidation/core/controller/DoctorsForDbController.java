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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeRequestDto;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeSummaryDto;
import uk.nhs.hee.tis.revalidation.core.service.DoctorsForDbService;

/**
 * Controller for Doctors for DB endpoint.
 */
@Slf4j
@RestController
@Api("/api/doctors")
@RequestMapping("/api/doctors")
public class DoctorsForDbController {

  protected static final String SORT_COLUMN = "sortColumn";
  protected static final String SORT_ORDER = "sortOrder";
  protected static final String SUBMISSION_DATE = "submissionDate";
  protected static final String DESC = "desc";
  protected static final String ASC = "asc";
  protected static final String UNDER_NOTICE = "underNotice";
  protected static final String UNDER_NOTICE_VALUE = "false";
  protected static final String PAGE_NUMBER = "pageNumber";
  protected static final String PAGE_NUMBER_VALUE = "0";
  protected static final String SEARCH_QUERY = "searchQuery";
  protected static final String EMPTY_STRING = "";

  @Value("${app.validation.sort.fields}")
  private List<String> sortFields;

  @Value("${app.validation.sort.order}")
  private List<String> sortOrder;

  @Autowired
  private DoctorsForDbService doctorsForDbService;

  /**
   * GET  /api/doctors : get trainee summary.
   *
   * @param sortColumn column to be sorted
   * @param sortOrder sorting order (ASC or DESC)
   * @param underNotice filter of data to get
   * @param pageNumber page number of data to get
   * @param searchQuery search query of data to get
   * @return the ResponseEntity with status 200 (OK) and connected summary in body
   */
  @ApiOperation(value = "All trainee doctors information",
      notes = "It will return all the information about trainee doctors",
      response = TraineeSummaryDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Trainee gmc all doctors data",
          response = TraineeSummaryDto.class)})
  @GetMapping
  public ResponseEntity<TraineeSummaryDto> getTraineeDoctorsInformation(
      @RequestParam(name = SORT_COLUMN, defaultValue = SUBMISSION_DATE, required = false)
      final String sortColumn,
      @RequestParam(name = SORT_ORDER, defaultValue = DESC, required = false)
      final String sortOrder,
      @RequestParam(name = UNDER_NOTICE, defaultValue = UNDER_NOTICE_VALUE, required = false)
      final boolean underNotice,
      @RequestParam(name = PAGE_NUMBER, defaultValue = PAGE_NUMBER_VALUE, required = false)
      final int pageNumber,
      @RequestParam(name = SEARCH_QUERY, defaultValue = EMPTY_STRING, required = false)
      final String searchQuery) {
    final var traineeRequestDto = TraineeRequestDto.builder()
        .sortColumn(sortColumn)
        .sortOrder(sortOrder)
        .underNotice(underNotice)
        .pageNumber(pageNumber)
        .searchQuery(searchQuery)
        .build();

    validate(traineeRequestDto);

    final var allTraineeDoctorDetails =
        doctorsForDbService.getAllTraineeDoctorDetails(traineeRequestDto);
    return ResponseEntity.ok().body(allTraineeDoctorDetails);
  }

  //TODO: find a better way like separate validator
  private void validate(final TraineeRequestDto requestDto) {
    if (!sortFields.contains(requestDto.getSortColumn())) {
      log.warn("Invalid sort column name provided: {}, revert to default column: {}",
          requestDto.getSortColumn(), SUBMISSION_DATE);
      requestDto.setSortColumn(SUBMISSION_DATE);
    }

    if (!sortOrder.contains(requestDto.getSortOrder())) {
      log.warn("Invalid sort order provided: {}, revert to default order: {}",
          requestDto.getSortOrder(), DESC);
      requestDto.setSortOrder(DESC);
    }

  }
}
