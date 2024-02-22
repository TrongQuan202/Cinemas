package org.example.project_cinemas_java.payload.dto.userdto;

import lombok.Data;

import java.time.LocalDate;
@Data
public class UserDTO {
    private int id;
    private String tenTaiKhoan;
    private String email;
    private String soDienThoai;
    private String tenNguoiDung;
    private LocalDate ngaySinh;
}
