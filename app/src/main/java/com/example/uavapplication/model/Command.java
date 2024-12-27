package com.example.uavapplication.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Command  implements java.io.Serializable{
    private static final long serialVersionUID = 1L;

    private int sysId;

    private int command;

    private float param1;

    private float param2;

    private float param3;

    private float param4;

    private float param5;

    private float param6;

    private float param7;
}
