package org.example.project_cinemas_java.payload.converter;

import org.example.project_cinemas_java.model.RankCustomer;
import org.example.project_cinemas_java.payload.dto.rankcustomerdtos.RankUserDTO;
import org.springframework.stereotype.Component;

@Component
public class RankUserConverter {
    public RankUserDTO rankCustomerToRankUserDTO(RankCustomer rankCustomer){
        RankUserDTO rankUserDTO = RankUserDTO.builder()
                .name(rankCustomer.getName())
                .description(rankCustomer.getDescription())
                .point(rankCustomer.getPoint())
                .build();
        return rankUserDTO;
    }
}
