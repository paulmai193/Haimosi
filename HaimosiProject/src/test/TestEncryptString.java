import logia.utility.string.EncryptionUtil;

public class TestEncryptString {

	public static void main(String[] args) {
		String string = "0123456789";
		String encString = EncryptionUtil.encode(string, "logia193");
		System.out.println(encString + " with length " + encString.length());

		string = EncryptionUtil.decode(encString, "logia193");
		System.out.println(string);
	}
}
