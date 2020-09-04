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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.nhs.hee.tis.revalidation.core.dto.AdminDto;
import uk.nhs.hee.tis.revalidation.core.service.AdminService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AdminController.class)
class AdminControllerTest {

  private final MockMvc mockMvc;

  @MockBean
  private AdminService service;

  @Autowired
  AdminControllerTest(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void shouldReturnEmptyListWhenNoAssignableAdminsFound() throws Exception {
    when(service.getAssignableAdmins()).thenReturn(Collections.emptyList());

    mockMvc.perform(get("/api/admins"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));
  }

  @Test
  void shouldReturnAdminsWhenAssignableAdminsFound() throws Exception {
    AdminDto admin1 = new AdminDto();
    admin1.setUsername("admin1");
    admin1.setFullName("Admin One");
    admin1.setEmail("admin1@email.com");

    AdminDto admin2 = new AdminDto();
    admin2.setUsername("admin2");
    admin2.setFullName("Admin Two");
    admin2.setEmail("admin2@email.com");

    when(service.getAssignableAdmins()).thenReturn(Arrays.asList(admin1, admin2));

    mockMvc.perform(get("/api/admins"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value("admin1"))
        .andExpect(jsonPath("$[0].fullName").value("Admin One"))
        .andExpect(jsonPath("$[0].email").value("admin1@email.com"))
        .andExpect(jsonPath("$[1].username").value("admin2"))
        .andExpect(jsonPath("$[1].fullName").value("Admin Two"))
        .andExpect(jsonPath("$[1].email").value("admin2@email.com"));
  }
}
