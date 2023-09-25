package com.darianngo.RiftCatcher.services;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {

	@Autowired
	private Cloudinary cloudinary;

	public String uploadFile(byte[] fileBytes, String folderPath, String[] tags) throws IOException {
		Map<?, ?> result = cloudinary.uploader().upload(fileBytes,
				ObjectUtils.asMap("folder", folderPath, "tags", String.join(",", tags)));
		return result.get("url").toString();
	}

//	public boolean imageWithTagExists(String... tags) {
//		try {
//			// Construct the search expression for all tags
//			String expression = String.join(" AND ", (CharSequence[]) tags); // Convert tags to a search expression
//			Map<?, ?> result = cloudinary.search().expression(expression).maxResults(1).execute();
//			Object totalCount = result.get("total_count");
//			return totalCount != null && (int) totalCount > 0;
//		} catch (Exception e) {
//			// Log or print the error message for diagnostics
//			e.printStackTrace();
//			return false; // Default behavior
//		}
//	}
}
