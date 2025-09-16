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

package uk.nhs.hee.tis.revalidation.core.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import software.amazon.awssdk.services.cognitoidentityprovider.model.UserType;
import uk.nhs.hee.tis.revalidation.core.dto.AdminDto;
import uk.nhs.hee.tis.revalidation.core.mapper.util.AdminUtil;
import uk.nhs.hee.tis.revalidation.core.mapper.util.AdminUtil.Email;
import uk.nhs.hee.tis.revalidation.core.mapper.util.AdminUtil.FullName;

@Mapper(componentModel = "spring", uses = AdminUtil.class)
public interface AdminMapper {

  @Mapping(target = "fullName", source = "userType", qualifiedBy = FullName.class)
  @Mapping(target = "email", source = "userType", qualifiedBy = Email.class)
  @Mapping(target = "username", source = "userType", qualifiedBy = AdminUtil.Username.class)
  AdminDto toDto(UserType userType);

  List<AdminDto> toDtos(List<UserType> userTypes);
}
