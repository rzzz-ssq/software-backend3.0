package com.cqupt.software4_backendv2.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuntimeBusCreateRequest {

    private String tablename;

    @Override
    public String toString() {
        return "RuntimeBusCreateRequest{" +
                "tablename='" + tablename + '\'' +
                ", targetcolumn=" + Arrays.toString(targetcolumn) +
                ", fea=" + Arrays.toString(fea) +
                ", K_OR=" + K_OR +
                ", K_and_pc=" + K_and_pc +
                ", K_and_sp=" + K_and_sp +
                ", knowledge=" + Arrays.toString(knowledge) +
                '}';
    }

    private String[] targetcolumn;
    private String[] fea;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private double K_OR;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private double K_and_pc;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    private double K_and_sp;
    private String[] knowledge;



}


