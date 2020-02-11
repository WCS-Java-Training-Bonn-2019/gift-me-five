import java.io.InputStream;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public class ImageController {

	@GetMapping("/image")
	public ResponseEntity<byte[]> loadImage() {
		InputStream imageAsStream = this.getClass().getResourceAsStream("mmm");
		
		byte[] imageAsByteArray =
		
		return new byte[] {};
	}
}
