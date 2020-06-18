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

package uk.nhs.hee.tis.revalidation.core.repository;

import static java.util.List.of;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.hee.tis.revalidation.core.entity.DoctorsForDB;
import uk.nhs.hee.tis.revalidation.core.entity.UnderNotice;

@Repository
public interface DoctorsForDBRepository extends MongoRepository<DoctorsForDB, String> {

  //Get count for trainee doctors who are underNotice
  long countByUnderNoticeIn(final UnderNotice... underNotice);

  //Get trainee doctors as part of search results (search by first name or last name or gmc number).
  //TODO: look at the query option too
  Page<DoctorsForDB> findByDoctorFirstNameIgnoreCaseLikeOrDoctorLastNameIgnoreCaseLikeOrGmcReferenceNumberIgnoreCaseLike(
      final Pageable pageable,
      final String firstName,
      final String lastName,
      final String gmcNumber);

  //Get under notice trainee doctors as part of search results (search by first name or last name or gmc number).
  //TODO: look at the query option too
  Page<DoctorsForDB> findByDoctorFirstNameIgnoreCaseLikeAndUnderNoticeInOrDoctorLastNameIgnoreCaseLikeAndUnderNoticeInOrGmcReferenceNumberIgnoreCaseLikeAndUnderNoticeIn(
      final Pageable pageable,
      final String firstName,
      final List<UnderNotice> un1,
      final String lastName,
      final List<UnderNotice> un2,
      final String gmcNumber,
      final List<UnderNotice> un3);


  //A wrapper search method of doctors to make it easy for use.
  default Page<DoctorsForDB> findAll(final Pageable pageable, final String searchQuery) {
    return findByDoctorFirstNameIgnoreCaseLikeOrDoctorLastNameIgnoreCaseLikeOrGmcReferenceNumberIgnoreCaseLike(
        pageable,
        searchQuery,
        searchQuery,
        searchQuery);
  }

  //A wrapper search method of under notice doctors to make it easy for use.
  default Page<DoctorsForDB> findAllByUnderNoticeIn(final Pageable pageable,
      final String searchQuery, final UnderNotice... underNotice) {
    return findByDoctorFirstNameIgnoreCaseLikeAndUnderNoticeInOrDoctorLastNameIgnoreCaseLikeAndUnderNoticeInOrGmcReferenceNumberIgnoreCaseLikeAndUnderNoticeIn(
        pageable,
        searchQuery,
        of(underNotice),
        searchQuery,
        of(underNotice),
        searchQuery,
        of(underNotice));
  }
}
