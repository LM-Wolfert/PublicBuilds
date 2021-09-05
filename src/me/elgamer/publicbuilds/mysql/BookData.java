package me.elgamer.publicbuilds.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

public class BookData {

	DataSource dataSource;

	public BookData(DataSource dataSource) {

		this.dataSource = dataSource;

	}

	private Connection conn() throws SQLException {
		return dataSource.getConnection();
	}

	public int newBookID() {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT MAX(book) as book FROM book_data;"
				)){

			ResultSet results = statement.executeQuery();

			if (results.next()) {
				return results.getInt("book");		
			} else { return 1;}

		} catch (SQLException sql) {
			sql.printStackTrace();
			return 1;
		}
	}

	public boolean addPage(int book, int page, String text) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"INSERT INTO book_data(book, page, text) VALUES(?, ?, ?);"
				)){
			statement.setInt(1, book);
			statement.setInt(2, page);
			statement.setString(3, text);
			statement.executeUpdate();

			return true;

		} catch (SQLException sql) {
			sql.printStackTrace();
			return false;
		}
	}

	public List<String> getPages(int id) {

		try (Connection conn = conn(); PreparedStatement statement = conn.prepareStatement(
				"SELECT text FROM book_data WHERE id = ?;"
				)){
			statement.setInt(1, id);

			ResultSet results = statement.executeQuery();
			List<String> pages = new ArrayList<String>();

			while (results.next()) {
				pages.add(results.getString("text"));
			}

			return pages;


		} catch (SQLException sql) {
			sql.printStackTrace();
			return null;
		}


	}

}
