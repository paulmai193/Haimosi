import logia.utility.string.EncryptionUtils;

public class TestEncryptString {

	public static void main(String[] args) {
		String string = "0123456789";
		String encString = EncryptionUtils.encode(string, "logia193");
		System.out.println(encString + " with length " + encString.length());

		string = EncryptionUtils.decode(encString, "logia193");
		System.out.println(string);
	}
}
