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

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.HttpMethod.GET;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeCoreDto;

@Slf4j
@Service
public class TraineeCoreService {

  @Autowired
  private RestTemplate restTemplate;

  @Value("${app.reval.tcs.url}")
  private String tcsUrl;

  public Map<String, TraineeCoreDto> getTraineeInformationFromCore(final List<String> gmcIds) {
    log.info("Fetching trainee core info from TCS for GmcId: {}", gmcIds);
    if (!gmcIds.isEmpty()) {
      final var gmcId = gmcIds.stream().collect(joining(","));
      final var requestUrl = format("%s/%s", tcsUrl, gmcId);
      log.debug("Tcs url to fetch core information: {}", requestUrl);
      Map<String, TraineeCoreDto> traineeCoreDtos = Map.of();
      try {
        traineeCoreDtos = restTemplate
            .exchange(requestUrl, GET, null,
                new ParameterizedTypeReference<Map<String, TraineeCoreDto>>() {
                }).getBody();

      } catch (final HttpStatusCodeException exception) {
        final var statusCode = exception.getStatusCode().value();
        log.error("Fail to connect to TCS service. Status code: {}", statusCode, exception);
      } catch (final Exception e) {
        log.error("Fail to connect to TCS service", e);
      }

      return traineeCoreDtos;
    }

    return Map.of();
  }
}
