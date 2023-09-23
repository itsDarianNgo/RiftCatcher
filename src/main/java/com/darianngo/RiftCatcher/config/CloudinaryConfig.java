package com.darianngo.RiftCatcher.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Configuration
@PropertySource("classpath:application-secrets.properties")
public class CloudinaryConfig {

	@Value("${cloudinary.apikey}")
	private String apiKey;

	@Value("${cloudinary.apisecret}")
	private String apiSecret;

	@Value("${cloudinary.cloudname}")
	private String cloudName;

	@Bean
	public Cloudinary cloudinary() {
		return new Cloudinary(ObjectUtils.asMap("cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret));
	}
}
