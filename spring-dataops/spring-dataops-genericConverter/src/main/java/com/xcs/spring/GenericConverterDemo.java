package com.xcs.spring;

import com.xcs.spring.bean.MyBean;
import com.xcs.spring.convert.AnnotatedStringToDateConverter;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.util.ReflectionUtils;

import java.util.Date;

public class GenericConverterDemo {

    public static void main(String[] args) {
        // Create a default conversion service
        DefaultConversionService service = new DefaultConversionService();
        // Add custom converters to the conversion service
        service.addConverter(new AnnotatedStringToDateConverter());

        // Define source and target types to convert a String to Date
        TypeDescriptor sourceType1 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType1 = new TypeDescriptor(ReflectionUtils.findField(MyBean.class, "date"));
        // Run the conversion
        Date date = (Date) service.convert("2023-01-01", sourceType1, targetType1);

        // Define another pair of source and target types to convert another String format to Date
        TypeDescriptor sourceType2 = TypeDescriptor.valueOf(String.class);
        TypeDescriptor targetType2 = new TypeDescriptor(ReflectionUtils.findField(MyBean.class, "dateTime"));
        // Run the conversion
        Date dateTime = (Date) service.convert("2023-01-01 23:59:59", sourceType2, targetType2);

        // Set the MyBean property using the converted Date
        MyBean myBean = new MyBean();
        myBean.setDate(date);
        myBean.setDateTime(dateTime);

        // Print the conversion result
        System.out.println("myBean = " + myBean);
    }
}
