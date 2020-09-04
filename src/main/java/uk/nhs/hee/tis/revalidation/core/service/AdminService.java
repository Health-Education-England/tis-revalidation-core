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

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersInGroupResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.hee.tis.revalidation.core.dto.AdminDto;
import uk.nhs.hee.tis.revalidation.core.mapper.AdminMapper;

@Service
public class AdminService {

  @Value("${app.cognito.admin-group}")
  private String adminGroup;

  @Value("${app.cognito.admin-user-pool}")
  private String adminUserPool;

  private AWSCognitoIdentityProvider identityProvider;
  private AdminMapper mapper;

  AdminService(AWSCognitoIdentityProvider identityProvider, AdminMapper mapper) {
    this.identityProvider = identityProvider;
    this.mapper = mapper;
  }

  /**
   * Get a list of the admins that can be assigned to a record.
   *
   * @return A list of admins.
   */
  public List<AdminDto> getAssignableAdmins() {
    ListUsersInGroupRequest request = new ListUsersInGroupRequest();
    request.setGroupName(adminGroup);
    request.setUserPoolId(adminUserPool);

    // TODO: A limited number of users can be returned before pagination occurs, handle pagination.
    ListUsersInGroupResult listUsersResult = identityProvider.listUsersInGroup(request);
    List<UserType> userTypeList = listUsersResult.getUsers();

    return buildAdminDtoList(userTypeList);

  }

  private List<AdminDto> buildAdminDtoList(List<UserType> userTypeList) {
    return userTypeList.stream().map(userType -> {
      List<AttributeType> userAttributes = userType.getAttributes();
      AdminDto adminDto = new AdminDto();
      adminDto.setUsername(userType.getUsername());
      adminDto.setFullName(getUserFullName(userAttributes));
      adminDto.setEmail(getUserEmail(userAttributes));
      return adminDto;
    }).collect(Collectors.toList());
  }

  private String getUserFullName(List<AttributeType> attributeTypeList) {
    String familyName = "";
    String givenName = "";
    for (AttributeType attributeType : attributeTypeList) {
      if (attributeType.getName().equals("family_name")) {
        familyName = attributeType.getValue();
      }
      if (attributeType.getName().equals("given_name")) {
        givenName = attributeType.getValue();
      }
    }
    return givenName + " " + familyName;
  }

  private String getUserEmail(List<AttributeType> attributeTypeList) {
    AttributeType emailAttributeType = attributeTypeList.stream().filter(attributeType -> attributeType.getName().equals("email")).findFirst().orElse(null);

    return emailAttributeType != null ? emailAttributeType.getValue() : "";
  }
}
