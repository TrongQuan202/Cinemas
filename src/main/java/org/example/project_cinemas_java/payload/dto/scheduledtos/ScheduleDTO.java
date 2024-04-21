package org.example.project_cinemas_java.payload.dto.scheduledtos;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleDTO {
    private String day;
    private Set<ScheduleByDayDTO> scheduleByDayDTOSet;
}
