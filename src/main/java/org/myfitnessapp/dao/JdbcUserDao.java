package org.myfitnessapp.dao;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.myfitnessapp.exception.UserNotFoundException;
import org.myfitnessapp.models.User;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@AllArgsConstructor
@Slf4j
public class JdbcUserDao implements UserDao {

    JdbcTemplate jdbcTemplate;

    @Override
    public User get(long id) {
        String sql = "select * from users where id = ?;";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, id);
        if (result.next()) {
            return mapSQLRowToUser(result);
        } else
            throw new UserNotFoundException("User with ID " + id + " does not exist.");
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "select * from users";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                users.add(mapSQLRowToUser(results));
            }
        } catch (DataAccessException e) {
            log.error("Error occurred while attempting to fetch all users. Error :" + e.getLocalizedMessage());
            throw e;
        }
        return users;
    }

    @Override
    public boolean verifyUser(String username, String password) throws EmptyResultDataAccessException {
        String sql = "select password from users where username = ?";

        try {
            String hashPassword = jdbcTemplate.queryForObject(sql, String.class, username);
            return BCrypt.checkpw(password, hashPassword);
        }

        catch (EmptyResultDataAccessException e) {
            log.error("username doesn't exist");
            return false;
        }
    }

    @Override
    public String create(User user) {
        String sql = "insert into users (email, username, password, role) values (?, ?, ?, ?) returning id;";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getUsername());
                ps.setString(3, hashPassword(user.getPassword()));
                ps.setString(4, user.getRole());
                return ps;
            }, keyHolder);

            // Get the generated ID from the KeyHolder
            return Objects.requireNonNull(keyHolder.getKey()).toString();
        } catch (DataIntegrityViolationException e) {
            log.error("username or email already exists. Returning database error: " + e.getLocalizedMessage());
            return "Username or email already exists";
        } catch (DataAccessException e) {
            return "Error occurred while attempting to update user with error: " + e.getLocalizedMessage();
        }
    }

    @Override
    public boolean update(long id, User user) {
        String sql = "update users set email = ?, username = ?, password = ?, role = ? where id = ?";
        try {
            int rowsUpdated = jdbcTemplate.update(sql, user.getEmail(), user.getUsername(), hashPassword(user.getPassword()), user.getRole(), id);
            if (rowsUpdated == 1) {
                return true;
            } else {
                throw new UserNotFoundException("User with ID " + id + " does not exist.");
            }
        } catch (DataAccessException e) {
            log.error("Error occurred while attempting to update user with error: " + e.getLocalizedMessage());
            return false;
        }
    }


    @Override
    public boolean partialUpdate(long id, Map<String, Object> updates) {
        StringBuilder sqlBuilder = new StringBuilder("UPDATE users SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : updates.entrySet()) {
            sqlBuilder.append(entry.getKey()).append(" = ?, ");
            if (entry.getKey().equals("password")) {
                params.add(hashPassword((String) entry.getValue()));
            } else
                params.add(entry.getValue());
        }

        // Remove the trailing comma and add the WHERE clause
        sqlBuilder.delete(sqlBuilder.length() - 2, sqlBuilder.length());
        sqlBuilder.append(" WHERE id = ?");
        params.add(id);

        String sql = sqlBuilder.toString();
        try {
            int result = jdbcTemplate.update(sql, params.toArray());
            if (result == 1) {
                return true;
            } else {
                throw new UserNotFoundException("User with ID " + id + " does not exist.");
            }
        } catch (DataAccessException e) {
            log.error("Error occurred while attempting to patch-update user with error: " + e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public void delete(long userId) {
        String sql = "delete from users where id = ?;";
        try {
            jdbcTemplate.update(sql, userId);
        } catch (DataAccessException e) {
            log.error("Error occurred while attempting to delete user with error: " + e.getLocalizedMessage());
        }
    }

    private User mapSQLRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }

    public String hashPassword(String plainPassword) {
        String salt = BCrypt.gensalt(12); // 12 rounds of hashing
        return BCrypt.hashpw(plainPassword, salt);
    }
}
