import logia.utility.string.EncryptionUtil;

public class TestEncryptString {

	public static void main(String[] args) {
		String string = "4242424242424242";
		String encString = EncryptionUtil.encode(string, "haimosiv1.0");
		System.out.println(encString + " with length " + encString.length());

		string = EncryptionUtil.decode(encString, "haimosiv1.0");
		System.out.println(string);

		String s = "ch_16l8xUBEvZ6wV7rN8ilP9NLX";
		System.out.println(s.length());
	}
}
