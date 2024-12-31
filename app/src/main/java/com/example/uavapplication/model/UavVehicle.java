package com.example.uavapplication.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 无人机信息对象 uav_vehicle
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UavVehicle implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    /** 无人机设备ID */
    private Long vehicleId;

    /** 无人机mac地址 */
    private String mac;

    /** 无人机速度 km/h */
    private Double vehicleSpeed;

    /** 无人机电量 */
    private Integer vehicleSoc;

    /** 无人机经度 */
    private Double vehicleLong;

    /** 无人机纬度 */
    private Double vehicleLat;

    /** 无人机海拔高度 */
    private Double vehicleAlt;

    /** 无人机当前模式 */
    private Long customMode;

    /** 错误信息 */
    private Integer faultInfo;

}
