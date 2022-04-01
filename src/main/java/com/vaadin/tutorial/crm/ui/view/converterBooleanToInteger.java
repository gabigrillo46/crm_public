package com.vaadin.tutorial.crm.ui.view;


import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;

public class converterBooleanToInteger implements Converter<Boolean, Integer> {


    @Override
    public Result<Integer> convertToModel(Boolean value, ValueContext context) {
        if(value)
        {
            return Result.ok(1);
        }
        else
        {
            return Result.ok(0);
        }
    }

    @Override
    public Boolean convertToPresentation(Integer value, ValueContext context) {
        if (value==1)
        {
            return true;
        }
        else
        {
            return false;
        }

    }


}
