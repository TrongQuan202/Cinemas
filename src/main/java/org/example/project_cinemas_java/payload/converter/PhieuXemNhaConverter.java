//package org.example.project_cinemas_java.payload.converter;
//
//import com.example.bejava_cmsbatdongsan.entity.PhieuXemNha;
//import com.example.bejava_cmsbatdongsan.payload.dto.phieuxemnhadto.PhieuXemNhaDTO;
//import org.springframework.stereotype.Component;
//
//@Component
//public class PhieuXemNhaConverter {
//    public PhieuXemNhaDTO entityToDTO(PhieuXemNha phieuXemNha){
//        PhieuXemNhaDTO dto = PhieuXemNhaDTO
//                .builder()
//                .id(phieuXemNha.getId())
//                .thoiGianTao(phieuXemNha.getThoiGianTao())
//                .sanPhamId(phieuXemNha.getSanPhamId())
//                .tenKhachHang(phieuXemNha.getTenKhachHang())
//                .soDienThoaiKH(phieuXemNha.getSdtKhachHang())
//                .moTa(phieuXemNha.getMoTa())
//                .khachHangId(phieuXemNha.getKhachHangId())
//                .banThanhCong(phieuXemNha.isBanThanhCong())
//                .build();
//        return dto;
//    }
//}
