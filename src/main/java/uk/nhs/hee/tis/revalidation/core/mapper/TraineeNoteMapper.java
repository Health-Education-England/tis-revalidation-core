package uk.nhs.hee.tis.revalidation.core.mapper;

import org.mapstruct.Mapper;
import uk.nhs.hee.tis.revalidation.core.dto.TraineeNoteDto;
import uk.nhs.hee.tis.revalidation.core.entity.TraineeNote;

@Mapper(componentModel = "spring")
public interface TraineeNoteMapper {

  TraineeNoteDto toDto(TraineeNote note);

}
