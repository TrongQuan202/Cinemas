package org.example.project_cinemas_java.service.iservice;

import org.example.project_cinemas_java.payload.dto.rankcustomerdtos.RankUserDTO;

public interface IRankCustomerService {
    RankUserDTO getRankOfUser(String email) throws  Exception;
}
