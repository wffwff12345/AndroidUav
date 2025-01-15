package com.example.uavapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LatLngDto {
    private double lat;
    private double lng;
    private float alt;
    private float param1;
    private float param2;
    private float param3;
    private float param4;
}
