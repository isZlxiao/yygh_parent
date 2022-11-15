package com.atguigu.yygh.hosp.service;

        import com.atguigu.yygh.model.hosp.Hospital;
        import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
        import org.springframework.data.domain.Page;

        import java.util.List;
        import java.util.Map;

public interface HospitalService {

    // 保存医院信息
    public void saveHospital(Map<String, Object> paramMap);

    // 获取医院对象
    Hospital getHospital(String hoscode);

    // 获取分页列表
    Page<Hospital> selectPage(Integer page, Integer limit, HospitalQueryVo queryVo);

    // 更新上线状态
    void updateStatus(String id, Integer status);

    // 根据id获取医院详情
    Map<String, Object> getHospById(String id);

    // 获取医院名称
    String getHospName(String hoscode);

    // 根据医院名称获取医院列表
    List<Hospital> findByHosname(String hosname);

    // 查询医院信息详情[医院基本信息+预约规则]
    Map<String, Object> getHospInfo(String hoscode);
}
