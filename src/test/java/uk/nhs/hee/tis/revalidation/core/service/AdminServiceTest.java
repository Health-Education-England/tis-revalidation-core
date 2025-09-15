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
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AttributeType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import uk.nhs.hee.tis.revalidation.core.dto.AdminDto;
import uk.nhs.hee.tis.revalidation.core.mapper.AdminMapperImpl;
import uk.nhs.hee.tis.revalidation.core.mapper.util.AdminUtil;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

  private AdminService service;

  @Mock
  private CognitoIdentityProviderClient identityProvider;

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
    ListUsersInGroupResponse listUsersInGroupResult = ListUsersInGroupResponse.builder()
        .build();
    listUsersInGroupResult = listUsersInGroupResult.toBuilder().users(Collections.emptyList())
        .build();

    when(identityProvider.listUsersInGroup(any(ListUsersInGroupRequest.class))).thenReturn(
        listUsersInGroupResult);

    // When.
    List<AdminDto> assignableAdmins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", assignableAdmins.size(), is(0));
  }

  @Test
  void shouldReturnAdminsWhenAssignableAdminsFound() {
    // Given.
    UserType user1 = UserType.builder()
        .build();
    user1 = user1.toBuilder().username("user1").build();

    UserType user2 = UserType.builder()
        .build();
    user2 = user2.toBuilder().username("user2").build();

    AttributeType attributeType1 = AttributeType.builder()
        .build();
    attributeType1 = attributeType1.toBuilder().name("family_name").build();
    attributeType1 = attributeType1.toBuilder().value("user1").build();

    AttributeType attributeType2 = AttributeType.builder()
        .build();
    attributeType2 = attributeType2.toBuilder().name("family_name").build();
    attributeType2 = attributeType2.toBuilder().value("user2").build();

    AttributeType attributeType3 = AttributeType.builder()
        .build();
    attributeType3 = attributeType3.toBuilder().name("given_name").build();
    attributeType3 = attributeType3.toBuilder().value("user1").build();

    AttributeType attributeType4 = AttributeType.builder()
        .build();
    attributeType4 = attributeType4.toBuilder().name("given_name").build();
    attributeType4 = attributeType4.toBuilder().value("user2").build();

    AttributeType attributeType5 = AttributeType.builder()
        .build();
    attributeType5 = attributeType5.toBuilder().name("email").build();
    attributeType5 = attributeType5.toBuilder().value("user1@email.com").build();

    AttributeType attributeType6 = AttributeType.builder()
        .build();
    attributeType6 = attributeType6.toBuilder().name("email").build();
    attributeType6 = attributeType6.toBuilder().value("user2@email.com").build();

    user1 = user1.toBuilder()
        .attributes(Arrays.asList(attributeType1, attributeType3, attributeType5)).build();
    user2 = user2.toBuilder()
        .attributes(Arrays.asList(attributeType2, attributeType4, attributeType6)).build();

    ListUsersInGroupResponse listUsersInGroupResult = ListUsersInGroupResponse.builder()
        .build();
    listUsersInGroupResult = listUsersInGroupResult.toBuilder().users(Arrays.asList(user1, user2))
        .build();

    when(identityProvider.listUsersInGroup(any(ListUsersInGroupRequest.class))).thenReturn(
        listUsersInGroupResult);

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
