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
  private CognitoIdentityProviderClient cognitoIdentityProviderClient;

  @BeforeEach
  void setUp() {
    AdminMapperImpl mapper = new AdminMapperImpl();
    Field field = ReflectionUtils.findField(AdminMapperImpl.class, "adminUtil");
    field.setAccessible(true);
    ReflectionUtils.setField(field, mapper, new AdminUtil());

    service = new AdminService(cognitoIdentityProviderClient, mapper);
  }

  @Test
  void shouldReturnEmptyListWhenNoAssignableAdminsFound() {
    // Given.
    ListUsersInGroupResponse listUsersInGroupResponse = ListUsersInGroupResponse.builder()
        .users(Collections.emptyList())
        .build();

    when(cognitoIdentityProviderClient.listUsersInGroup(
        any(ListUsersInGroupRequest.class))).thenReturn(
        listUsersInGroupResponse);

    // When.
    List<AdminDto> assignableAdmins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", assignableAdmins.size(), is(0));
  }

  @Test
  void shouldReturnAdminsWhenAssignableAdminsFound() {
    // Given.
    UserType user1 = buildUser(1);
    UserType user2 = buildUser(2);

    ListUsersInGroupResponse listUsersInGroupResponse = ListUsersInGroupResponse.builder()
        .users(List.of(user1, user2))
        .build();

    when(cognitoIdentityProviderClient.listUsersInGroup(
        any(ListUsersInGroupRequest.class))).thenReturn(
        listUsersInGroupResponse);

    // When.
    List<AdminDto> admins = service.getAssignableAdmins();

    // Then.
    assertThat("Unexpected number of admins.", admins.size(), is(2));

    AdminDto admin = admins.get(0);
    assertThat("Unexpected username.", admin.getUsername(), is("username1"));
    assertThat("Unexpected full name.", admin.getFullName(), is("user1 user1"));
    assertThat("Unexpected email.", admin.getEmail(), is("user1@tis.nhs.uk"));

    admin = admins.get(1);
    assertThat("Unexpected username.", admin.getUsername(), is("username2"));
    assertThat("Unexpected full name.", admin.getFullName(), is("user2 user2"));
    assertThat("Unexpected email.", admin.getEmail(), is("user2@tis.nhs.uk"));
  }

  private UserType buildUser(int id) {
    return UserType.builder()
        .username(String.format("username%d", id))
        .attributes(List.of(
            buildAttributeType("family_name", String.format("user%d", id)),
            buildAttributeType("given_name", String.format("user%d", id)),
            buildAttributeType("email", String.format("user%d@tis.nhs.uk", id))
        ))
        .build();
  }

  private AttributeType buildAttributeType(String name, String value) {
    return AttributeType.builder().name(name).value(value).build();
  }
}
