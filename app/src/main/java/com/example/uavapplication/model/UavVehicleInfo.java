package com.example.uavapplication.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 无人机基础信息管理对象 uav_vehicle_info
 * 
 * @author ruoyi
 * @date 2024-09-03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UavVehicleInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 无人机id */
    private Long vehicleId;

    /** 无人机名称 */
    private String vehicleName;

    /** 无人机mac地址 */
    private String vehicleMac;

    /** 无人机视频流地址 */
    private String vehicleRtsp;

    /** 无人机所属公司 */
    private String vehicleCompany;

    /** 无人机负责人 */
    private String vehicleCharger;

    /** 无人机负责人联系方式 */
    private String vehiclePhone;

    /** 无人机状态 0: 未连接, 1: 已连接*/
    private String vehicleStatus;

    private int sysId;

}
