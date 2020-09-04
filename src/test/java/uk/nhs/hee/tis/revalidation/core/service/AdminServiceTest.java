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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ReflectionUtils;
import uk.nhs.hee.tis.revalidation.core.dto.AdminDto;
import uk.nhs.hee.tis.revalidation.core.mapper.AdminMapperImpl;
import uk.nhs.hee.tis.revalidation.core.mapper.util.AdminUtil;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  private AdminService service;

  @Mock
  private AWSCognitoIdentityProvider identityProvider;

  @BeforeEach
  void setUp() {
    AdminMapperImpl mapper = new AdminMapperImpl();
    Field field = ReflectionUtils.findField(AdminMapperImpl.class, "adminUtil");
    field.setAccessible(true);
    ReflectionUtils.setField(field, mapper, new AdminUtil());

    service = new AdminService(identityProvider, mapper);
  }

  @Test
  void shouldReturnEmptyListWhenNoAssignableAdminsFound() {
    // Given.
    ListUsersInGroupResult listUsersInGroupResult = new ListUsersInGroupResult();
    listUsersInGroupResult.setUsers(Collections.emptyList());

    when(identityProvider.listUsersInGroup(any())).thenReturn(listUsersInGroupResult);

    // When.
    List<AdminDto> assignableAdmins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", assignableAdmins.size(), is(0));
  }

  @Test
  void shouldReturnAdminsWhenAssignableAdminsFound() {
    // Given.
    UserType user1 = new UserType();
    user1.setUsername("user1");

    UserType user2 = new UserType();
    user2.setUsername("user2");

    AttributeType attributeType1 = new AttributeType();
    attributeType1.setName("family_name");
    attributeType1.setValue("user1");

    AttributeType attributeType2 = new AttributeType();
    attributeType2.setName("family_name");
    attributeType2.setValue("user2");

    AttributeType attributeType3 = new AttributeType();
    attributeType3.setName("given_name");
    attributeType3.setValue("user1");

    AttributeType attributeType4 = new AttributeType();
    attributeType4.setName("given_name");
    attributeType4.setValue("user2");

    AttributeType attributeType5 = new AttributeType();
    attributeType5.setName("email");
    attributeType5.setValue("user1@email.com");

    AttributeType attributeType6 = new AttributeType();
    attributeType6.setName("email");
    attributeType6.setValue("user2@email.com");

    user1.setAttributes(Arrays.asList(attributeType1, attributeType3, attributeType5));
    user2.setAttributes(Arrays.asList(attributeType2, attributeType4, attributeType6));

    ListUsersInGroupResult listUsersInGroupResult = new ListUsersInGroupResult();
    listUsersInGroupResult.setUsers(Arrays.asList(user1, user2));

    when(identityProvider.listUsersInGroup(any())).thenReturn(listUsersInGroupResult);

    // When.
    List<AdminDto> admins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", admins.size(), is(2));

    AdminDto admin = admins.get(0);
    assertThat("Unexpected username.", admin.getUsername(), is("user1"));
    assertThat("Unexpected full name.", admin.getFullName(), is("user1 user1"));
    assertThat("Unexpected email.", admin.getEmail(), is("user1@email.com"));

    admin = admins.get(1);
    assertThat("Unexpected username.", admin.getUsername(), is("user2"));
    assertThat("Unexpected full name.", admin.getFullName(), is("user2 user2"));
    assertThat("Unexpected email.", admin.getEmail(), is("user2@email.com"));
  }
}
