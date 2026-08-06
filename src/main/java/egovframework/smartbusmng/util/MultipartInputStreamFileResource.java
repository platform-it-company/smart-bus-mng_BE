package egovframework.smartbusmng.util;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.InputStreamResource;

public class MultipartInputStreamFileResource extends InputStreamResource {

	public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
		super(inputStream);
		this.filename = filename;
	}

	private final String filename;
		
	@Override
	public String getFilename() {
		return this.filename;
	}
	
	@Override
	public long contentLength() throws IOException {
		return -1; // 우리는 길이를 알 수 없음
	}
}
