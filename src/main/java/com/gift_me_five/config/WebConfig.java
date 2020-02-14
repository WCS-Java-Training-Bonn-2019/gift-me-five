package com.gift_me_five.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.gift_me_five.converter.MultipartFileToByteArrayConverter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	public void addFormatters(FormatterRegistry formatterRegistry) {
		formatterRegistry.addConverter(new MultipartFileToByteArrayConverter());
	}

}
