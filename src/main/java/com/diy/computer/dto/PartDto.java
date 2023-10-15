package com.diy.computer.dto;


import com.diy.computer.entity.Part;
import com.diy.computer.entity.PartAfter;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PartDto extends Part {

    private List<PartAfter> afters = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
