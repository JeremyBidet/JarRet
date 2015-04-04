package fr.upem.jarret.server;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * This class represent the server response header.<br>
 * Get:
 * <ul>
 * 	<li>HTTP version</li>
 * 	<li>response code</li>
 * 	<li>content type</li>
 * 	<li>content length</li>
 * 	<li>content encoding charset</li>
 * </ul>
 * 
 * @author Jeremy
 */
public class ServerResponseHeader {
	
	private final List<String>  HTTP_VERSIONS = Arrays.asList("1.0", "1.1", "1.2");
	private final List<Integer> HTTP_CODE = Arrays.asList(200, 400);
	private final List<String>  HTTP_CODE_MSG = Arrays.asList("OK", "BAD REQUEST");
	
	private final String http_version;
	private final int    code;
	private final String code_msg;
	private final String content_type;
	private final String charset;
	private final int    content_length;
	
	/**
	 * Init the server response header.<br>
	 * Parse the header with a regex and set all fields, ignoring content response.
	 * @param header the header to parse
	 * @throws ServerResponseException 
	 */
	public ServerResponseHeader(String header) throws ServerResponseException {
		Pattern p = Pattern.compile(regex());
		Matcher m = p.matcher(header);
		if( ! m.matches() ) {
			throw new ServerResponseException("Server response header format is not valid !");
		}
		this.http_version   = m.group("http_version");
		this.code           = Integer.parseInt(m.group("code"));
		this.code_msg       = m.group("code_msg");
		this.content_type   = m.group("content_type");
		this.charset        = m.group("charset");
		this.content_length = Integer.parseInt(m.group("content_length"));
	}	
	
	/**
	 * Validate the server response header with values given by statements.<br>
	 * If statements are valid, method return this server response header,
	 * otherwise it throw a {@link ServerResponseException}.
	 * @return this {@linkplain ServerResponseHeader server response header}
	 * @throws ServerResponseException
	 */
	public ServerResponseHeader valid() throws ServerResponseException {
		StringBuilder exception_msg = new StringBuilder();
		if( http_version != "1.1") {
			exception_msg.append("\tHTTP version not valid : " + http_version).append('\n');
		}
		if( code != 200 ) {
			exception_msg.append("\tHTTP response code not valid : " + code).append('\n');
		}
		if( code_msg != "OK" ) {
			exception_msg.append("\tHTTP response message code not valid : " + code_msg).append('\n');
		}
		if( content_type != "application/json" ) {
			exception_msg.append("\tHTTP response content type not valid : " + content_type).append('\n');
		}
		if( charset != "utf-8" ) {
			exception_msg.append("\tHTTP response content charset not valid : " + charset).append('\n');
		}
		
		if( exception_msg.length() != 0 ) {
			throw new ServerResponseException("The server response header is not valid :\n" + exception_msg.toString());
		} else {
			return this;
		}
	}

	private String regex() {
		/**
    		HTTP/1.1 200 OK\r\n
    		Content-type: application/json; charset=utf-8\r\n
    		Content-length: 199\r\n
    		\r\n
    		... <content> ...
		**/
		return	"HTTP/(?<http_version>HTTP/" + httpVersions() + ") (?<code>" + httpCode() + ") (?<code_s>" + httpCodeMSG() + ")\r\n"
				+ "Content-type: (?<content_type>.+); charset=(?<charset>.+)\r\n"
				+ "Content-length: (?<content_length>\\d+)\r\n" 
				+ "\r\n"
				+ ".*"; // content response could be not valid cause response charset may differs from decode charset
	}
	
	private String httpVersions() {
		StringBuilder versions = HTTP_VERSIONS.stream()
				.map(s -> new StringBuilder().append(s).append('|'))
				.reduce((s1, s2) -> s1.append(s2))
				.get();
		return versions.deleteCharAt(versions.length()-1).toString();
	}
	
	private String httpCode() {
		StringBuilder codes = HTTP_CODE.stream()
				.map(s -> new StringBuilder().append(s).append('|'))
				.reduce((s1, s2) -> s1.append(s2))
				.get();
		return codes.deleteCharAt(codes.length()-1).toString();
	}
	
	private String httpCodeMSG() {
		StringBuilder codes_msg = HTTP_CODE_MSG.stream()
				.map(s -> new StringBuilder().append(s).append('|'))
				.reduce((s1, s2) -> s1.append(s2))
				.get();
		return codes_msg.deleteCharAt(codes_msg.length()-1).toString();
	}
	
	/**
	 * @return the http_version
	 */
	public String getHTTPVersion() {
		return this.http_version;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return this.code;
	}

	/**
	 * @return the code message
	 */
	public String getCodeMSG() {
		return this.code_msg;
	}

	/**
	 * @return the content type
	 */
	public String getContentType() {
		return this.content_type;
	}

	/**
	 * @return the charset
	 */
	public String getCharset() {
		return this.charset;
	}

	/**
	 * @return the content length
	 */
	public int getContentLength() {
		return this.content_length;
	}
	
}
