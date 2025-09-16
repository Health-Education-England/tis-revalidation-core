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

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupRequest;
import software.amazon.awssdk.services.cognitoidentityprovider.model.ListUsersInGroupResponse;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import uk.nhs.hee.tis.revalidation.core.dto.AdminDto;
import uk.nhs.hee.tis.revalidation.core.mapper.AdminMapper;

@Service
public class AdminService {

  @Value("${app.cognito.admin-group}")
  private String adminGroup;

  @Value("${app.cognito.admin-user-pool}")
  private String adminUserPool;

  private CognitoIdentityProviderClient identityProvider;
  private AdminMapper mapper;

  AdminService(CognitoIdentityProviderClient identityProvider, AdminMapper mapper) {
    this.identityProvider = identityProvider;
    this.mapper = mapper;
  }

  /**
   * Get a list of the admins that can be assigned to a record.
   *
   * @return A list of admins.
   */
  public List<AdminDto> getAssignableAdmins() {
    ListUsersInGroupRequest request = ListUsersInGroupRequest.builder()
        .groupName(adminGroup)
        .userPoolId(adminUserPool)
        .build();

    // TODO: A limited number of users can be returned before pagination occurs, handle pagination.
    ListUsersInGroupResponse listUsersResult = identityProvider.listUsersInGroup(request);
    List<UserType> userTypeList = listUsersResult.users();

    return mapper.toDtos(userTypeList);
  }
}
