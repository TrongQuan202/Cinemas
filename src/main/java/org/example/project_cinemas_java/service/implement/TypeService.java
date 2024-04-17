package org.example.project_cinemas_java.service.implement;

import org.example.project_cinemas_java.exceptions.DataIntegrityViolationException;
import org.example.project_cinemas_java.model.Type;
import org.example.project_cinemas_java.repository.TypeRepo;
import org.example.project_cinemas_java.service.iservice.ITypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class TypeService implements ITypeService {
    @Autowired
    private TypeRepo typeRepo;

    @Override
    public Type createType(String typeName) throws Exception {
        if(typeRepo.existsByMovieTypeName(typeName)){
            throw new DataIntegrityViolationException("Type early exits");
        }
        Type type = Type.builder()
                .movieTypeName(typeName)
                .isActive(true)
                .build();

        return typeRepo.save(type);
    }
}
