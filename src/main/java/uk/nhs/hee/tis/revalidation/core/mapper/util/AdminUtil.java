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

package uk.nhs.hee.tis.revalidation.core.mapper.util;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.mapstruct.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class AdminUtil {

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface Email {

  }

  @Qualifier
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface FullName {

  }

  @Email
  public String email(List<AttributeType> attributes) {
    AttributeType emailAttributeType = attributes.stream()
        .filter(attributeType -> attributeType.getName().equals("email")).findFirst().orElse(null);

    return emailAttributeType != null ? emailAttributeType.getValue() : "";
  }

  @FullName
  public String fullName(List<AttributeType> attributes) {
    String familyName = "";
    String givenName = "";

    for (AttributeType attribute : attributes) {
      if (attribute.getName().equals("family_name")) {
        familyName = attribute.getValue();
      }
      if (attribute.getName().equals("given_name")) {
        givenName = attribute.getValue();
      }
    }

    return givenName + " " + familyName;
  }
}
