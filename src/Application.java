import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.filechooser.*;

public class Application {

	static Connection connection;
	Statement statement = null;
	static ResultSet rs = null;
	static PreparedStatement preparedStatement;
	static String path;

	public static void main(String[] args) {

		connection = JavaConnection.ConnerDb();
		int batchSize = 20;
		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		int returnValue = fileChooser.showOpenDialog(null);

		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			path = selectedFile.getAbsolutePath();
		}

		try {

			String sql = "INSERT INTO products (id, product_id, product_name, status, category_id, inventory_id, created_at, modified_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);

			BufferedReader lineReader = new BufferedReader(new FileReader(path));
			String lineText = null;

			int count = 0;

			lineReader.readLine();

			while ((lineText = lineReader.readLine()) != null) {
				String[] data = lineText.split(",");
				String id = data[0];
				String product_id = data[1];
				String product_name = data[2];
				String status = data[3];
				String category_id = data[4];
				String inventory_id = data[5];
				String created_at = data[6];
				String modified_at = data[7];

				statement.setString(1, id);
				statement.setString(2, product_id);
				statement.setString(3, product_name);
				statement.setString(4, status);
				statement.setString(5, category_id);
				statement.setString(6, inventory_id);
				statement.setString(7, created_at);
				statement.setString(8, modified_at);

				statement.addBatch();

				if (count % batchSize == 0) {
					statement.executeBatch();
				}
			}

			lineReader.close();

			statement.executeBatch();

		} catch (IOException e) {
			System.err.println(e);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				preparedStatement.close();
			} catch (Exception e3) {

			}
		}
	}
}

