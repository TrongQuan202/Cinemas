//package org.example.project_cinemas_java.payload.converter;
//
//import com.example.bejava_cmsbatdongsan.entity.PhongBan;
//import com.example.bejava_cmsbatdongsan.payload.dto.phongbandto.PhongBanDTO;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDate;
//
//@Component
//public class PhongBanConverter {
//    public PhongBanDTO entityToDTO(PhongBan phongBan){
//        PhongBanDTO dto = new PhongBanDTO();
//        dto.setThoiGianTao(LocalDate.now());
//        dto.setThoiGianCapNhat(LocalDate.now());
//        dto.setMoTa(phongBan.getMoTa());
//        dto.setThanhVien(phongBan.getThanhVien());
//        dto.setTenPhongBan(phongBan.getTenPhongBan());
//        dto.setKhauHieu(phongBan.getKhauHieu());
//        dto.setLoaiPhongBan(phongBan.getLoaiPhongBanId());
//        dto.setTruongPhongId(phongBan.getTruongPhongId());
//        return dto;
//    }
//}
