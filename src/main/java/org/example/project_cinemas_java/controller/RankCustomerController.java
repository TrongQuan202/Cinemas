package org.example.project_cinemas_java.controller;

import lombok.RequiredArgsConstructor;
import org.example.project_cinemas_java.exceptions.DataNotFoundException;
import org.example.project_cinemas_java.payload.dto.promotiondtos.PromotionDTO;
import org.example.project_cinemas_java.payload.dto.rankcustomerdtos.RankUserDTO;
import org.example.project_cinemas_java.service.implement.RankCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/rank")
@RequiredArgsConstructor
public class RankCustomerController {
    @Autowired
    private RankCustomerService rankCustomerService;

    @GetMapping("/get-rank-by-user")
    public ResponseEntity<?> getRankOfUser(@RequestParam String email){
        try {
            RankUserDTO rankUserDTO = rankCustomerService.getRankOfUser(email);
            return ResponseEntity.ok().body(rankUserDTO);
        }catch (DataNotFoundException ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
