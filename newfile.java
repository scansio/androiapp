import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

public class Main {
	public static void main(String[] args) throws IOException {
		var path = Paths.get("textprocess.java");
		if (!Files.exists(path)) {
			System.out.println("File " + path + " doesn't exist.\nThis example sh" + 
			                   "ould be saved before running.");
			return;
		}
		String text = Files.readString(path);
		System.out.println(String.format("Input:\n%s", text));
		String out = text.lines().map(l -> new StringBuilder(l).reverse()).collect(Collectors.joining("\n"));
		System.out.println(String.format("Output:\n%s", out));
	}
}
