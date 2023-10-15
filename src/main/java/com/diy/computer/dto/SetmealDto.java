package com.diy.computer.dto;

import com.diy.computer.entity.Setmeal;
import com.diy.computer.entity.SetmealPart;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealPart> setmealParts;

    private String categoryName;
}
